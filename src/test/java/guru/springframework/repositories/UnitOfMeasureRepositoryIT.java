package guru.springframework.repositories;

import guru.springframework.bootstrap.RecipesBootstrap;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureRepository repository;

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        recipeRepository.deleteAll();
        repository.deleteAll();
        categoryRepository.deleteAll();

        RecipesBootstrap recipesBootstrap = new RecipesBootstrap(recipeRepository, repository, categoryRepository);
        recipesBootstrap.onApplicationEvent(null);
    }

    @Test
    public void findByDecriptionTeaspoon() {

        Optional<UnitOfMeasure> unitOfMeasure = repository.findByDescription("Teaspoon");
        assertTrue(unitOfMeasure.isPresent());
        assertEquals("Teaspoon", unitOfMeasure.get().getDescription());
    }

    @Test
    public void findByDecriptionCup() {
        Optional<UnitOfMeasure> unitOfMeasure = repository.findByDescription("Cup");
        assertTrue(unitOfMeasure.isPresent());
        assertEquals("Cup", unitOfMeasure.get().getDescription());
    }
}