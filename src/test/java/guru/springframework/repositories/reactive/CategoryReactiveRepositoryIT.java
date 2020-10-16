package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryIT {

    @Autowired
    CategoryReactiveRepository repository;

    @Test
    public void testSaveCategory() {
        Category category = new Category();
        category.setDescription("Test Category #1");
        Category savedCategory = repository.save(category).block();
        assertEquals(category.getDescription(), savedCategory.getDescription());
    }

    @Test
    public void testFindByDescription() {
        Category category = new Category();
        category.setDescription("Test Category #1");
        repository.save(category).block();

        Category foundCategory = repository.findByDescription(category.getDescription()).block();
        assertEquals(category.getDescription(), foundCategory.getDescription());
    }

}
