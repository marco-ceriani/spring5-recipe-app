package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;

public interface IngredientService {

    IngredientCommand findByRecipeIdAndIngredientId(long anyLong, long anyLong1);

    IngredientCommand saveIngredient(IngredientCommand command);

}
