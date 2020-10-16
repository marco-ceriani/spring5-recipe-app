package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryIT {

    public static final String EACH = "Each";
    @Autowired
    UnitOfMeasureReactiveRepository repository;

    @Before
    public void setUp() {
        repository.deleteAll().block();
    }

    @Test
    public void testSaveUnitOfMeasure() {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setDescription(EACH);

        UnitOfMeasure savedUom = repository.save(uom).block();
        long count = repository.count().block();

        assertEquals(EACH, savedUom.getDescription());
        assertEquals(1L, count);
    }

    @Test
    public void testFindByDescription() {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setDescription(EACH);
        repository.save(uom).block();

        Category foundUom = repository.findByDescription(EACH).block();

        assertEquals(EACH, foundUom.getDescription());
    }

}
