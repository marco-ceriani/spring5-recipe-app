package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class IngredientController {

    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService, UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model) {
        log.debug("Getting ingredient list for recipe {}", recipeId);

        model.addAttribute("recipe", recipeService.findCommandById(recipeId));

        return "recipe/ingredient/list";
    }

    @GetMapping("/recipe/{recipeId}/ingredient/{id}/show")
    public String listIngredients(@PathVariable String recipeId,
                                  @PathVariable String id,
                                  Model model) {
        log.debug("Getting ingredient list for recipe {}", recipeId);

        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));

        return "recipe/ingredient/show";
    }

    @GetMapping(value = "/recipe/{recipeId}/ingredient/new")
    public String newIngredient(@PathVariable String recipeId, Model model) {
        Mono<IngredientCommand> ingredient = recipeService.findCommandById(recipeId)
                .map(recipeCommand -> {
                    IngredientCommand ingredientCommand = new IngredientCommand();
                    ingredientCommand.setRecipeId(recipeCommand.getId());
                    ingredientCommand.setUnitOfMeasure(new UnitOfMeasureCommand());
                    return ingredientCommand;
                });
        model.addAttribute("ingredient", ingredient);

        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping(value = "/recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id,
                                         Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms().collectList());

        return "recipe/ingredient/ingredientform";
    }

    @PostMapping(value = "/recipe/{recipeId}/ingredient")
    public Mono<String> saveOrUpdate(@ModelAttribute IngredientCommand command, @PathVariable String recipeId) {
        command.setRecipeId(recipeId);
        return ingredientService.saveIngredient(command)
                .doOnNext(sc -> log.debug("saved recipe id:{} and ingredient id:{}", command.getRecipeId(),
                        command.getId()))
                .map(sc -> "redirect:/recipe/" + sc.getRecipeId() + "/ingredient/" + sc.getId() + "/show");
    }

    @GetMapping(value = "/recipe/{recipeId}/ingredient/{id}/delete")
    public Mono<String> deleteIngredient(@PathVariable String recipeId, @PathVariable String id) {
        log.debug("removing ingredient {} from recipe id:{}", id, recipeId);
        return ingredientService.deleteById(recipeId, id).toProcessor()
            .thenReturn("redirect:/recipe/" + recipeId + "/ingredients");
    }
}
