package guru.springframework.services;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageService {

    Mono<Void> saveImageFile(String recipeId, Flux<DataBuffer> data);
}
