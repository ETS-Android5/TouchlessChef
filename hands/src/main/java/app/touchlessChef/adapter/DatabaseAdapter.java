package app.touchlessChef.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import app.touchlessChef.dao.recipe.RecipeDAO;
import app.touchlessChef.dao.SQLiteDAO;
import app.touchlessChef.model.Recipe;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/adapters/DatabaseAdapter.java
 * Adoption Singleton Design Pattern
 */
public class DatabaseAdapter {
    /**
     * The singleton instance.
     */
    @SuppressLint("StaticFieldLeak")
    private static DatabaseAdapter instance;
    private static final String DATABASE_NAME = "recipes";
    private static final int DATABASE_VERSION = 1;

    private final SQLiteDAO dbCRUD;
    private RecipeDAO recipeDAO;

    /**
     * A static factory method.
     *
     * @return the singleton instance of the DatabaseAdapter.
     */
    public static DatabaseAdapter getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseAdapter.class) {
                if (instance == null)
                    instance = new DatabaseAdapter(context).open();
            }
        }
        return instance;
    }

    private DatabaseAdapter(Context context) {
        dbCRUD = new SQLiteDAO(context, DATABASE_NAME, DATABASE_VERSION);
    }

    private DatabaseAdapter open() {
        SQLiteDatabase db = dbCRUD.getWritableDatabase();
        recipeDAO = new RecipeDAO(db);
        return this;
    }

    public void addNewRecipe(Recipe recipe) {
        recipeDAO.insert(recipe);
    }

    public void deleteRecipe(long recipeId) {
        recipeDAO.deleteById(recipeId);
    }

    public List<Recipe> getAllRecipesByCategory(String category) {
        return recipeDAO.selectAllByCategory(category);
    }
}
