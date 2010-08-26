package log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Roman Alekseenkov
 */
public class Log {

    private static boolean GUI_INITIALIZED = false;
    private static boolean GUI_MODE = true;
    private static WindowHandler WINDOW_HANDLER;

    public static void info(String message) {
        initGui();
        if (GUI_MODE) {
            WINDOW_HANDLER.publish(message);
        } else {
            System.out.println(message);
            System.out.flush();
        }
    }

    public static void error(String message) {
        initGui();
        if (GUI_MODE) {
            WINDOW_HANDLER.publish(message);
        } else {
            System.err.println(message);
            System.err.flush();
        }
    }

    public static void error(Exception e) {
        initGui();
        if (GUI_MODE) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(out));
            WINDOW_HANDLER.publish(out.toString());
        } else {
            e.printStackTrace(System.err);
            System.err.flush();
        }
    }

    public static void fatalError() {
        initGui();
        if (GUI_MODE) {
            WINDOW_HANDLER.fatalError();
        }
    }

    public static void success() {
        initGui();
        if (GUI_MODE) {
            WINDOW_HANDLER.success();
        }
    }

    private static synchronized void initGui() {
        if (!GUI_INITIALIZED) {

            if (GUI_MODE) {
                try {
                    WINDOW_HANDLER = WindowHandler.getInstance();
                } catch (Exception e) {
                    // fallback to command-line mode
                    GUI_MODE = false;
                }
            }

            GUI_INITIALIZED = true;
        }
    }

    public static synchronized void setMode(boolean guiMode) {
        GUI_MODE = guiMode;
    }

}
