package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ImageControllerTest {

    MockMvc mockMvc;
    @Mock
    private ImageService imageService;
    @Mock
    private RecipeService recipeService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ImageController controller = new ImageController(imageService, recipeService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    public void testImageForm() throws Exception {
        // given
        String recipeId = "42";
        RecipeCommand recipe = new RecipeCommand();
        recipe.setId(recipeId);
        when(recipeService.findCommandById(recipeId)).thenReturn(Mono.just(recipe));

        // when
        mockMvc.perform(get("/recipe/42/image"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(view().name("recipe/imageuploadform"));
    }

    @Test
    public void handleImagePost() throws Exception {
        MockMultipartFile multipartFile =
                new MockMultipartFile("imagefile", "testing.txt", "text/plain",
                        "Spring Framework Guru".getBytes());

        mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/recipe/1/show"));
        verify(imageService).saveImageFile(eq("1"), any());
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