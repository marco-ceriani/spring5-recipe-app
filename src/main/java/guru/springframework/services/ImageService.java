package guru.springframework.services;

import org.springframework.core.io.InputStreamSource;

public interface ImageService {

    void saveImageFile(Long recipeId, InputStreamSource data);
}
