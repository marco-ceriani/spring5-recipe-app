package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final RecipeReactiveRepository recipeRepository;
    private final IngredientToIngredientCommand outConverter;
    private final IngredientCommandToIngredient inConverter;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    public IngredientServiceImpl(RecipeReactiveRepository recipeRepository, IngredientToIngredientCommand outConverter, IngredientCommandToIngredient inConverter, UnitOfMeasureReactiveRepository unitOfMeasureRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.outConverter = outConverter;
        this.inConverter = inConverter;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
        return recipeRepository.findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand command = outConverter.convert(ingredient);
                    command.setRecipeId(recipeId);
                    return command;
                });
    }

    @Override
    @Transactional
    public Mono<IngredientCommand> saveIngredient(IngredientCommand command) {
        Recipe recipe = recipeRepository.findById(command.getRecipeId()).block();
        if (recipe == null) {
            log.error("recipe not found for id:{}", command.getRecipeId());
            return Mono.empty();
        }

        Optional<Ingredient> ingredientOpt = findIngredient(recipe, command.getId());

        if (ingredientOpt.isPresent()) {
            Ingredient ingredient = ingredientOpt.get();
            ingredient.setDescription(command.getDescription());
            ingredient.setAmount(command.getAmount());
            UnitOfMeasure unitOfMeasure = getUnitOfMeasure(command);
            ingredient.setUnitOfMeasure(unitOfMeasure);
        } else {
            Ingredient ingredient = inConverter.convert(command);
            recipe.addIngredient(ingredient);
        }

        Recipe savedRecipe = recipeRepository.save(recipe).block();

        Ingredient savedIngredient = findIngredient(recipe, command.getId())
                .orElseGet(() -> findIngredientByValue(recipe, command));
        IngredientCommand savedCommand = outConverter.convert(savedIngredient);
        savedCommand.setRecipeId(recipe.getId());
        return Mono.just(savedCommand);
    }

    @Override
    public Mono<Void> deleteById(String recipeId, String id) {
        log.info("deleting ingredient {} from recipe {}", id, recipeId);
        return recipeRepository.findById(recipeId)
                .doOnNext(recipe -> recipe.getIngredients().removeIf(ingredient -> ingredient.getId().equals(id)))
                .map(recipeRepository::save)
                .doOnError(error -> log.error("error deleting ingredient {} from recipe {}", id, recipeId, error))
                .then();

    }

    private Optional<Ingredient> findIngredient(Recipe recipe, String id) {
        return recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(id))
                .findAny();
    }

//    private Mono<Ingredient> findIngredient(Recipe recipe, String id) {
//        return Flux.fromIterable(recipe.getIngredients())
//                .filter(ingredient -> ingredient.getId().equals(id))
//                .next();
//    }

    private Ingredient findIngredientByValue(Recipe recipe, IngredientCommand command) {
        return recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getDescription().equals(command.getDescription()))
                .filter(ingredient -> ingredient.getAmount().equals(command.getAmount()))
                .filter(ingredient -> ingredient.getUnitOfMeasure().getId().equals(command.getUnitOfMeasure().getId()))
                .findFirst()
                .orElse(null);
    }

    private UnitOfMeasure getUnitOfMeasure(IngredientCommand command) {
        String uomId = command.getUnitOfMeasure().getId();
        return unitOfMeasureRepository.findById(uomId).block();
    }
}
