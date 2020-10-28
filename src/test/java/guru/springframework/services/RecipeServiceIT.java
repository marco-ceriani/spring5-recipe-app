package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecipeServiceIT {

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeReactiveRepository recipeRepository;

    @Autowired
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Autowired
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void saveRecipeCommand() {
        // given
        Recipe testRecipe = recipeRepository.findAll().blockFirst();
        RecipeCommand testCommand = recipeToRecipeCommand.convert(testRecipe);

        // when
        String testDescription = "This is the test description";
        testCommand.setDescription(testDescription);
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(testCommand).block();

        // then
        assertEquals(testDescription, savedCommand.getDescription());
        assertEquals(testCommand.getCategories().size(), savedCommand.getCategories().size());
        assertEquals(testCommand.getIngredients().size(), savedCommand.getIngredients().size());

    }

}