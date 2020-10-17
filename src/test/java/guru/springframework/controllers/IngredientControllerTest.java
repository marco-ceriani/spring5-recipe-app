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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.hamcrest.Matchers;

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
        when(recipeService.findCommandById(anyString())).thenReturn(recipeCommand);

        // when
        mockMvc.perform(get("/recipe/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/list"))
                .andExpect(model().attributeExists("recipe"));

        // then
        verify(recipeService, times(1)).findCommandById("1");
    }

    @Test
    public void getShowIngredients() throws Exception {

        // given
        IngredientCommand ingredientCommand = new IngredientCommand();
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(ingredientCommand));

        // when
        mockMvc.perform(get("/recipe/1/ingredient/2/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/show"))
                .andExpect(model().attribute("ingredient", Matchers.is(ingredientCommand)));

        // then
        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId("1", "2");
    }

    @Test
    public void testNewIngredientForm() throws Exception {
        // given
        String commandId = "1";
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(commandId);
        when(recipeService.findCommandById(commandId)).thenReturn(recipeCommand);
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.empty());

        // when + then
        mockMvc.perform(get("/recipe/1/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient", "uomList"))
                .andExpect(model().attribute("ingredient", Matchers.isA(IngredientCommand.class)));

        verify(recipeService).findCommandById(commandId);
    }

    @Test
    public void testUpdateIngredientForm() throws Exception {

        // given
        IngredientCommand ingredientCommand = new IngredientCommand();
        when(ingredientService.findByRecipeIdAndIngredientId("1", "2")).thenReturn(Mono.just(ingredientCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.empty());

        // when
        mockMvc.perform(get("/recipe/1/ingredient/2/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attribute("ingredient", Matchers.is(ingredientCommand)))
                .andExpect(model().attributeExists("uomList"));
    }

    @Test
    public void testSaveOrUpdate() throws Exception {

        // given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId("5");
        ingredientCommand.setRecipeId("12");
        when(ingredientService.saveIngredient(any(IngredientCommand.class))).thenReturn(Mono.just(ingredientCommand));

        // when
        mockMvc.perform(post("/recipe/12/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "")
                .param("description", "a description")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/12/ingredient/5/show"));
    }

    @Test
    public void testDeleteIngredient() throws Exception {
        when(ingredientService.deleteById(any(), any())).thenReturn(Mono.empty());

        // when + then
        mockMvc.perform(get("/recipe/7/ingredient/37/delete"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/recipe/7/ingredients"));

        // then
        verify(ingredientService).deleteById("7", "37");
    }

}