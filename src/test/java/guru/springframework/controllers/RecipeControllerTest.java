package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Notes;
import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = RecipeController.class)
@Import(ThymeleafAutoConfiguration.class)
public class RecipeControllerTest {

    @MockBean
    RecipeService recipeService;

    RecipeController controller;

    @Autowired
    WebTestClient webTestClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new RecipeController(recipeService);
    }

    @Test
    public void testGetRecipe() throws Exception {
        String recipeNotes = "These are the recipe notes";
        Recipe recipe = new Recipe();
        recipe.setDescription("Lasagne");
        recipe.setId("1");
        Notes notes = new Notes();
        notes.setRecipeNotes(recipeNotes);
        recipe.setNotes(notes);

        when(recipeService.findById(anyString())).thenReturn(Mono.just(recipe));

        webTestClient.get().uri("/recipe/1/show")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("Lasagne"));
                    assertThat(body, containsString(recipeNotes));
                });
    }

    @Test
    public void testGetRecipeNotFound() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");

        when(recipeService.findById(anyString())).thenReturn(Mono.empty());

        webTestClient.get().uri("/recipe/1/show")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testGetNewRecipeForm() throws Exception {
        RecipeCommand recipe = new RecipeCommand();
        recipe.setId("1");

//        mockMvc.perform(get("/recipe/new"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("recipe/recipeform"))
//                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void testPostNewRecipe() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("5");

        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));

//        mockMvc.perform(post("/recipe")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("id", "")
//                .param("description", "a description")
//                .param("directions", "some direction")
//        )
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/recipe/5/show"));
    }

    @Test
    public void testPostNewRecipeFormValidationFail() throws Exception {
        RecipeCommand command = new RecipeCommand();
        command.setId("5");

        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));

//        mockMvc.perform(post("/recipe")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("id", "")
//        )
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("recipe"))
//                .andExpect(view().name("recipe/recipeform"));
    }

    @Test
    public void testGetUpdateView() throws Exception {
        RecipeCommand recipe = new RecipeCommand();
        recipe.setId("7");

        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipe));

//        mockMvc.perform(get("/recipe/1/update"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("recipe/recipeform"))
//                .andExpect(model().attributeExists("recipe"));
    }

    @Test
    public void testGetDeleteAction() throws Exception {
        when(recipeService.deleteById(anyString())).thenReturn(Mono.empty());

//        mockMvc.perform(get("/recipe/1/delete"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/"));

//        verify(recipeService, times(1)).deleteById("1");
    }

}