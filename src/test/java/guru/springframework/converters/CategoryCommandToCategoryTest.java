package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.domain.Category;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CategoryCommandToCategoryTest {

    CategoryCommandToCategory converter;

    @Before
    public void setUp() {
        converter = new CategoryCommandToCategory();
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new CategoryCommand()));
    }

    @Test
    public void testConvert() throws Exception {

        String id = "3";
        String description = "description";
        CategoryCommand command = new CategoryCommand();
        command.setId(id);
        command.setDescription(description);

        Category converted = converter.convert(command);

        assertEquals(id, converted.getId());
        assertEquals(description, converted.getDescription());
    }
}