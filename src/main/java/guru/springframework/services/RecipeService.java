package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;

import java.util.Set;

public interface RecipeService {

    public Set<Recipe> listRecipes();

    Recipe findById(long l);

    RecipeCommand findCommandById(long l);

    RecipeCommand saveRecipeCommand(RecipeCommand command);
}
