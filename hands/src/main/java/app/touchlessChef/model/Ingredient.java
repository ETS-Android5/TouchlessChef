package app.touchlessChef.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Reference: https://github.com/aza0092/Cooking-Recipe-Android-App/blob/master/app/src/main/java/models/Ingredient.java
 */
public class Ingredient implements Parcelable {
    private long id;
    private final String name;
    private long recipeId;

    public Ingredient(String name) {
        this.name = name;
    }

    public Ingredient(long id, String name, long recipeId) {
        this.id = id;
        this.name = name;
        this.recipeId = recipeId;
    }

    protected Ingredient(Parcel in) {
        id = in.readLong();
        name = in.readString();
        recipeId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(recipeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public String getName() {
        return name;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", recipeId=" + recipeId +
                '}';
    }
}
