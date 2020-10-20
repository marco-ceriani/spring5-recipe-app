package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.*;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class RecipeServiceImplTest {


    private RecipeServiceImpl recipeService;

    @Mock
    private RecipeReactiveRepository recipeRepository;



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

        when(recipeRepository.findById("1")).thenReturn(Mono.just(recipe));

        Recipe recipeReturned = recipeService.findById("1").block();

        assertNotNull(recipeReturned);
        verify(recipeRepository).findById("1");
        verify(recipeRepository, never()).findAll();
    }

    @Test
    public void testGetRecipeNotFound() throws Exception {
        when(recipeRepository.findById(anyString())).thenReturn(Mono.empty());

        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        StepVerifier.create(recipeService.findById("1"))
                .verifyComplete();
    }

    @Test
    public void getRecipesTest() {
        when(recipeRepository.findAll()).thenReturn(Flux.just(new Recipe()));

        List<Recipe> recipes = recipeService.listRecipes().collectList().block();
        assertEquals(1, recipes.size());
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteById() {
        // given
        String idToDelete = "2";
        when(recipeRepository.deleteById(idToDelete)).thenReturn(Mono.empty());

        // when
        recipeService.deleteById(idToDelete).block();

        // then
        verify(recipeRepository, times(1)).deleteById(idToDelete);
    }
}