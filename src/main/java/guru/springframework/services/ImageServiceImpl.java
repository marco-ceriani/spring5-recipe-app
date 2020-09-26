package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final RecipeRepository recipeRepository;

    public ImageServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    @Transactional
    public void saveImageFile(Long recipeId, MultipartFile data) {

        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("Recipe not found " + recipeId));

            recipe.setImage(data.getBytes());
            recipeRepository.save(recipe);
        } catch (IOException e) {
            log.error("Error saving image for recipe {}", recipeId, e);
        }
    }
}
