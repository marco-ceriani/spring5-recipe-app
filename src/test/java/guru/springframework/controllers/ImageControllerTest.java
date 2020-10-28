package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Ignore;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = ImageController.class)
@Import(ThymeleafAutoConfiguration.class)
public class ImageControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private ImageService imageService;
    @MockBean
    private RecipeService recipeService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ImageController controller = new ImageController(imageService, recipeService);
    }

    @Test
    public void testImageForm() throws Exception {
        // given
        String recipeId = "42";
        RecipeCommand recipe = new RecipeCommand();
        recipe.setId(recipeId);
        when(recipeService.findCommandById(recipeId)).thenReturn(Mono.just(recipe));

        // when
        webTestClient.get()
                .uri("/recipe/42/image")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body.toLowerCase(), containsString("upload"));
                    assertThat(body, containsString("/recipe/42/image"));
                });
    }

    @Test
    public void handleImagePost() throws Exception {
//        MultipartBodyBuilder multipartBuilder = new MultipartBodyBuilder();
//        multipartBuilder.part("imagefile", "Spring Framework Guru".getBytes(), MediaType.TEXT_PLAIN)
//                .header("Content-Disposition", "form-data; name=imagefile; filename=image.jpg");
//        webTestClient.post()
//                .uri("/recipe/1/image")
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .body(BodyInserters.fromMultipartData(multipartBuilder.build()))
//        .exchange()
//        .expectStatus().is3xxRedirection();

//        mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(header().string("Location", "/recipe/1/show"));
//        verify(imageService).saveImageFile(eq("1"), any());
    }

    @Test
    @Ignore
    public void readImageFromRepository() throws Exception {

        // given
//        String recipeId = "6";
//        byte[] content = "some content".getBytes();
//        RecipeCommand command = new RecipeCommand();
//        command.setId(recipeId);
//        command.setImage(content);
//
//        when(recipeService.findCommandById(recipeId)).thenReturn(Mono.just(command));
//
//        // when
//        MockHttpServletResponse resp = mockMvc.perform(get("/recipe/" + recipeId + "/recipeimage"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse();
//
//        byte[] respBody = resp.getContentAsByteArray();
//        assertEquals(respBody.length, content.length);
    }

}