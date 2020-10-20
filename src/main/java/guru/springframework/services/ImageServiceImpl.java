package guru.springframework.services;

import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final RecipeReactiveRepository recipeRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    @Transactional
    public Mono<Void> saveImageFile(String recipeId, MultipartFile data) {
        try {
            byte[] bytes = data.getBytes();
            return recipeRepository.findById(recipeId)
                    .doOnNext(recipe -> recipe.setImage(bytes))
                    .flatMap(recipeRepository::save)
                    .doOnNext(rec -> log.info("saved image for recipe {}", rec.getId()))
                    .doOnError(e -> log.error("error saving image for recipe{}", recipeId, e))
                    .then();
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

}
