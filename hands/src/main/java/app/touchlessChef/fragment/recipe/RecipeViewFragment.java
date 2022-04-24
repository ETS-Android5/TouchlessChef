package app.touchlessChef.fragment.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import app.touchlessChef.R;
import app.touchlessChef.constants.RecipeConstants.FRAGMENT_ARGS;
import app.touchlessChef.model.Ingredient;
import app.touchlessChef.model.Instruction;


/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/tree/master/app/src/main/java/ui/fragments
 * Modified to support Hands Tracking (show Instructions and Ingredients within one TextView for scrolling)
 */
public class RecipeViewFragment extends Fragment {
    private List<Ingredient> ingredientList;
    private List<Instruction> instructionList;

    public RecipeViewFragment() {
    }

    public static RecipeViewFragment newInstance(List<Ingredient> ingredients,
                                                 List<Instruction> instructions) {
        RecipeViewFragment fragment = new RecipeViewFragment();
        if (ingredients == null) ingredients = new ArrayList<>();
        if (instructions == null) instructions = new ArrayList<>();
        Bundle args = new Bundle();

        args.putParcelableArrayList(FRAGMENT_ARGS.INGREDIENTS, (ArrayList<Ingredient>) ingredients);
        args.putParcelableArrayList(FRAGMENT_ARGS.INSTRUCTIONS, (ArrayList<Instruction>) instructions);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_view, container, false);

        Bundle args = getArguments();
        if (args != null) {
            ingredientList = args.getParcelableArrayList(FRAGMENT_ARGS.INGREDIENTS);
            instructionList = args.getParcelableArrayList(FRAGMENT_ARGS.INSTRUCTIONS);
        }
        TextView recipeView = view.findViewById(R.id.recipe_holder);

        recipeView.append("\nINGREDIENTS\n");
        for (Ingredient ingredient: ingredientList)
        {
            recipeView.append("\n");
            recipeView.append(ingredientList.indexOf(ingredient) + 1 +  ". " + ingredient.getName());
            recipeView.append("\n");
        }
        recipeView.append("\n\nINSTRUCTIONS\n");
        for(Instruction instruction : instructionList)
        {
            recipeView.append("\n");
            recipeView.append(instruction.getContent());
            recipeView.append("\n");

        }
        return view;
    }
}
