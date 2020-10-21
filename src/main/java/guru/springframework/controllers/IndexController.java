package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Created by jt on 6/1/17.
 */
@Controller
public class IndexController {

    private final RecipeService recipeService;

    public IndexController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping({"", "/", "/index"})
    public String getIndexPage(Model model){
        List<Recipe> o = recipeService.listRecipes().collectList().toProcessor().block();
        model.addAttribute("recipes", o);
        return "index";
    }
}
