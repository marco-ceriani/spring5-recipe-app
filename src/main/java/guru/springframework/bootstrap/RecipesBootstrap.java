package guru.springframework.bootstrap;

import guru.springframework.domain.*;
import guru.springframework.repositories.reactive.CategoryReactiveRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
@Profile("default")
public class RecipesBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final RecipeReactiveRepository recipeRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;
    private final CategoryReactiveRepository categoryRepository;

    public RecipesBootstrap(RecipeReactiveRepository recipeRepository, UnitOfMeasureReactiveRepository unitOfMeasureRepository, CategoryReactiveRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Loading boostrap data");

        recipeRepository.deleteAll()
            .concatWith(unitOfMeasureRepository.deleteAll().and(categoryRepository.deleteAll()))
            .then()
            .block();

        loadCategories()
            .zipWith(loadUnitOfMeasures())
            .block();
        saveRecipes().then().block();
    }

    private Mono<Void> loadCategories() {
        List<Category> categories = new ArrayList<>();
        Category cat1 = new Category();
        cat1.setDescription("American");
        categories.add(cat1);

        Category cat2 = new Category();
        cat2.setDescription("Italian");
        categoryRepository.save(cat2);
        categories.add(cat2);

        Category cat3 = new Category();
        cat3.setDescription("Mexican");
        categories.add(cat3);

        Category cat4 = new Category();
        cat4.setDescription("Fast Food");
        categories.add(cat4);

        Category cat5 = new Category();
        cat5.setDescription("Japanese Food");
        categories.add(cat5);

        return categoryRepository.saveAll(categories).then();
    }

    private Mono<Void> loadUnitOfMeasures() {
        List<UnitOfMeasure> units = new ArrayList<>();
        UnitOfMeasure uom1 = new UnitOfMeasure();
        uom1.setDescription("Teaspoon");
        units.add(uom1);

        UnitOfMeasure uom2 = new UnitOfMeasure();
        uom2.setDescription("Tablespoon");
        units.add(uom2);

        UnitOfMeasure uom3 = new UnitOfMeasure();
        uom3.setDescription("Cup");
        units.add(uom3);

        UnitOfMeasure uom4 = new UnitOfMeasure();
        uom4.setDescription("Pinch");
        units.add(uom4);

        UnitOfMeasure uom5 = new UnitOfMeasure();
        uom5.setDescription("Ounce");
        units.add(uom5);

        UnitOfMeasure uom6 = new UnitOfMeasure();
        uom6.setDescription("Each");
        units.add(uom6);

        UnitOfMeasure uom7 = new UnitOfMeasure();
        uom7.setDescription("Pint");
        units.add(uom7);

        UnitOfMeasure uom8 = new UnitOfMeasure();
        uom8.setDescription("Dash");
        units.add(uom8);

        return unitOfMeasureRepository.saveAll(units).then();
    }

    private Flux<Recipe> saveRecipes() {
        return Flux.concat(insertGuacamoleRecipe(), createRecipeTacos())
                .collectList()
                .log()
                .flatMapMany(recipeRepository::saveAll)
                .doOnError(exc -> log.error("Error initializing recipes", exc));
    }

    private Mono<Recipe> insertGuacamoleRecipe() {
        log.debug("creating recipe for perfect guacamole");
        Recipe recipe = new Recipe();
        recipe.setDescription("Perfect guacamole");
        recipe.setPrepTime(10);
        recipe.setCookTime(0);
        recipe.setServings(3);
        recipe.setDifficulty(Difficulty.EASY);
        recipe.setSource("simplyrecipes");
        recipe.setUrl("https://www.simplyrecipes.com/recipes/perfect_recipe/");
        recipe.setDirections("For a very quick guacamole just take a 1/4 cup of salsa and mix it in with your mashed avocados.\n" +
                "Feel free to experiment! One classic Mexican guacamole has pomegranate seeds and chunks of peaches in it (a Diana Kennedy favorite). Try guacamole with added pineapple, mango, or strawberries.\n" +
                "The simplest version of guacamole is just mashed avocados with salt. Don't let the lack of availability of other ingredients stop you from making guacamole.\n" +
                "To extend a limited supply of avocados, add either sour cream or cottage cheese to your guacamole dip. Purists may be horrified, but so what? It tastes great.\n");
        Set<Category> categories = new HashSet<>();
        categories.add(categoryRepository.findByDescription("American").block());
        categories.add(categoryRepository.findByDescription("Mexican").block());
        recipe.setCategories(categories);

        Notes guacNotes = new Notes();
        guacNotes.setRecipeNotes("For a very quick recipe just take a 1/4 cup of salsa and mix it in with your mashed avocados.\n" +
                "Feel free to experiment! One classic Mexican recipe has pomegranate seeds and chunks of peaches in it (a Diana Kennedy favorite). Try recipe with added pineapple, mango, or strawberries.\n" +
                "The simplest version of recipe is just mashed avocados with salt. Don't let the lack of availability of other ingredients stop you from making recipe.\n" +
                "To extend a limited supply of avocados, add either sour cream or cottage cheese to your recipe dip. Purists may be horrified, but so what? It tastes great.\n" +
                "\n" +
                "\n" +
                "Read more: http://www.simplyrecipes.com/recipes/perfect_recipe/#ixzz4jvoun5ws");
        recipe.setNotes(guacNotes);

        return unitOfMeasureRepository.findAll()
                .collectMap(uom -> uom.getDescription(), uom -> uom)
                .map(units -> insertGuacamoleIngredients(recipe, units));
    }

    private Recipe insertGuacamoleIngredients(Recipe recipe, Map<String, UnitOfMeasure> units) {
        UnitOfMeasure tablespoonUom = units.get("Tablespoon");
        UnitOfMeasure teaspoonUom = units.get("Teaspoon");
        UnitOfMeasure dashUom = units.get("Dash");
        UnitOfMeasure numberUom = units.get("Each");
        recipe.addIngredient(new Ingredient("ripe avocado", BigDecimal.valueOf(2), numberUom));
        recipe.addIngredient(new Ingredient("salt", BigDecimal.valueOf(0.25), teaspoonUom));
        recipe.addIngredient(new Ingredient("salt", BigDecimal.valueOf(0.25), teaspoonUom));

        recipe.addIngredient(new Ingredient("fresh lime juice or lemon juice", new BigDecimal(2), tablespoonUom
        ));
        recipe.addIngredient(new Ingredient("minced red onion or thinly sliced green onion", new BigDecimal(2),
                tablespoonUom));
        recipe.addIngredient(new Ingredient("serrano chiles, stems and seeds removed, minced", new BigDecimal(2),
                numberUom));
        recipe.addIngredient(new Ingredient("Cilantro", new BigDecimal(2), tablespoonUom));
        recipe.addIngredient(new Ingredient("freshly grated black pepper", new BigDecimal(2), dashUom));
        recipe.addIngredient(new Ingredient("ripe tomato, seeds and pulp removed, chopped", new BigDecimal(".5"),
                numberUom));

        return recipe;
    }

    private Mono<Recipe> createRecipeTacos() {
        log.debug("creating recipe for spicy tacos");
        Recipe recipe = new Recipe();
        recipe.setDescription("Spicy Grilled Chicken Taco");
        recipe.setPrepTime(20);
        recipe.setCookTime(15);
        recipe.setServings(5);
        recipe.setDifficulty(Difficulty.MODERATE);
        recipe.setSource("simplyrecipes");
        recipe.setUrl("https://www.simplyrecipes.com/recipes/perfect_recipe/");
        recipe.setDirections("1 Prepare a gas or charcoal grill for medium-high, direct heat.\n" +
                "2 Make the marinade and coat the chicken: In a large bowl, stir together the chili powder, oregano, cumin, sugar, salt, garlic and orange zest. Stir in the orange juice and olive oil to make a loose paste. Add the chicken to the bowl and toss to coat all over.\n" +
                "Set aside to marinate while the grill heats and you prepare the rest of the toppings.\n" +
                "3 Grill the chicken: Grill the chicken for 3 to 4 minutes per side, or until a thermometer inserted into the thickest part of the meat registers 165F. Transfer to a plate and rest for 5 minutes.\n" +
                "4 Warm the tortillas: Place each tortilla on the grill or on a hot, dry skillet over medium-high heat. As soon as you see pockets of the air start to puff up in the tortilla, turn it with tongs and heat for a few seconds on the other side.\n" +
                "Wrap warmed tortillas in a tea towel to keep them warm until serving.\n" +
                "5 Assemble the tacos: Slice the chicken into strips. On each tortilla, place a small handful of arugula. Top with chicken slices, sliced avocado, radishes, tomatoes, and onion slices. Drizzle with the thinned sour cream. Serve with lime wedges.\n");
        List<Category> categories = Flux.concat(
                categoryRepository.findByDescription("American"),
                categoryRepository.findByDescription("Mexican"))
                .collectList().block();
        recipe.setCategories(new HashSet<>(categories));

        Notes notes = new Notes();
        notes.setRecipeNotes("We have a family motto and it is this: Everything goes better in a tortilla.\n" +
                "Any and every kind of leftover can go inside a warm tortilla, usually with a healthy dose of pickled jalapenos. I can always sniff out a late-night snacker when the aroma of tortillas heating in a hot pan on the stove comes wafting through the house.\n" +
                "Today’s tacos are more purposeful – a deliberate meal instead of a secretive midnight snack!\n" +
                "First, I marinate the chicken briefly in a spicy paste of ancho chile powder, oregano, cumin, and sweet orange juice while the grill is heating. You can also use this time to prepare the taco toppings.\n" +
                "Grill the chicken, then let it rest while you warm the tortillas. Now you are ready to assemble the tacos and dig in. The whole meal comes together in about 30 minutes!\n");
        recipe.setNotes(notes);

        return unitOfMeasureRepository.findAll()
                .collectMap(uom -> uom.getDescription(), uom -> uom)
                .map(units -> addTacosIngredients(recipe, units));
    }

    private Recipe addTacosIngredients(Recipe recipe, Map<String, UnitOfMeasure> units) {
        UnitOfMeasure tablespoonUom = units.get("Tablespoon");
        UnitOfMeasure teaspoonUom = units.get("Teaspoon");
        UnitOfMeasure cupsUom = units.get("Cup");
        UnitOfMeasure pintUom = units.get("Pint");
        UnitOfMeasure numberUom = units.get("Each");
        recipe.addIngredient(new Ingredient("Ancho Chili Powder", new BigDecimal(2), tablespoonUom));
        recipe.addIngredient(new Ingredient("Dried Oregano", new BigDecimal(1), teaspoonUom));
        recipe.addIngredient(new Ingredient("Dried Cumin", new BigDecimal(1), teaspoonUom));
        recipe.addIngredient(new Ingredient("Sugar", new BigDecimal(1), teaspoonUom));
        recipe.addIngredient(new Ingredient("Salt", new BigDecimal(".5"), teaspoonUom));
        recipe.addIngredient(new Ingredient("Clove of Garlic, Choppedr", new BigDecimal(1), numberUom));
        recipe.addIngredient(new Ingredient("finely grated orange zestr", new BigDecimal(1), tablespoonUom));
        recipe.addIngredient(new Ingredient("fresh-squeezed orange juice", new BigDecimal(3), tablespoonUom));
        recipe.addIngredient(new Ingredient("Olive Oil", new BigDecimal(2), tablespoonUom));
        recipe.addIngredient(new Ingredient("boneless chicken thighs", new BigDecimal(4), tablespoonUom));
        recipe.addIngredient(new Ingredient("small corn tortillasr", new BigDecimal(8), numberUom));
        recipe.addIngredient(new Ingredient("packed baby arugula", new BigDecimal(3), cupsUom));
        recipe.addIngredient(new Ingredient("medium ripe avocados, slic", new BigDecimal(2), numberUom));
        recipe.addIngredient(new Ingredient("radishes, thinly sliced", new BigDecimal(4), numberUom));
        recipe.addIngredient(new Ingredient("cherry tomatoes, halved", new BigDecimal(".5"), pintUom));
        recipe.addIngredient(new Ingredient("red onion, thinly sliced", new BigDecimal(".25"), numberUom));
        recipe.addIngredient(new Ingredient("Roughly chopped cilantro", new BigDecimal(4), numberUom));
        recipe.addIngredient(new Ingredient("cup sour cream thinned with 1/4 cup milk", new BigDecimal(4), cupsUom));
        recipe.addIngredient(new Ingredient("lime, cut into wedges", new BigDecimal(4), numberUom));
        return recipe;
    }

}
