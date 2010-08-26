package log;

import javax.swing.*;

/**
 * @author Roman Alekseenkov
 */
public class LogWindow extends JFrame {

    private JTextArea textArea;

    public LogWindow(String title, int width, int height) {
        super(title);
        setSize(width, height);
        textArea = new JTextArea();
        JScrollPane pane = new JScrollPane(textArea);
        getContentPane().add(pane);
        setVisible(true);
    }

    /**
     * This method appends the data to the text area.
     *
     * @param data the Logging information data
     */
    public void showInfo(String data) {
        textArea.append(data);
        this.getContentPane().validate();
    }
}

/**
 * @author Roman Alekseenkov
 */
class WindowHandler {

    // the window to which the logging is done
    private LogWindow window = null;

    // the singleton instance
    private static WindowHandler handler = null;

    /**
     * private constructor, preventing initialization
     */
    private WindowHandler() {
        if (window == null) {
            window = new LogWindow("serato-itch-sync logging window", 650, 350);
        }
    }

    /**
     * The getInstance method returns the singleton instance of the
     * WindowHandler object It is synchronized to prevent two threads trying to
     * create an instance simultaneously. @ return WindowHandler object
     *
     * @return window handler
     */
    public static synchronized WindowHandler getInstance() {
        if (handler == null) {
            handler = new WindowHandler();
        }
        return handler;
    }

    /**
     * This method writes the logging information to the associated
     * Java window. This method is synchronized to make it thread-safe.
     *
     * @param message The message to diplay
     */
    public synchronized void publish(String message) {
        window.showInfo(message + "\n");
    }

    /**
     * Reports a fatal error
     */
    public void fatalError() {
        JOptionPane.showMessageDialog(window,
                "Error occured. Please inspect main window for details.",
                "Failure", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Reports success
     */
    public void success() {
        JOptionPane.showMessageDialog(window,
                "Sync process is completed! See main window for details",
                "Success!", JOptionPane.INFORMATION_MESSAGE);
    }

}