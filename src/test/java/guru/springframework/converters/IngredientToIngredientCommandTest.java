package guru.springframework.converters;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class IngredientToIngredientCommandTest {


    IngredientToIngredientCommand converter;
    private String id = "3";
    private String description = "description";
    private BigDecimal amount = BigDecimal.valueOf(10);
    private String uomId = "12";

    @Before
    public void setUp() {
        converter = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new Ingredient()));
    }

    @Test
    public void testConvert() throws Exception {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setAmount(amount);
        ingredient.setDescription(description);
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setId(uomId);
        ingredient.setUnitOfMeasure(uom);

        IngredientCommand converted = converter.convert(ingredient);

        assertEquals(id, converted.getId());
        assertEquals(amount, converted.getAmount());
        assertEquals(description, converted.getDescription());
        assertNotNull(converted.getUnitOfMeasure());
        assertEquals(uomId, converted.getUnitOfMeasure().getId());
    }

    @Test
    public void testConvertWithNullUOM() throws Exception {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setAmount(amount);
        ingredient.setDescription(description);

        IngredientCommand converted = converter.convert(ingredient);

        assertEquals(id, converted.getId());
        assertEquals(amount, converted.getAmount());
        assertEquals(description, converted.getDescription());
        assertNull(converted.getUnitOfMeasure());
    }
}