package guru.springframework.repositories.reactive;

import guru.springframework.domain.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryIT {

    @Autowired
    RecipeReactiveRepository repository;

    @Before
    public void setUp() {
        repository.deleteAll().block();
    }

    @Test
    public void testSaveRecipe() {
        Recipe recipe = new Recipe();
        recipe.setDescription("Yummy recipe");

        Recipe savedRecipe = repository.save(recipe).block();
        assertEquals(savedRecipe.getDescription(), savedRecipe.getDescription());

        assertEquals(Long.valueOf(1), repository.count().block());
    }

}
