package app.touchlessChef.activity.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import app.touchlessChef.R;
import app.touchlessChef.adapter.DatabaseAdapter;
import app.touchlessChef.model.Ingredient;
import app.touchlessChef.model.Instruction;
import app.touchlessChef.model.Recipe;
import app.touchlessChef.fragment.recipe.NavigableFragment;
import app.touchlessChef.fragment.recipe.RecipeCreateImageFragment;
import app.touchlessChef.fragment.recipe.RecipeCreateIngredientFragment;
import app.touchlessChef.fragment.recipe.RecipeCreateInstructionFragment;
import app.touchlessChef.constants.RecipeConstants.INTENT_KEYS;
import app.touchlessChef.constants.RecipeConstants.REQUEST_CODES;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/ui/CreateRecipeActivity.java
 * Modified using the Builder Design Pattern
 */
public class CreateRecipeActivity extends AppCompatActivity implements
        RecipeCreateImageFragment.ImageListener, RecipeCreateInstructionFragment.InstructionListener,
        RecipeCreateIngredientFragment.IngredientListener{
    private static final String NEXT = "NEXT";
    private static final String FINISH = "FINISH";

    private Recipe.RecipeBuilder recipeBuilder;
    private DatabaseAdapter databaseAdapter;
    private final Activity mActivity = this;

    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_create);

        databaseAdapter = DatabaseAdapter.getInstance(this);

        Intent intent = getIntent();
        String category = intent.getStringExtra(INTENT_KEYS.CATEGORY);
        recipeBuilder = new Recipe.RecipeBuilder().setCategory(category);

        initializeUI();
        displayFragment(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment instanceof RecipeCreateIngredientFragment)
            displayFragment(0);
        else if (fragment instanceof RecipeCreateInstructionFragment)
            displayFragment(1);
        else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODES.REQUEST_OPEN_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri imageData = data.getData();
                String imagePath = Files.getRealPathFromURI(this, imageData);
                ((RecipeCreateImageFragment) Objects.requireNonNull(getSupportFragmentManager()
                        .findFragmentById(R.id.frame_container))).onImageSelected(imagePath);
                recipeBuilder.setImagePath(imagePath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODES.REQUEST_TO_ACCESS_GALLERY) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openGallery();
            else
                Toast.makeText(this, "Permission denied to access the gallery.", Toast.LENGTH_LONG)
                        .show();
        }
    }

    private void displayFragment(int position) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        String nextButtonText = "";

        switch (position) {
            case 0:
                fragment = RecipeCreateImageFragment.newInstance();
                ft.setCustomAnimations(R.animator.left_slide_in, R.animator.right_slide_out);
                nextButtonText = NEXT;
                break;
            case 1:
                fragment = RecipeCreateIngredientFragment.newInstance();
                ft.setCustomAnimations(R.animator.right_slide_in, R.animator.left_slide_out);
                nextButtonText = NEXT;
                break;
            case 2:
                fragment = RecipeCreateInstructionFragment.newInstance();
                ft.setCustomAnimations(R.animator.right_slide_in, R.animator.left_slide_out);
                nextButtonText = FINISH;
                break;
        }

        nextButton.setText(nextButtonText);

        assert fragment != null;
        ft.replace(R.id.frame_container, fragment, "fragment" + position);
        ft.commit();
    }

    public void onNext(View view) {
        ((NavigableFragment) Objects.requireNonNull(getSupportFragmentManager()
                .findFragmentById(R.id.frame_container))).onNext();
    }

    private void initializeUI() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        nextButton = findViewById(R.id.nextButton);
    }

    @Override
    public void navigateToIngredientsFragment(String name, String description, String time, String mealType) {
        recipeBuilder.setName(name).setDescription(description).setTime(time).setMealType(mealType);
        displayFragment(1);
    }

    @Override
    public void navigateToInstructionsFragment(List<Ingredient> ingredients) {
        recipeBuilder.setIngredients(ingredients);
        displayFragment(2);
    }

    @Override
    public void onSelectImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODES.REQUEST_TO_ACCESS_GALLERY);
        } else {
            openGallery();
        }
    }

    @Override
    public void onStepsFinished(List<Instruction> instructions) {
        recipeBuilder.setInstructions(instructions);
        databaseAdapter.addNewRecipe(recipeBuilder.build());
        setResult(REQUEST_CODES.RECIPE_ADDED);
        finish();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        mActivity.startActivityForResult(gallery, REQUEST_CODES.REQUEST_OPEN_GALLERY);
    }

    public static class Files {
        public static String getRealPathFromURI(Context context, Uri contentURI) {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(contentURI);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
        }
    }
}