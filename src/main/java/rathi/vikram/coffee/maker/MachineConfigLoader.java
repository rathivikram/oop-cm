package rathi.vikram.coffee.maker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rathi.vikram.coffee.maker.input.MachineConfig;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MachineConfigLoader {
    private static final Logger LOG = Logger.getLogger(MachineConfigLoader.class.getSimpleName());
    private static final Gson GSON = new GsonBuilder()
            .create();

    private static MachineConfigLoader instance;

    private MachineConfigLoader() {
    }

    public static MachineConfigLoader getInstance() {
        if (instance == null) {
            instance = new MachineConfigLoader();
        }

        return instance;
    }


    public MachineConfig loadConfig(String path) {
        try {
            InputStream configStream = this.getClass().getClassLoader().getResourceAsStream(path);
            MachineConfig config = GSON
                    .fromJson(new InputStreamReader(configStream), MachineConfig.class);
            assert config != null;
            assert config.machine != null;
            assert config.machine.outlets != null;

            return config;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to load config", e);
            throw new RuntimeException("Unable to load config", e);
        }
    }

}

