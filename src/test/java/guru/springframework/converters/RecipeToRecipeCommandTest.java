package guru.springframework.converters;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class RecipeToRecipeCommandTest {

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

    RecipeToRecipeCommand converter;

    @Before
    public void setUp() {
        CategoryToCategoryCommand categoryConverter = new CategoryToCategoryCommand();
        UnitOfMeasureToUnitOfMeasureCommand uomConverter = new UnitOfMeasureToUnitOfMeasureCommand();
        IngredientToIngredientCommand ingredientConverter = new IngredientToIngredientCommand(uomConverter);
        NotesToNotesCommand notesConverter = new NotesToNotesCommand();
        converter = new RecipeToRecipeCommand(categoryConverter, ingredientConverter, notesConverter);
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new Recipe()));
    }

    @Test
    public void testConvert() throws Exception {

        Notes notes = new Notes();
        notes.setId(NOTES_ID);

        Category cateogry1 = new Category();
        cateogry1.setId(CAT_ID_1);
        Category cateogry2 = new Category();
        cateogry2.setId(CAT_ID_2);
        Set<Category> categories = new HashSet<>();
        categories.add(cateogry1);
        categories.add(cateogry2);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(INGRED_ID_1);
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(INGRED_ID_2);
        Set<Ingredient> ingredients = new HashSet<>();
        ingredients.add(ingredient1);
        ingredients.add(ingredient2);

        Recipe recipe = new Recipe();
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

        RecipeCommand converted = converter.convert(recipe);

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