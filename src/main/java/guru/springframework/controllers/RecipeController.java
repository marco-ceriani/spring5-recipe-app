package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public Mono<String> showById(@PathVariable String id, Model model){
        return recipeService.findById(id)
                .doOnNext(recipe -> model.addAttribute("recipe", recipe))
                .map(rec -> "recipe/show")
                .switchIfEmpty(Mono.error(new NotFoundException("recipe not found: " + id)));
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
    public Mono<String> deleteRecipe(@PathVariable String id){
        log.debug("Deleting recipe {}", id);
        return recipeService.deleteById(id)
                .thenReturn("redirect:/");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(Exception exc, Model model) {
        log.error("Handling not found exception {}", exc.getMessage());
        model.addAttribute("exception", exc);
        return "error404";
    }

}
