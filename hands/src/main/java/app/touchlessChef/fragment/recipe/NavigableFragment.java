package app.touchlessChef.fragment.recipe;

import androidx.fragment.app.Fragment;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/ui/fragments/NavigableFragment.java
 * Modified RecipeCreateXXXFragment to support Builder Design Pattern
 */
public abstract class NavigableFragment extends Fragment {
    public abstract void onNext();
}