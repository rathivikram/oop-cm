package rathi.vikram.coffee.maker.input;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class MachineConfig {

    @SerializedName("machine")
    public Machine machine;

    public class Machine {
        @SerializedName("outlets")
        public Outlets outlets;

        @SerializedName("total_items_quantity")
        public Map<String, Integer> ingredients;


        @SerializedName("beverages")
        public Map<String, Map<String, Integer>> beverageCompositions;
    }

    public class Outlets {
        @SerializedName("count_n")
        public Integer count;
    }
}
