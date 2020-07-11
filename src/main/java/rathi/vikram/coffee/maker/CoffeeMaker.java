package rathi.vikram.coffee.maker;

import rathi.vikram.coffee.maker.input.MachineConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

public class CoffeeMaker {

    private static final Logger LOG = Logger.getLogger(CoffeeMaker.class.getName());
    private static final long MILLIS_TO_PREPARE_A_BEVERAGE = TimeUnit.SECONDS.toMillis(5);

    private boolean onState;
    private int outlets;
    private BeverageConfig beverageConfig;
    private ConcurrentHashMap<String, Integer> stock;
    private ExecutorService brewingService;


    public CoffeeMaker(String resourcesPath) {
        MachineConfig config = MachineConfigLoader.getInstance().loadConfig(resourcesPath);

        if (config.machine.outlets.count <= 0) {
            throw new IllegalArgumentException("Number of outlets should be > 0");
        }

        this.outlets = config.machine.outlets.count;
        beverageConfig = new BeverageConfig(config.machine.beverageCompositions);
        stock = new ConcurrentHashMap<>(config.machine.ingredients);
        brewingService = Executors.newFixedThreadPool(outlets, new NamedThreadFactory(CoffeeMaker.class, "outlets"));
    }

    /**
     * FIRST COME, FIRST SERVE basis
     * @param beverages
     */
    public void makeBeverages(List<String> beverages) {
        if (!isOn()) {
            LOG.info("CoffeeMaker is not on, please switch it on first.");
            return;
        }

        for (String beverageName : beverages) {
            BeverageComposition beverageComposition = beverageConfig.getBeverageComposition(beverageName);

            if (beverageComposition == null) {
                LOG.log(Level.WARNING, "No composition data exists for beverage: " + beverageName);
                continue;
            }

            synchronized (this) {
                List<String> unavailableIngredients = checkIngredients(beverageComposition);

                if (unavailableIngredients.isEmpty()) {
                    prepareBeverage(beverageComposition);
                } else {
                    LOG.info(format("Can not serve %s because %s is not sufficient/available", beverageComposition.getName(),
                            String.join(", ", unavailableIngredients)));
                }
            }
        }
    }

    private List<String> checkIngredients(BeverageComposition beverageComposition) {
        List<String> unavailableIngredients = new LinkedList<>();
        for (Map.Entry<String, Integer> ingredient : beverageComposition.getIngredients().entrySet()) {
            if (stock.getOrDefault(ingredient.getKey(), 0) < ingredient.getValue()) {
                unavailableIngredients.add(ingredient.getKey());
            }
        }
        return unavailableIngredients;
    }

    private void prepareBeverage(BeverageComposition beverageComposition) {
        LOG.info("Finding outlet to prepare beverage " + beverageComposition.getName() + "...");
        for (Map.Entry<String, Integer> ingredient : beverageComposition.getIngredients().entrySet()) {
            int ingredientStock = stock.getOrDefault(ingredient.getKey(), 0);
            stock.put(ingredient.getKey(), ingredientStock - ingredient.getValue());
        }

        try {
            brewingService.submit(() -> {
                LOG.info("Preparing beverage " + beverageComposition.getName() + "...");
                try {
                    Thread.sleep(MILLIS_TO_PREPARE_A_BEVERAGE);
                } catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "Beverage was interrupted: " + beverageComposition.getName(), e);
                }
                LOG.info(beverageComposition.getName() + " is prepared");
            });
        } catch (RejectedExecutionException e) {
            LOG.log(Level.WARNING, "Unable to submit beverage because machine is off", e);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to prepare beverage", e);
        }
    }

    public void on() {
        synchronized (this) {
            onState = true;
            //loadStock(); Future improvement to load ingredient stock data
        }
    }

    public void off() {
        try {
            brewingService.awaitTermination(MILLIS_TO_PREPARE_A_BEVERAGE * 2,
                    TimeUnit.MILLISECONDS);
            brewingService.shutdown();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to shut off properly", e);
        } finally {
            synchronized (this) {
                //saveStock(); future improvement to save ingredient stock data
                onState = false;
            }
        }
    }

    public boolean isOn() {
        return onState;
    }

    public void refillIngredients(Map<String, Integer> ingredients) {
        synchronized (this) {
            for (Map.Entry<String, Integer> refill : ingredients.entrySet()) {
                String ingredientName = refill.getKey();
                int ingredientCurrent = stock.getOrDefault(ingredientName, 0);
                stock.put(ingredientName, ingredientCurrent + refill.getValue());
                LOG.info(format("Refilled ingredient: %s, last: %d -> current: %d",
                        ingredientName, ingredientCurrent, stock.get(ingredientName)));
            }
        }
    }

    public void showIngredients() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----CURRENT INGREDIENT STOCK-----\n");
        int count = 1;
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            sb.append(format("%d. %s : %d \n", count++, entry.getKey(), entry.getValue()));
        }

        LOG.info(sb.toString());
    }
}
