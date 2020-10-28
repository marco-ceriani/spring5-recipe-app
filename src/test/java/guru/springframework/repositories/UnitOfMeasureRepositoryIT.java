package guru.springframework.repositories;

import guru.springframework.bootstrap.RecipesBootstrap;
import guru.springframework.repositories.reactive.CategoryReactiveRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureReactiveRepository repository;

    @Autowired
    private RecipeReactiveRepository recipeRepository;
    @Autowired
    private CategoryReactiveRepository categoryRepository;

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
        StepVerifier.create(repository.findByDescription("Teaspoon"))
                .assertNext(uom -> assertThat(uom.getDescription(), is("Teaspoon")));
    }

    @Test
    public void findByDecriptionCup() {
        StepVerifier.create(repository.findByDescription("Cup"))
                .assertNext(uom -> assertThat(uom.getDescription(), is("Cup")));
    }
}