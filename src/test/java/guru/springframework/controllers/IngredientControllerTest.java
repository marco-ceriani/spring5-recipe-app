package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = IngredientController.class)
@Import(ThymeleafAutoConfiguration.class)
public class IngredientControllerTest {

    @Autowired
    WebTestClient webTestClient;

    IngredientController controller;

    @MockBean
    private RecipeService recipeService;
    @MockBean
    private IngredientService ingredientService;
    @MockBean
    private UnitOfMeasureService unitOfMeasureService;

    UnitOfMeasureCommand ounceUnit;
    IngredientCommand ingredient1;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new IngredientController(recipeService, ingredientService, unitOfMeasureService);

        ounceUnit = new UnitOfMeasureCommand();
        ounceUnit.setDescription("Ounce");
        ounceUnit.setId(UUID.randomUUID().toString());

        ingredient1 = new IngredientCommand();
        ingredient1.setId("1358136a8f4dbd51e");
        ingredient1.setDescription("Salt");
        ingredient1.setAmount(BigDecimal.valueOf(3));
        ingredient1.setUnitOfMeasure(ounceUnit);
    }

    @Test
    public void getListIngredients() throws Exception {

        // given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setDescription("Recipe description");
        recipeCommand.setIngredients(Arrays.asList(ingredient1));
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipeCommand));

        // when
        webTestClient.get().uri("/recipe/1/ingredients")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("Salt"));
                    assertThat(body, containsString("3 Ounce"));
                });

        // then
        verify(recipeService, times(1)).findCommandById("1");
    }

    @Test
    public void getShowIngredients() throws Exception {

        // given
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(ingredient1));

        // when
        webTestClient.get()
                .uri("/recipe/1/ingredient/2/show")
                .exchange()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("Salt"));
                    assertThat(body, containsString("3 Ounce"));
                });

        // then
        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId("1", "2");
    }

    @Test
    public void testNewIngredientForm() throws Exception {
        // given
        String recipeId = "1234321";
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(recipeId);
        when(recipeService.findCommandById(recipeId)).thenReturn(Mono.just(recipeCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.empty());

        // when + then
        webTestClient.get()
                .uri("/recipe/1234321/ingredient/new")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("/recipe/" + recipeId));
                });

        verify(recipeService).findCommandById(recipeId);
    }

    @Test
    public void testUpdateIngredientForm() throws Exception {

        // given
        String recipeId = "a1f3d2";
        ingredient1.setRecipeId(recipeId);
        when(ingredientService.findByRecipeIdAndIngredientId("1", "2")).thenReturn(Mono.just(ingredient1));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.just(ounceUnit));

        // when
        webTestClient.get()
                .uri("/recipe/1/ingredient/2/update")
                .exchange()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("/recipe/" + recipeId));
                    assertThat(body, containsString(ingredient1.getId()));
                    assertThat(body, containsString(ounceUnit.getDescription()));
                    assertThat(body, containsString(ounceUnit.getId()));
                });
    }

    @Test
    public void testSaveOrUpdate() throws Exception {

        // given
        String ingredientId = "5123123123";
        String description = "Sugar";
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setDescription(description);
        ingredientCommand.setId(ingredientId);
        ingredientCommand.setRecipeId("12");
        when(ingredientService.saveIngredient(any(IngredientCommand.class))).thenReturn(Mono.just(ingredientCommand));

        // when
        webTestClient.post()
                .uri("/recipe/12/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("id", ingredientId)
                        .with("description", description)
                        .with("amount", "2")
                        .with("unitOfMeasure.id", "5fa2eb39e80c2c5def8da99f"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader()
                .valueEquals("Location", "/recipe/12/ingredient/5123123123/show");

        ArgumentCaptor<IngredientCommand> captor = ArgumentCaptor.forClass(IngredientCommand.class);
        verify(ingredientService).saveIngredient(captor.capture());
        IngredientCommand savedIngredient = captor.getValue();
        assertThat(savedIngredient.getId(), is(ingredientCommand.getId()));
        assertThat(savedIngredient.getDescription(), is(ingredientCommand.getDescription()));
        assertThat(savedIngredient.getRecipeId(), is(ingredientCommand.getRecipeId()));
    }

    @Test
    public void testDeleteIngredient() throws Exception {
        when(ingredientService.deleteById(any(), any())).thenReturn(Mono.empty());

        // when + then
        webTestClient.get()
                .uri("/recipe/7/ingredient/37/delete")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader()
                .valueEquals("Location", "/recipe/7/ingredients");

        // then
        verify(ingredientService).deleteById("7", "37");
    }

}