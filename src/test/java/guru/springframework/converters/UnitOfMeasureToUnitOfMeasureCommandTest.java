package guru.springframework.converters;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnitOfMeasureToUnitOfMeasureCommandTest {

    UnitOfMeasureToUnitOfMeasureCommand converter;

    @Before
    public void setUp() {
        converter = new UnitOfMeasureToUnitOfMeasureCommand();
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new UnitOfMeasure()));
    }

    @Test
    public void testConvert() throws Exception {

        String id = "3";
        String description = "description";
        UnitOfMeasure command = new UnitOfMeasure();
        command.setId(id);
        command.setDescription(description);

        UnitOfMeasureCommand converted = converter.convert(command);

        assertEquals(id, converted.getId());
        assertEquals(description, converted.getDescription());

    }

}