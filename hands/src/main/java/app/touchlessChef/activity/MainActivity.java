package app.touchlessChef.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ViewSwitcher;


import androidx.annotation.DrawableRes;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;


import java.util.Objects;

import app.touchlessChef.R;
import app.touchlessChef.adapter.DatabaseAdapter;
import app.touchlessChef.fragment.cuisine.ChineseFragment;
import app.touchlessChef.fragment.cuisine.VietnamFragment;
import app.touchlessChef.model.Recipe;
import app.touchlessChef.fragment.cuisine.BaseFragment;
import app.touchlessChef.activity.recipe.CreateRecipeActivity;
import app.touchlessChef.activity.recipe.ViewRecipeActivity;
import app.touchlessChef.constants.RecipeConstants;
import app.touchlessChef.constants.RecipeConstants.INTENT_KEYS;
import app.touchlessChef.constants.RecipeConstants.REQUEST_CODES;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/ui/MainActivity.java
 * Modified to use Drawer for Navigation + add default recipe
 */
public class MainActivity extends BaseActivity implements BaseFragment.FragmentListener {
    private DatabaseAdapter databaseAdapter;

    private ImageView firstView;
    private ImageView secondView;
    private ViewSwitcher mViewSwitcher;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private String currentCategory;
    private final Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        databaseAdapter = DatabaseAdapter.getInstance(this);
        findViewsByID();

        // Set default cuisine
        int defaultImage = R.drawable.vn_bot_loc;
        currentCategory = RecipeConstants.VIETNAMESE;
        firstView.setImageResource(defaultImage);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                VietnamFragment.newInstance(RecipeConstants.VIETNAMESE)).commit();

        // Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(item -> {
            @DrawableRes int image = -1;
            switch (item.toString()) {
                case RecipeConstants.VIETNAMESE:
                    currentCategory = RecipeConstants.VIETNAMESE;
                    image = R.drawable.vn_bot_loc;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                            VietnamFragment.newInstance(RecipeConstants.VIETNAMESE)).commit();
                    break;
                case RecipeConstants.CHINESE:
                    currentCategory = RecipeConstants.CHINESE;
                    image = R.drawable.chn_dumpling;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                            ChineseFragment.newInstance(RecipeConstants.CHINESE)).commit();
                    break;
            }
            if (firstView.getVisibility() == View.VISIBLE) {
                secondView.setImageResource(image);
                mViewSwitcher.showNext();
            } else {
                firstView.setImageResource(image);
                mViewSwitcher.showPrevious();
            }
            mDrawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void findViewsByID() {
        CollapsingToolbarLayout mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mToolbar = findViewById(R.id.toolbar);
        firstView = findViewById(R.id.firstView);
        secondView = findViewById(R.id.secondView);
        mViewSwitcher = findViewById(R.id.switcher);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        // Toolbar attributes
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        mCollapsingToolbarLayout.setCollapsedTitleTypeface(font);
        mCollapsingToolbarLayout.setExpandedTitleTypeface(font);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_recipe) {
            Intent intent = new Intent(this, CreateRecipeActivity.class);
            intent.putExtra(INTENT_KEYS.CATEGORY, currentCategory);
            mActivity.startActivityForResult(intent, REQUEST_CODES.REQUEST_ADD_RECIPE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODES.REQUEST_ADD_RECIPE &&
                resultCode == REQUEST_CODES.RECIPE_ADDED) {
            Snackbar.make(getWindow().getDecorView(), "Recipe added.",
                    Snackbar.LENGTH_LONG).show();
        }

        if (requestCode == REQUEST_CODES.REQUEST_VIEW_RECIPE &&
                resultCode == REQUEST_CODES.RECIPE_SHOULD_BE_DELETED) {
            long recipeId = data.getLongExtra(INTENT_KEYS.RECIPE_ID, -1);
            if (recipeId != -1) {
                onDeleteRecipe(recipeId);
            }
            Snackbar.make(getWindow().getDecorView(), "Recipe deleted.",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onShowRecipe(Recipe recipe, Pair<ImageView, String> pairs) {
        Intent intent = new Intent(this, ViewRecipeActivity.class);
        intent.putExtra(INTENT_KEYS.RECIPE, recipe);

        ActivityOptions transitionActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(
                        mActivity, pairs.first, pairs.second);
        mActivity.startActivityForResult(
                intent, REQUEST_CODES.REQUEST_VIEW_RECIPE, transitionActivityOptions.toBundle());
    }

    public void onDeleteRecipe(long recipeId) {
        databaseAdapter.deleteRecipe(recipeId);
        Snackbar.make(getWindow().getDecorView(), "Recipe deleted.", Snackbar.LENGTH_LONG).show();
    }
}

