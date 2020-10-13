package guru.springframework.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Recipe {

    private String id;

    private String description;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private String source;
    private String url;
    private String directions;
    private Difficulty difficulty;
    private Set<Ingredient> ingredients = new HashSet<>();
    private byte[] image;
    private Notes notes;
    private Set<Category> categories = new HashSet<>();

    public Notes getNotes() {
        return notes;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
        if (notes != null) {
            notes.setRecipe(this);
        }
    }
    
    public void addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

}
