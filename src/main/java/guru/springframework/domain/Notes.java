package guru.springframework.domain;

import lombok.*;

@Getter
@Setter
public class Notes {

    private String id;
    @ToString.Exclude
    private Recipe recipe;
    private String recipeNotes;

}
