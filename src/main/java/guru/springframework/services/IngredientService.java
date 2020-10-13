package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {

    IngredientCommand findByRecipeIdAndIngredientId(String anyString, String anyString1);

    IngredientCommand saveIngredient(IngredientCommand command);

    void deleteById(String recipeId, String id);
}
