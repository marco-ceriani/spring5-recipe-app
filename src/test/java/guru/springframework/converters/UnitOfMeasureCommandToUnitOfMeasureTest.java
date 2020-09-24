package guru.springframework.converters;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnitOfMeasureCommandToUnitOfMeasureTest {

    UnitOfMeasureCommandToUnitOfMeasure converter;

    @Before
    public void setUp() {
        converter = new UnitOfMeasureCommandToUnitOfMeasure();
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new UnitOfMeasureCommand()));
    }

    @Test
    public void testConvert() throws Exception {

        Long id = 3L;
        String description = "description";
        UnitOfMeasureCommand command = new UnitOfMeasureCommand();
        command.setId(id);
        command.setDescription(description);

        UnitOfMeasure converted = converter.convert(command);

        assertEquals(id, converted.getId());
        assertEquals(description, converted.getDescription());

    }
}