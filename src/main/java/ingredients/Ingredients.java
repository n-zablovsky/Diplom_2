package ingredients;

import java.util.ArrayList;

public class Ingredients {

    private ArrayList <String> ingredients;

    public Ingredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Ingredients() {
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
