package guru.springframework.repositories;

import guru.springframework.domain.UnitOfMeasure;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UnitOfMeasureRepositoryIT {

    @Autowired
    UnitOfMeasureRepository repository;

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