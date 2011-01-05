package config;

import log.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Roman Alekseenkov
 */
public class Config {

    public static final String CONFIG_FILE = "itch-sync.properties";
    public static final String CONFIG_FILE_NOOB_USER = "itch-sync.properties.txt";

    private Properties properties;

    public Config() throws IOException {
        properties = new Properties();

        FileInputStream in = null;
        try {
            in = new FileInputStream(CONFIG_FILE);
        } catch (FileNotFoundException e) {
            in = new FileInputStream(CONFIG_FILE_NOOB_USER);
        }
        properties.load(in);
        in.close();
    }

    public boolean isGuiMode() {
        return getBooleanConfigOptionUsingFalseValue(properties, "mode", "cmd");
    }

    public boolean isClearLibraryBeforeSync() {
        return getBooleanConfigOption(properties, "music.library.itch.clear-before-sync");
    }

    public String getMusicLibraryPath() {
        return getRequiredConfigOption(properties, "music.library.filesystem");
    }

    public String getItchLibraryPath() {
        return getRequiredConfigOption(properties, "music.library.itch");
    }

    private static boolean getBooleanConfigOptionUsingFalseValue(Properties config, String name, String falseValue) {
        String result = config.getProperty(name);
        return !falseValue.equals(result);
    }

    private static boolean getBooleanConfigOption(Properties config, String name) {
        return "true".equals(config.getProperty(name));
    }

    private static String getRequiredConfigOption(Properties config, String name) {
        String result = config.getProperty(name);
        if (result == null || result.trim().length() <= 0) {
            Log.error("The required config option '" + name + "' is not specified");
            Log.fatalError();
        }
        return result;
    }

}
