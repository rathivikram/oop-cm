package rathi.vikram.coffee.maker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public final class BeverageConfig {

    Map<String, BeverageComposition> beverages = new HashMap<>();

    public BeverageConfig(Map<String, Map<String, Integer>> compositions) {
        for (Map.Entry<String, Map<String, Integer>> entry: compositions.entrySet()) {
            beverages.put(entry.getKey(),
                    new BeverageComposition(entry.getKey(), entry.getValue()));
        }
    }

    public BeverageComposition getBeverageComposition(String name) {
        return beverages.get(name);
    }
}
