package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.*;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.RecipeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class RecipeServiceImplTest {


    private RecipeServiceImpl recipeService;

    @Mock
    private RecipeRepository recipeRepository;



    @Before
    public void setUp() throws Exception {
        RecipeToRecipeCommand recipeToRecipeCommand = new RecipeToRecipeCommand(new CategoryToCategoryCommand(),
                new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand()), new NotesToNotesCommand());

        RecipeCommandToRecipe recipeCommandToRecipe = new RecipeCommandToRecipe(new CategoryCommandToCategory(),
                new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure()), new NotesCommandToNotes());

        MockitoAnnotations.initMocks(this);
        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipeByIdTest() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeRepository.findById("1")).thenReturn(Optional.of(recipe));

        Recipe recipeReturned = recipeService.findById("1");

        assertNotNull(recipeReturned);
        verify(recipeRepository).findById("1");
        verify(recipeRepository, never()).findAll();
    }


    @Test(expected = NotFoundException.class)
    public void testGetRecipeNotFound() throws Exception {
        when(recipeRepository.findById(anyString())).thenReturn(Optional.empty());

        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        recipeService.findById("1");
    }

    @Test
    public void getRecipesTest() {
        Set recipesData = new HashSet();
        recipesData.add(new Recipe());
        when(recipeRepository.findAll()).thenReturn(recipesData);

        Set<Recipe> recipes = recipeService.listRecipes();
        assertEquals(1, recipes.size());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteById() {
        // given
        String idToDelete = "2";

        // when
        recipeService.deleteById(idToDelete);

        // then
        verify(recipeRepository, times(1)).deleteById(idToDelete);
    }
}