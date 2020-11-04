package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final RecipeReactiveRepository recipeRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    @Transactional
    public Mono<Void> saveImageFile(String recipeId, Flux<DataBuffer> data) {
        Mono<byte[]> dataMono = DataBufferUtils.join(data)
                .map(this::getBytes);
        Mono<Recipe> recipeMono = recipeRepository.findById(recipeId);

        return recipeMono.zipWith(dataMono, (recipe, bytes)-> {
            recipe.setImage(bytes);
            return recipe;
        }).flatMap(recipeRepository::save)
                .doOnNext(rec -> log.info("saved image for recipe {}", rec.getId()))
                .doOnError(e -> log.error("error saving image for recipe{}", recipeId, e))
                .then();
    }

    private byte[] getBytes(DataBuffer buffer) {
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        return bytes;
    }

}
