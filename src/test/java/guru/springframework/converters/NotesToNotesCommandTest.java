package guru.springframework.converters;

import guru.springframework.commands.NotesCommand;
import guru.springframework.domain.Notes;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotesToNotesCommandTest {

    NotesToNotesCommand converter;

    @Before
    public void setUp() {
        converter = new NotesToNotesCommand();
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new Notes()));
    }

    @Test
    public void testConvert() throws Exception {

        String id = "3";
        String recipeNotes = "notes for the recipe";
        Notes notes = new Notes();
        notes.setId(id);
        notes.setRecipeNotes(recipeNotes);

        NotesCommand converted = converter.convert(notes);

        assertEquals(id, converted.getId());
        assertEquals(recipeNotes, converted.getRecipeNotes());
    }
}