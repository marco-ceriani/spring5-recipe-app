package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = IndexController.class)
@Import(ThymeleafAutoConfiguration.class)
public class IndexControllerTest {

    private IndexController indexController;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private RecipeService recipeService;

    @Mock
    private Model model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        indexController = new IndexController(recipeService);
    }

    @Test
    public void testMockMVC() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setDescription("Lasagne");
        when(recipeService.listRecipes()).thenReturn(Flux.just(recipe));

        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(resp -> {
                    String body = Objects.requireNonNull(resp.getResponseBody());
                    assertThat(body, containsString("<td>Lasagne</td>"));
                });
    }

    @Test
    public void getIndexPage() {

        // given
        Recipe recipe1 = new Recipe();
        recipe1.setId("1");
        Recipe recipe2 = new Recipe();
        recipe2.setId("2");

        when(recipeService.listRecipes()).thenReturn(Flux.just(recipe1, recipe2));

        ArgumentCaptor<Flux<Recipe>> argCaptor = ArgumentCaptor.forClass(Flux.class);

        // when
        String templateName = indexController.getIndexPage(model);

        // then
        assertEquals("index", templateName);
        verify(recipeService, times(1)).listRecipes();
        verify(model, times(1)).addAttribute(eq("recipes"), argCaptor.capture());
        List<Recipe> modelRecipes = argCaptor.getValue().collectList().block();
        assertEquals(2, modelRecipes.size());
    }
}