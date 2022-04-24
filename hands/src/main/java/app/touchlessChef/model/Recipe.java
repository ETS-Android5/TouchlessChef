package app.touchlessChef.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/models/Recipe.java
 * Modified using the Builder Design Pattern and add mealType and time attributes
 */
public class Recipe implements Parcelable {
    private final long id;
    private final String name;
    private final String category;
    private final String description;
    private final List<Ingredient> ingredients;
    private final List<Instruction> instructions;
    private final String imagePath;
    private final String mealType;
    private final String time;

    private Recipe(RecipeBuilder recipeBuilder) {
        this.id = recipeBuilder.id;
        this.name = recipeBuilder.name;
        this.category = recipeBuilder.category;
        this.description = recipeBuilder.description;
        this.ingredients = recipeBuilder.ingredients;
        this.instructions = recipeBuilder.instructions;
        this.imagePath = recipeBuilder.imagePath;
        this.mealType = recipeBuilder.mealType;
        this.time = recipeBuilder.time;
    }

    public static class RecipeBuilder {
        private long id;
        private String name;
        private String category;
        private String description;
        private List<Ingredient> ingredients;
        private List<Instruction> instructions;
        private String imagePath;
        private String mealType;
        private String time;

        public RecipeBuilder() {
            this.ingredients = new ArrayList<>();
            this.instructions = new ArrayList<>();
        }

        public RecipeBuilder setID(long id) {
            this.id = id;
            return this;
        }

        public RecipeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public RecipeBuilder setCategory(String category) {
            this.category = category;
            return this;
        }

        public RecipeBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public RecipeBuilder setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public RecipeBuilder setInstructions(List<Instruction> instructions) {
            this.instructions = instructions;
            return this;
        }

        public RecipeBuilder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public RecipeBuilder setMealType(String mealType) {
            this.mealType = mealType;
            return this;
        }

        public RecipeBuilder setTime(String time) {
            this.time = time;
            return this;
        }

        public Recipe build() {
            return new Recipe(this);
        }
    }

    protected Recipe(Parcel in) {
        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();

        id = in.readLong();
        name = in.readString();
        category = in.readString();
        description = in.readString();
        in.readTypedList(ingredients, Ingredient.CREATOR);
        in.readTypedList(instructions, Instruction.CREATOR);
        imagePath = in.readString();
        time = in.readString();
        mealType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(instructions);
        dest.writeString(imagePath);
        dest.writeString(time);
        dest.writeString(mealType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getMealType() {
        return mealType;
    }

    public String getTime() {
        return time;
    }

    @NonNull
    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", ingredients=" + ingredients +
                ", instructions=" + instructions +
                ", imagePath='" + imagePath + '\'' +
                ", mealType='" + mealType + '\'' +
                ", time=" + time +
                '}';
    }
}
