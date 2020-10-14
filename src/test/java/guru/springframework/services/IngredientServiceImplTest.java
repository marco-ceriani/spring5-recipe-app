package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IngredientServiceImplTest {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient inConverter;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UnitOfMeasureRepository unitOfMeasureRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    private IngredientService ingredientService;

    public IngredientServiceImplTest() {
        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        inConverter = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ingredientService = new IngredientServiceImpl(recipeRepository, ingredientToIngredientCommand, inConverter, unitOfMeasureRepository, ingredientRepository);
    }

    @Test
    public void findByRecipeIdAndIngredientId() {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId("1");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId("1");

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId("3");

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        when(recipeRepository.findById("1")).thenReturn(Optional.of(recipe));

        // when
        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1", "3");

        // then
        assertEquals("3", ingredientCommand.getId());
    }

    @Test
    public void testSaveRecipe() {
        // given
        String recipeId = "2";
        String ingredientId = "3";

        IngredientCommand command = new IngredientCommand();
        command.setId(ingredientId);
        command.setRecipeId(recipeId);

        Recipe savedRecipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        savedRecipe.addIngredient(ingredient);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(new Recipe()));
        when(recipeRepository.save(any())).thenReturn(savedRecipe);

        // when
        IngredientCommand savedCommand = ingredientService.saveIngredient(command);

        // then
        assertEquals(ingredientId, savedCommand.getId());
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    public void testUpdateIngredient() {
        // given
        String recipeId = "2";
        String ingredientId = "3";

        UnitOfMeasureCommand uomCommand = new UnitOfMeasureCommand();
        uomCommand.setId("0");
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setId("0");
        when(unitOfMeasureRepository.findById("0")).thenReturn(Optional.of(uom));

        IngredientCommand command = new IngredientCommand();
        command.setId(ingredientId);
        command.setRecipeId(recipeId);
        command.setUnitOfMeasure(uomCommand);

        Recipe savedRecipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        ingredient.setUnitOfMeasure(new UnitOfMeasure());
        savedRecipe.addIngredient(ingredient);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(savedRecipe));
        when(recipeRepository.save(any())).thenReturn(savedRecipe);

        // when
        IngredientCommand savedCommand = ingredientService.saveIngredient(command);

        // then
        assertEquals(ingredientId, savedCommand.getId());
        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    public void testDeleteIngredient() throws Exception {

        String recipeId = "7";
        String ingredientId = "15";

        Recipe recipe = new Recipe();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        recipe.addIngredient(ingredient);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // when
        ingredientService.deleteById(recipeId, ingredientId);

        // then
        verify(recipeRepository).findById(recipeId);
        verify(ingredientRepository).deleteById(ingredientId);

    }

}