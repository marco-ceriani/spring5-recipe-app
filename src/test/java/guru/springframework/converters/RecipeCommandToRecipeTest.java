package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.NotesCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Difficulty;
import guru.springframework.domain.Recipe;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RecipeCommandToRecipeTest {

    public static final String RECIPE_ID = "1";
    public static final Integer COOK_TIME = Integer.valueOf("5");
    public static final Integer PREP_TIME = Integer.valueOf("7");
    public static final String DESCRIPTION = "My Recipe";
    public static final String DIRECTIONS = "Directions";
    public static final Difficulty DIFFICULTY = Difficulty.HARD;
    public static final Integer SERVINGS = Integer.valueOf("3");
    public static final String SOURCE = "Source";
    public static final String URL = "Some URL";
    public static final String CAT_ID_1 = "1";
    public static final String CAT_ID_2 = "2";
    public static final String INGRED_ID_1 = "3";
    public static final String INGRED_ID_2 = "4";
    public static final String NOTES_ID = "9";

    RecipeCommandToRecipe converter;

    @Before
    public void setUp() {
        CategoryCommandToCategory categoryConverter = new CategoryCommandToCategory();
        UnitOfMeasureCommandToUnitOfMeasure uomConverter = new UnitOfMeasureCommandToUnitOfMeasure();
        IngredientCommandToIngredient ingredientConverter = new IngredientCommandToIngredient(uomConverter);
        NotesCommandToNotes notesConverter = new NotesCommandToNotes();
        converter = new RecipeCommandToRecipe(categoryConverter, ingredientConverter, notesConverter);
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new RecipeCommand()));
    }

    @Test
    public void testConvert() throws Exception {

        NotesCommand notes = new NotesCommand();
        notes.setId(NOTES_ID);

        CategoryCommand cateogry1 = new CategoryCommand();
        cateogry1.setId(CAT_ID_1);
        CategoryCommand cateogry2 = new CategoryCommand();
        cateogry2.setId(CAT_ID_2);
        List<CategoryCommand> categories = new ArrayList<>();
        categories.add(cateogry1);
        categories.add(cateogry2);

        IngredientCommand ingredient1 = new IngredientCommand();
        ingredient1.setId(INGRED_ID_1);
        IngredientCommand ingredient2 = new IngredientCommand();
        ingredient2.setId(INGRED_ID_2);
        List<IngredientCommand> ingredients = new ArrayList<>();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);

        RecipeCommand recipe = new RecipeCommand();
        recipe.setId(RECIPE_ID);
        recipe.setDescription(DESCRIPTION);
        recipe.setCookTime(COOK_TIME);
        recipe.setPrepTime(PREP_TIME);
        recipe.setServings(SERVINGS);
        recipe.setDirections(DIRECTIONS);
        recipe.setDifficulty(DIFFICULTY);
        recipe.setSource(SOURCE);
        recipe.setUrl(URL);
        recipe.setNotes(notes);
        recipe.setCategories(categories);
        recipe.setIngredients(ingredients);

        Recipe converted = converter.convert(recipe);

        assertEquals(RECIPE_ID, converted.getId());
        assertEquals(DESCRIPTION, converted.getDescription());
        assertEquals(PREP_TIME, converted.getPrepTime());
        assertEquals(COOK_TIME, converted.getCookTime());
        assertEquals(SERVINGS, converted.getServings());
        assertEquals(SOURCE, converted.getSource());
        assertEquals(URL, converted.getUrl());
        assertEquals(DIRECTIONS, converted.getDirections());
        assertEquals(DIFFICULTY, converted.getDifficulty());
        assertEquals(2, converted.getIngredients().size());
        assertEquals(2, converted.getCategories().size());
    }
}