package guru.springframework.converters;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class IngredientCommandToIngredientTest {


    IngredientCommandToIngredient converter;
    private String id = "3";
    private String description = "description";
    private BigDecimal amount = BigDecimal.valueOf(10);
    private String uomId = "12";

    @Before
    public void setUp() {
        converter = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new IngredientCommand()));
    }

    @Test
    public void testConvert() throws Exception {
        IngredientCommand command = new IngredientCommand();
        command.setId(id);
        command.setAmount(amount);
        command.setDescription(description);
        UnitOfMeasureCommand uom = new UnitOfMeasureCommand();
        uom.setId(uomId);
        command.setUnitOfMeasure(uom);

        Ingredient converted = converter.convert(command);

        assertEquals(id, converted.getId());
        assertEquals(amount, converted.getAmount());
        assertEquals(description, converted.getDescription());
        assertNotNull(converted.getUnitOfMeasure());
        assertEquals(uomId, converted.getUnitOfMeasure().getId());
    }

    @Test
    public void testConvertWithNullUOM() throws Exception {
        IngredientCommand command = new IngredientCommand();
        command.setId(id);
        command.setAmount(amount);
        command.setDescription(description);

        Ingredient converted = converter.convert(command);

        assertEquals(id, converted.getId());
        assertEquals(amount, converted.getAmount());
        assertEquals(description, converted.getDescription());
        assertNull(converted.getUnitOfMeasure());
    }
}