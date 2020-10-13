package guru.springframework.domain;

import lombok.*;

import java.util.Set;

@Getter
@Setter
public class Category {

    private String id;
    private String description;

    @ToString.Exclude
    private Set<Recipe> recipes;

}
