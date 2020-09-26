package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @Mock
    private RecipeService recipeService;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ImageController controller = new ImageController(imageService, recipeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testImageForm() throws Exception {
        // given
        long recipeId = 42L;
        RecipeCommand recipe = new RecipeCommand();
        recipe.setId(recipeId);
        when(recipeService.findCommandById(recipeId)).thenReturn(recipe);

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
        verify(imageService).saveImageFile(eq(1L), any());
    }

    @Test
    public void readImageFromRepository() throws Exception {

        // given
        long recipeId = 6L;
        byte [] content = "some content".getBytes();
        RecipeCommand command = new RecipeCommand();
        command.setId(recipeId);
        command.setImage(content);

        when(recipeService.findCommandById(recipeId)).thenReturn(command);

        // when
        MockHttpServletResponse resp =  mockMvc.perform(get("/recipe/"+recipeId + "/recipeimage"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        byte[] respBody = resp.getContentAsByteArray();
        assertEquals(respBody.length, content.length);
    }

}