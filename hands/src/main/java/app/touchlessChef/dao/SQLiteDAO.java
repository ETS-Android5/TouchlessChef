package app.touchlessChef.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import app.touchlessChef.constants.RecipeConstants.DEFAULT_RECIPE;
import app.touchlessChef.dao.recipe.IngredientDAO;
import app.touchlessChef.dao.recipe.InstructionDAO;
import app.touchlessChef.dao.recipe.RecipeDAO;
import app.touchlessChef.model.Ingredient;
import app.touchlessChef.model.Instruction;
import app.touchlessChef.model.Recipe;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/dao/SQLiteDatabaseHelper.java
 * Adopting Data Access Object Design Pattern to provide our app an API to talk to the SQLite db
 * Modified to add default recipe on create
 */
public class SQLiteDAO extends SQLiteOpenHelper {
    private IngredientDAO ingredientDAO;
    private InstructionDAO instructionDAO;
    private static final String TAG = SQLiteDAO.class.getSimpleName();

    public SQLiteDAO(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecipeDAO.Config.CREATE_TABLE_STATEMENT);
        db.execSQL(InstructionDAO.Config.CREATE_TABLE_STATEMENT);
        db.execSQL(IngredientDAO.Config.CREATE_TABLE_STATEMENT);
        default_insert_recipe(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion +
                " to " + newVersion + ", which will destroy all old data.");
        String DROP_TABLE = "DROP TABLE IF EXISTS ";
        db.execSQL(DROP_TABLE + InstructionDAO.Config.TABLE_NAME);
        db.execSQL(DROP_TABLE + IngredientDAO.Config.TABLE_NAME);
        db.execSQL(DROP_TABLE + RecipeDAO.Config.TABLE_NAME);
        onCreate(db);
    }

    private void default_insert_recipe(SQLiteDatabase db) {
        Recipe defaultRecipe = DEFAULT_RECIPE.VIETNAM_DEFAULT_RECIPE;
        ingredientDAO = new IngredientDAO(db);
        instructionDAO = new InstructionDAO(db);
        long recipeID = default_insert_helper(db, defaultRecipe.getName(),
                defaultRecipe.getCategory(), defaultRecipe.getDescription(),
                defaultRecipe.getImagePath(), defaultRecipe.getTime(), defaultRecipe.getMealType());
        for (Ingredient ingredient : defaultRecipe.getIngredients()) {
            ingredient.setRecipeId(recipeID);
            ingredientDAO.insert(ingredient);
            Log.i(TAG, "Inserted " + ingredient);
        }
        for (Instruction instruction : defaultRecipe.getInstructions()) {
            instruction.setRecipeId(recipeID);
            instructionDAO.insert(instruction);
            Log.i(TAG, "Inserted " + instruction);
        }

    }

    private long default_insert_helper(SQLiteDatabase db, String name, String category,
                                       String description, String imagePath,
                                       String time, String mealType){
        ingredientDAO = new IngredientDAO(db);
        instructionDAO = new InstructionDAO(db);
        ContentValues values = new ContentValues();
        values.put(RecipeDAO.Config.KEY_NAME, name);
        values.put(RecipeDAO.Config.KEY_CATEGORY, category);
        values.put(RecipeDAO.Config.KEY_DESCRIPTION, description);
        values.put(RecipeDAO.Config.KEY_IMAGE_PATH, imagePath);
        values.put(RecipeDAO.Config.KEY_TIME, time);
        values.put(RecipeDAO.Config.KEY_MEAL_TYPE, mealType);
        return db.insert(RecipeDAO.Config.TABLE_NAME, null, values);
    }
}
