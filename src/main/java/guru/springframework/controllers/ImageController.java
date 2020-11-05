package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class ImageController {

    private final ImageService imageService;
    private final RecipeService recipeService;

    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model) {
        Mono<RecipeCommand> recipe = recipeService.findCommandById(id);
        model.addAttribute("recipe", recipe);
        return "recipe/imageuploadform";
    }

    @PostMapping("/recipe/{id}/image")
    public Mono<String> handleImageUpload(@PathVariable String id, @RequestPart("imagefile") FilePart file) {
        log.info("handling file upload {}", file.filename());
        return imageService.saveImageFile(id, file.content())
                .thenReturn("redirect:/recipe/" + id + "/show");
    }

    @GetMapping(value = "/recipe/{id}/recipeimage")
    public Mono<ResponseEntity<byte[]>> getRecipeImage(@PathVariable String id) {
        return recipeService.findCommandById(id)
                .flatMap(recipe -> Mono.justOrEmpty(recipe.getImage()))
                .map(image -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentLength(image.length);
                    return new ResponseEntity<>(image, headers, HttpStatus.OK);
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(new ResponseEntity<>(null, null, HttpStatus.NOT_FOUND))));
    }
}
