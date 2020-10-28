package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageServiceImplTest {

    @Mock
    RecipeReactiveRepository recipeRepository;

    ImageService imageService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        imageService = new ImageServiceImpl(recipeRepository);
    }

    @Test
    public void saveImageFile() throws Exception {
        // given
        String id = "1";

        Recipe recipe = new Recipe();
        recipe.setId(id);
        when(recipeRepository.findById(id)).thenReturn(Mono.just(recipe));
        when(recipeRepository.save(recipe)).thenReturn(Mono.empty());

        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

        DataBuffer buffer = Mockito.mock(DataBuffer.class);
        when(buffer.readableByteCount()).thenReturn(38);
        // when
        imageService.saveImageFile(id, Flux.just(buffer)).block();

        // then
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe savedRecipe = recipeCaptor.getValue();
        assertEquals(38, savedRecipe.getImage().length);
    }
}