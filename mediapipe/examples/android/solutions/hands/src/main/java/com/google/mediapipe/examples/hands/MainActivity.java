// Copyright 2021 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.mediapipe.examples.hands;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
// ContentResolver dependency
import com.google.mediapipe.formats.proto.LandmarkProto.Landmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;


/** Main activity of MediaPipe Hands app. */
public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private Hands hands;
  // Run the pipeline and the model inference on GPU or CPU.
  private static final boolean RUN_ON_GPU = true;

  private enum InputSource {
    UNKNOWN,
    CAMERA,
  }
  private InputSource inputSource = InputSource.UNKNOWN;

  private CameraInput cameraInput;
  private SolutionGlSurfaceView<HandsResult> glSurfaceView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupLiveDemoUiComponents();
  }

  /** Sets up the UI components for the live demo with camera input. */
  private void setupLiveDemoUiComponents() {
    Button startCameraButton = findViewById(R.id.button_start_camera);
    startCameraButton.setOnClickListener(
        v -> {
          if (inputSource == InputSource.CAMERA) {
            return;
          }
          stopCurrentPipeline();
          setupStreamingModePipeline(InputSource.CAMERA);
        });
  }

  /** Sets up core workflow for streaming mode. */
  private void setupStreamingModePipeline(InputSource inputSource) {
    this.inputSource = inputSource;
    // Initializes a new MediaPipe Hands solution instance in the streaming mode.
    hands =
        new Hands(
            this,
            HandsOptions.builder()
                .setStaticImageMode(false)
                .setMaxNumHands(2)
                .setRunOnGpu(RUN_ON_GPU)
                .build());
    hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

    if (inputSource == InputSource.CAMERA) {
      cameraInput = new CameraInput(this);
      cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
    }

    // Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
    glSurfaceView =
        new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
    glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer());
    glSurfaceView.setRenderInputImage(true);
    hands.setResultListener(
        handsResult -> {
          logWristLandmark(handsResult, /*showPixelValues=*/ false);
          glSurfaceView.setRenderData(handsResult);
          glSurfaceView.requestRender();
        });

    // The runnable to start camera after the gl surface view is attached.
    if (inputSource == InputSource.CAMERA) {
      glSurfaceView.post(this::startCamera);
    }

    // Updates the preview layout.
    FrameLayout frameLayout = findViewById(R.id.preview_display_layout);
    frameLayout.removeAllViewsInLayout();
    frameLayout.addView(glSurfaceView);
    glSurfaceView.setVisibility(View.VISIBLE);
    frameLayout.requestLayout();
  }

  private void startCamera() {
    cameraInput.start(
        this,
        hands.getGlContext(),
        CameraInput.CameraFacing.FRONT,
        glSurfaceView.getWidth(),
        glSurfaceView.getHeight());
  }

  private void stopCurrentPipeline() {
    if (cameraInput != null) {
      cameraInput.setNewFrameListener(null);
      cameraInput.close();
    }
    if (glSurfaceView != null) {
      glSurfaceView.setVisibility(View.GONE);
    }
    if (hands != null) {
      hands.close();
    }
  }

  private void logWristLandmark(HandsResult result, boolean showPixelValues) {
    if (result.multiHandLandmarks().isEmpty()) {
      return;
    }
    NormalizedLandmark wristLandmark =
        result.multiHandLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST);
    // For Bitmaps, show the pixel values. For texture inputs, show the normalized coordinates.
    if (showPixelValues) {
      int width = result.inputBitmap().getWidth();
      int height = result.inputBitmap().getHeight();
      Log.i(
          TAG,
          String.format(
              "MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
              wristLandmark.getX() * width, wristLandmark.getY() * height));
    } else {
      Log.i(
          TAG,
          String.format(
              "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
              wristLandmark.getX(), wristLandmark.getY()));
    }
    if (result.multiHandWorldLandmarks().isEmpty()) {
      return;
    }
    Landmark wristWorldLandmark =
        result.multiHandWorldLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST);
    Log.i(
        TAG,
        String.format(
            "MediaPipe Hand wrist world coordinates (in meters with the origin at the hand's"
                + " approximate geometric center): x=%f m, y=%f m, z=%f m",
            wristWorldLandmark.getX(), wristWorldLandmark.getY(), wristWorldLandmark.getZ()));
  }
}
