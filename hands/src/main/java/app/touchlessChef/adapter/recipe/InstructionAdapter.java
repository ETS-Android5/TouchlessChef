package app.touchlessChef.adapter.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.touchlessChef.R;
import app.touchlessChef.model.Instruction;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/adapters/DirectionAdapter.java
 * Adapter for RecyclerView
 * Modified to only support delete feature
 */
public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.InstructionViewHolder>{
    private final List<Instruction> instructionList;
    private InstructionListener instructionListener;

    public InstructionAdapter(List<Instruction> instructionList) {
        this.instructionList = instructionList;
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_recipe_instruction_item, parent, false);
        return new InstructionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final InstructionViewHolder holder, int position) {
        Instruction instruction = instructionList.get(position);
        holder.bind(instruction);
    }

    @Override
    public int getItemCount() {
        return instructionList.size();
    }

    public class InstructionViewHolder extends RecyclerView.ViewHolder {
        final TextView instructionText;
        final ImageView wasteBin;

        public InstructionViewHolder(View itemView) {
            super(itemView);

            instructionText = itemView.findViewById(R.id.instructionText);
            wasteBin = itemView.findViewById(R.id.wasteBin);
            wasteBin.setOnClickListener(v -> {
                if (instructionListener != null)
                    instructionListener.onDeleteInstruction(getAdapterPosition());
            });
        }

        public void bind(Instruction instruction) {
            instructionText.setText(instruction.getContent());
        }
    }

    public void setInstructionListener(InstructionListener instructionListener) {
        this.instructionListener = instructionListener;
    }

    public interface InstructionListener {
        void onDeleteInstruction(int position);
    }
}
