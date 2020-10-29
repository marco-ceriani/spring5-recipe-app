package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Controller
@Slf4j
public class RecipeController {

    public static final String RECIPE_RECIPEFORM = "recipe/recipeform";
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model){
        Mono<Recipe> recipe = recipeService.findById(id);
        model.addAttribute("recipe", recipe);
        return "recipe/show";
    }

    @GetMapping("/recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());
        return RECIPE_RECIPEFORM;
    }

    @GetMapping("/recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        Mono<RecipeCommand> recipe = recipeService.findCommandById(id);
        model.addAttribute("recipe", recipe);
        return RECIPE_RECIPEFORM;
    }

    @PostMapping("/recipe")
    public Mono<String> saveOrUpdate(@Valid @ModelAttribute("recipe") Mono<RecipeCommand> command) {
        return command
                .flatMap(recipeService::saveRecipeCommand)
                .map(recipe -> "redirect:/recipe/" + recipe.getId() + "/show")
                .doOnError(thr -> log.error("Error saving recipe"))
                .onErrorResume(WebExchangeBindException.class, thr -> Mono.just(RECIPE_RECIPEFORM));
    }

    @GetMapping("/recipe/{id}/delete")
    public Mono<String> deleteRecipe(@PathVariable String id, Model model){
        log.debug("Deleting recipe {}", id);
        return recipeService.deleteById(id)
                .thenReturn("redirect:/");
    }

    // TODO: replace this
//    @ExceptionHandler(NotFoundException.class)
//    public ModelAndView handleNotFound(Exception exc) {
//        log.error("Handling not found exception", exc);
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setStatus(HttpStatus.NOT_FOUND);
//        modelAndView.setViewName("error404");
//        modelAndView.addObject("exception", exc);
//        return modelAndView;
//    }

}
