package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe,
                             RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> listRecipes() {
        log.debug("listRecipes");
        return recipeRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id) {
        return recipeRepository.findById(id)
                .doOnError(thr -> log.warn("Error reading Recipe. ID value: {}", id, thr));
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> findCommandById(String id) {
        return findById(id)
                .map(recipeToRecipeCommand::convert)
                .doOnError(thr -> log.warn("Recipe Not Found. ID value: " + id));
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        Recipe recipe = recipeCommandToRecipe.convert(command);
        return recipeRepository.save(recipe)
                .map(recipeToRecipeCommand::convert)
                .doOnNext(savedRecipe -> log.debug("Saved recipe {}", savedRecipe.getId()));
    }

    @Override
    public Mono<Void> deleteById(String idToDelete) {
        return recipeRepository.deleteById(idToDelete)
                .doOnSuccess(tmp -> log.info("Deleted recipe {}", idToDelete));
    }
}
