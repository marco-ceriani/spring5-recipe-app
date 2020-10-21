package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        Recipe recipe = recipeService.findById(id).toProcessor().block();
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
        RecipeCommand recipe = recipeService.findCommandById(id).toProcessor().block();
        model.addAttribute("recipe", recipe);
        return RECIPE_RECIPEFORM;
    }

    @PostMapping("/recipe")
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return RECIPE_RECIPEFORM;
        }

        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command).toProcessor().block();

        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("/recipe/{id}/delete")
    public String deleteRecipe(@PathVariable String id, Model model){
        log.debug("Deleting recipe {}", id);
        recipeService.deleteById(id).toProcessor().block();
        return "redirect:/";
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
