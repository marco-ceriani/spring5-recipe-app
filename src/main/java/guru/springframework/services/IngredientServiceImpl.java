package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final RecipeRepository recipeRepository;
    private final IngredientToIngredientCommand outConverter;
    private final IngredientCommandToIngredient inConverter;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(RecipeRepository recipeRepository, IngredientToIngredientCommand outConverter, IngredientCommandToIngredient inConverter, UnitOfMeasureRepository unitOfMeasureRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.outConverter = outConverter;
        this.inConverter = inConverter;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(long recipeId, long ingredientId) {
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        Set<Ingredient> ingredients = recipe.map(Recipe::getIngredients)
                .orElseThrow(() -> new RuntimeException("Recipe not found " + recipeId));

        return ingredients.stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(outConverter::convert)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ingredient not found " + ingredientId));

    }

    @Override
    @Transactional
    public IngredientCommand saveIngredient(IngredientCommand command) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(command.getRecipeId());
        if (!recipeOpt.isPresent()) {
            log.error("recipe not found for id:{}", command.getRecipeId());
            return new IngredientCommand();
        }

        Recipe recipe = recipeOpt.get();

        Optional<Ingredient> ingredientOpt = findIngredient(recipe, command.getId());

        if (ingredientOpt.isPresent()) {
            Ingredient ingredient = ingredientOpt.get();
            ingredient.setDescription(command.getDescription());
            ingredient.setAmount(command.getAmount());
            UnitOfMeasure unitOfMeasure = getUnitOfMeasure(command);
            ingredient.setUnitOfMeasure(unitOfMeasure);
        } else {
            Ingredient ingredient = inConverter.convert(command);
            ingredient.setRecipe(recipe);
            recipe.addIngredient(ingredient);
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        Ingredient savedIngredient = findIngredient(recipe, command.getId())
                .orElseGet(() -> findIngredientByValue(recipe, command));
        return outConverter.convert(savedIngredient);
    }

    @Override
    public void deleteById(long recipeId, long id) {
        log.info("deleting ingredient {} from recipe {}", id, recipeId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Invalid recipe id " + recipeId));

        ingredientRepository.deleteById(id);
    }

    private Optional<Ingredient> findIngredient(Recipe recipe, Long id) {
        return recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(id))
                .findFirst();
    }

    private Ingredient findIngredientByValue(Recipe recipe, IngredientCommand command) {
        return recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getDescription().equals(command.getDescription()))
                .filter(ingredient -> ingredient.getAmount().equals(command.getAmount()))
                .filter(ingredient -> ingredient.getUnitOfMeasure().getId().equals(command.getUnitOfMeasure().getId()))
                .findFirst()
                .orElse(null);
    }

    private UnitOfMeasure getUnitOfMeasure(IngredientCommand command) {
        Long uomId = command.getUnitOfMeasure().getId();
        return unitOfMeasureRepository.findById(uomId)
                .orElseThrow(() -> new RuntimeException("Unit of Measure not found: " + uomId));
    }
}
