package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IngredientControllerTest {

    MockMvc mockMvc;
    @Mock
    private RecipeService recipeService;
    @Mock
    private IngredientService ingredientService;
    @Mock
    private UnitOfMeasureService unitOfMeasureService;
    @Mock
    private Model model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        IngredientController controller = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void getListIngredients() throws Exception {

        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);

        // when
        mockMvc.perform(get("/recipe/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/list"))
                .andExpect(model().attributeExists("recipe"));

        // then
        verify(recipeService, times(1)).findCommandById(1);
    }

    @Test
    public void getShowIngredients() throws Exception {

        // given
        IngredientCommand ingredientCommand = new IngredientCommand();
        when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong())).thenReturn(ingredientCommand);

        // when
        mockMvc.perform(get("/recipe/1/ingredient/2/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/show"))
                .andExpect(model().attributeExists("ingredient"));

        // then
        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId(1, 2);
    }

    @Test
    public void testNewIngredientForm() throws Exception {
        // given
        Long commandId = 1L;
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(commandId);
        when(recipeService.findCommandById(commandId)).thenReturn(recipeCommand);
        when(unitOfMeasureService.listAllUoms()).thenReturn(new HashSet<>());

        // when + then
        mockMvc.perform(get("/recipe/1/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient", "uomList"));

        verify(recipeService).findCommandById(commandId);
    }

    @Test
    public void testUpdateIngredientForm() throws Exception {

        // given
        IngredientCommand ingredientCommand = new IngredientCommand();
        when(ingredientService.findByRecipeIdAndIngredientId(1L, 2L)).thenReturn(ingredientCommand);
        when(unitOfMeasureService.listAllUoms()).thenReturn(new HashSet<>());

        // when
        mockMvc.perform(get("/recipe/1/ingredient/2/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("uomList"));
    }

    @Test
    public void testSaveOrUpdate() throws Exception {

        // given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(5L);
        ingredientCommand.setRecipeId(12L);
        when(ingredientService.saveIngredient(any(IngredientCommand.class))).thenReturn(ingredientCommand);

        // when
        mockMvc.perform(post("/recipe/12/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "a description")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/12/ingredient/5/show"));
    }

}