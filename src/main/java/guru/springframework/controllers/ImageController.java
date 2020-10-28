package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.RecipeService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

@Controller
public class ImageController {

    private final ImageService imageService;
    private final RecipeService recipeService;

    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model) {
        RecipeCommand recipe = recipeService.findCommandById(id).toProcessor().block();
        model.addAttribute("recipe", recipe);
        return "recipe/imageuploadform";
    }

    @PostMapping("/recipe/{id}/image")
    public String handleImageUpload(@PathVariable String id, @RequestPart("imagefile") FilePart file) {
        imageService.saveImageFile(id, file.content())
                .toProcessor()
                .block();
        return "redirect:/recipe/" + id + "/show";
    }

    // TODO: replace this
//    @GetMapping("/recipe/{id}/recipeimage")
//    public void getRecipeImage(@PathVariable String id, HttpServletResponse response) throws IOException {
//        RecipeCommand recipe = recipeService.findCommandById(id).toProcessor().block();
//
//        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//        if (recipe.getImage() != null) {
//            InputStream is = new ByteArrayInputStream((recipe.getImage()));
//            IOUtils.copy(is, response.getOutputStream());
//        }
//    }
}
