package guru.springframework.converters;

import guru.springframework.commands.NotesCommand;
import guru.springframework.domain.Notes;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotesCommandToNotesTest {

    NotesCommandToNotes converter;

    @Before
    public void setUp() {
        converter = new NotesCommandToNotes();
    }

    @Test
    public void testNullParameter() throws Exception {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() throws Exception {
        assertNotNull(converter.convert(new NotesCommand()));
    }

    @Test
    public void testConvert() throws Exception {

        String id = "3";
        String recipeNotes = "description";
        NotesCommand command = new NotesCommand();
        command.setId(id);
        command.setRecipeNotes(recipeNotes);

        Notes converted = converter.convert(command);

        assertEquals(id, converted.getId());
        assertEquals(recipeNotes, converted.getRecipeNotes());
    }
}