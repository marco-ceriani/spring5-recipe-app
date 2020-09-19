package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class IndexControllerTest {

    private IndexController indexController;

    @Mock
    private RecipeService recipeService;

    @Mock
    private Model model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        indexController = new IndexController(recipeService);
    }

    @Test
    public void name() {
        Set<Recipe> mockedRecipes = new HashSet<>();
        mockedRecipes.add(new Recipe());
        when(recipeService.listRecipes()).thenReturn(mockedRecipes);

        String templateName = indexController.getIndexPage(model);
        assertEquals("index", templateName);
        verify(model, times(1)).addAttribute("recipes", mockedRecipes);
        verify(recipeService, times(1)).listRecipes();
    }
}