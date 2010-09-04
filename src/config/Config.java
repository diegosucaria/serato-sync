package config;

import log.Log;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Roman Alekseenkov
 */
public class Config {

    public static final String CONFIG_FILE = "itch-sync.properties";

    private Properties properties;

    public Config() throws IOException {
        properties = new Properties();
        properties.load(new FileReader(CONFIG_FILE));
    }

    public boolean isGuiMode() {
        return getBooleanConfigOption(properties, "mode", "cmd");
    }

    public String getMusicLibraryPath() {
        return getRequiredConfigOption(properties, "music.library.filesystem");
    }

    public String getItchLibraryPath() {
        return getRequiredConfigOption(properties, "music.library.itch");
    }

    private static boolean getBooleanConfigOption(Properties config, String name, String falseValue) {
        String result = config.getProperty(name);
        return !falseValue.equals(result);
    }

    private static String getRequiredConfigOption(Properties config, String name) {
        String result = config.getProperty(name);
        if (result == null || result.trim().length() <= 0) {
            Log.error("The required config option '" + name + "' is not specified");
            System.exit(-1);
        }
        return result;
    }

}
