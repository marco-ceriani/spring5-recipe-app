package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.domain.Category;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CategoryToCategoryCommandTest {

    CategoryToCategoryCommand converter;

    @Before
    public void setUp() {
        converter = new CategoryToCategoryCommand();
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new Category()));
    }

    @Test
    public void testConvert() throws Exception {

        String id = "3";
        String description = "description";
        Category command = new Category();
        command.setId(id);
        command.setDescription(description);

        CategoryCommand converted = converter.convert(command);

        assertEquals(id, converted.getId());
        assertEquals(description, converted.getDescription());
    }
}