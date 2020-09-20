package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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

        // given
        Set<Recipe> mockedRecipes = new HashSet<>();
        Recipe recipe1 = new Recipe();
        recipe1.setId(1l);
        mockedRecipes.add(recipe1);
        Recipe recipe2 = new Recipe();
        recipe2.setId(2l);
        mockedRecipes.add(recipe2);

        when(recipeService.listRecipes()).thenReturn(mockedRecipes);

        ArgumentCaptor<Set<Recipe>> argCaptor = ArgumentCaptor.forClass(Set.class);

        // when
        String templateName = indexController.getIndexPage(model);

        // then
        assertEquals("index", templateName);
        verify(recipeService, times(1)).listRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argCaptor.capture());
        Set<Recipe> modelRecipes = argCaptor.getValue();
        assertEquals(2, modelRecipes.size());
    }
}