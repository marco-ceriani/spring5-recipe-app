package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import reactor.core.publisher.Mono;

public interface IngredientService {

    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String anyString, String anyString1);

    Mono<IngredientCommand> saveIngredient(IngredientCommand command);

    Mono<Void> deleteById(String recipeId, String id);
}
