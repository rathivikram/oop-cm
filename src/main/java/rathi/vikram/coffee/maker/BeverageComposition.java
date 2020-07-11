package rathi.vikram.coffee.maker;

import java.util.LinkedHashMap;
import java.util.Map;

public class BeverageComposition {

    private String name;
    private Map<String, Integer> ingredients;

    public BeverageComposition(String name, Map<String, Integer> ingredients) {
        this.name = name;
        this.ingredients = new LinkedHashMap<>(ingredients);
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getIngredients() {
        return ingredients;
    }
}
