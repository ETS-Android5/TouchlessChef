package app.touchlessChef.dao.recipe;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.touchlessChef.model.Ingredient;
import app.touchlessChef.model.Instruction;
import app.touchlessChef.model.Recipe;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/dao/RecipeDAO.java
 * Adopting Data Access Object Design Pattern to provide our app an API to talk to the SQLite db
 * Modified to support Builder Design Pattern
 */
public class RecipeDAO {
    private final SQLiteDatabase db;

    private final IngredientDAO ingredientDAO;
    private final InstructionDAO instructionDAO;

    public RecipeDAO(SQLiteDatabase db) {
        this.db = db;
        ingredientDAO = new IngredientDAO(db);
        instructionDAO = new InstructionDAO(db);
    }

    public void insert(Recipe recipe) {
        if (recipe.getIngredients() == null || recipe.getInstructions() == null)
            throw new IllegalStateException("Cannot insert recipe: the recipe is incomplete.");

        long newRecipeId = insert(recipe.getName(), recipe.getCategory(), recipe.getDescription(),
                recipe.getImagePath(), recipe.getTime(), recipe.getMealType());
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.setRecipeId(newRecipeId);
            ingredientDAO.insert(ingredient);
        }
        for (Instruction instruction : recipe.getInstructions()) {
            instruction.setRecipeId(newRecipeId);
            instructionDAO.insert(instruction);
        }
    }

    private long insert(String name, String category, String description, String imagePath,
                        String time, String mealType) {
        ContentValues values = new ContentValues();
        values.put(Config.KEY_NAME, name);
        values.put(Config.KEY_CATEGORY, category);
        values.put(Config.KEY_DESCRIPTION, description);
        values.put(Config.KEY_IMAGE_PATH, imagePath);
        values.put(Config.KEY_TIME, time);
        values.put(Config.KEY_MEAL_TYPE, mealType);
        return db.insert(Config.TABLE_NAME, null, values);
    }

    public List<Recipe> selectAllByCategory(String category) {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = db.query(Config.TABLE_NAME,
                new String[]{Config.KEY_ID, Config.KEY_NAME, Config.KEY_CATEGORY,
                        Config.KEY_DESCRIPTION, Config.KEY_IMAGE_PATH,
                        Config.KEY_TIME, Config.KEY_MEAL_TYPE},
                Config.KEY_CATEGORY + " = ?", new String[]{category},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    long recipeID = cursor.getLong(0);
                    Recipe recipe = new Recipe.RecipeBuilder()
                            .setID(recipeID)
                            .setName(cursor.getString(1))
                            .setCategory(cursor.getString(2))
                            .setDescription(cursor.getString(3))
                            .setIngredients(ingredientDAO.selectAllByRecipeId(recipeID))
                            .setInstructions(instructionDAO.selectAllByRecipeId(recipeID))
                            .setImagePath(cursor.getString(4))
                            .setTime(cursor.getString(5))
                            .setMealType(cursor.getString(6))
                            .build();
                    recipes.add(recipe);

                } while (cursor.moveToNext());
            }
        }
        return recipes;
    }

    public void deleteById(long id) {
        ingredientDAO.deleteAllByRecipeId(id);
        instructionDAO.deleteAllByRecipeId(id);
        db.delete(Config.TABLE_NAME, Config.KEY_ID + "=" + id, null);
    }

    public static class Config {
        public static final String TABLE_NAME = "recipes";
        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "name";
        public static final String KEY_CATEGORY = "category";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_IMAGE_PATH = "imagePath";
        public static final String KEY_TIME = "time";
        public static final String KEY_MEAL_TYPE = "mealType";

        public static final String CREATE_TABLE_STATEMENT =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_NAME + " TEXT NOT NULL, " +
                        KEY_CATEGORY + " TEXT NOT NULL, " +
                        KEY_DESCRIPTION + " TEXT NOT NULL, " +
                        KEY_IMAGE_PATH + " TEXT NOT NULL, " +
                        KEY_TIME + " TEXT NOT NULL, " +
                        KEY_MEAL_TYPE + " TEXT NOT NULL)";
    }
}
