package rathi.vikram.coffee.maker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CoffeeMakerTest {

    private static final Logger LOG = Logger.getLogger(CoffeeMakerTest.class.getSimpleName());

    @Before
    public void setUp() {
        LOG.info("setup()");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidOutlets() {
        new CoffeeMaker("input_invalid_outlets.json");
    }

    @Test
    public void refillIngredients() throws InterruptedException {
        CoffeeMaker coffeeMaker = new CoffeeMaker("input.json");
        coffeeMaker.on();

        List<String> beverages = new ArrayList<>();
        beverages.add("hot_tea");
        beverages.add("hot_coffee");
        coffeeMaker.makeBeverages(beverages);

        coffeeMaker.showIngredients();

        Map<String, Integer> refillIngredients = new HashMap<>();
        refillIngredients.put("green_mixture", 50);
        refillIngredients.put("sugar_syrup", 60);
        refillIngredients.put("hot_water", 200);

        coffeeMaker.refillIngredients(refillIngredients);
        coffeeMaker.showIngredients();

        beverages = new ArrayList<>();
        beverages.add("black_tea");
        beverages.add("green_tea");
        coffeeMaker.makeBeverages(beverages);
        coffeeMaker.showIngredients();

        Thread.sleep(15_000);
    }

    @Test
    public void makeSingleBeverage() {
        CoffeeMaker coffeeMaker = new CoffeeMaker("input.json");
        coffeeMaker.on();

        coffeeMaker.makeBeverages(Collections.singletonList("hot_tea"));

        coffeeMaker.showIngredients();
        coffeeMaker.off();
    }

    @Test
    public void ingredientOutOfStock() {
        CoffeeMaker coffeeMaker = new CoffeeMaker("input.json");
        coffeeMaker.on();

        List<String> beverages = new ArrayList<>();
        beverages.add("hot_tea");
        beverages.add("hot_coffee");
        beverages.add("black_tea");
        beverages.add("green_tea");
        coffeeMaker.makeBeverages(beverages);

        coffeeMaker.showIngredients();
    }

    @Test
    public void beverageCompositionUnavailable() {
        CoffeeMaker coffeeMaker = new CoffeeMaker("input.json");
        coffeeMaker.on();

        coffeeMaker.makeBeverages(Collections.singletonList("hot_damn"));

        coffeeMaker.showIngredients();
    }
}