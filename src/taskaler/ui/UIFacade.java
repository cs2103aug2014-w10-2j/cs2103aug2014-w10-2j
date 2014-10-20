/**
 * 
 */
package taskaler.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.JOptionPane;

import taskaler.common.data.Task;
import taskaler.ui.controller.RootController;
import taskaler.ui.hook.DLLConnector;
import taskaler.ui.model.RootModel;

/**
 * Class to act as the facade for the UI component
 * 
 * @author Cheah Kit Weng, A0059806W
 *
 */
public class UIFacade extends Application implements Observer {
    // Special Constants
    private static final String TITLE = "Taskaler";
    private static final String DEFAULT_VIEW_CALENDAR = "CALENDAR";
    private static final String DEFAULT_VIEW_LIST = "LIST";
    private static final String[] DEFAULT_VIEW_OPTIONS = {
            DEFAULT_VIEW_CALENDAR, DEFAULT_VIEW_LIST };

    // User Profile Configs
    private static String userDefaultView = DEFAULT_VIEW_CALENDAR;

    // File Constants
    private static final String ICON_PNG = "/res/icon.png";
    private static final String DLL_PATH_32 = "/JNILibrary";
    private static final String DLL_PATH_64 = "/JNILibrary64";

    // Class Variables
    private RootController rootController = null;
    private Stage primaryStage = null;

    @Override
    public void start(Stage stage) {
        try {
            userDefaultView = (String) JOptionPane.showInputDialog(null,
                    "Choose a default view", "Default View Option",
                    JOptionPane.INFORMATION_MESSAGE, null,
                    DEFAULT_VIEW_OPTIONS, DEFAULT_VIEW_OPTIONS[0]);
            createHook();
            stage.getIcons().add(
                    new Image(getClass().getResourceAsStream(ICON_PNG)));
            stage.setTitle(TITLE);
            stage.setResizable(false);

            RootModel model = new RootModel();
            rootController = new RootController(model);
            Parent pane = rootController;
            Scene scene = new Scene(pane, 400, 475);

            stage.setScene(scene);
            stage.sizeToScene();

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    DLLConnector.isStopped.set(true);
                }
            });

            stage.show();
            primaryStage = stage;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void createHook() {
        try {
            String jvmVer = System.getProperty("sun.arch.data.model");
            if (jvmVer.compareTo("32") == 0) {
                System.loadLibrary(DLL_PATH_32);
            } else if (jvmVer.compareTo("64") == 0) {
                System.loadLibrary(DLL_PATH_64);
            }else{
                throw new Exception("Unknown JVM version detected");
            }

            // Create the source of the key press
            final DLLConnector eventSource = new DLLConnector();

            // subscribe the observer to the event source
            eventSource.addObserver(this);

            // starts the event thread
            Thread thread = new Thread(eventSource);
            thread.start();
        } catch (UnsatisfiedLinkError err) {
            System.out
                    .println("Incorrect library configurations due to UnsatisfiedLinkException");
        } catch(Exception err){
            System.out.println("Unknown Error Found while creating hook");
        }
    }

    /**
     * Method to render default view for the list of task
     */
    public void display(ArrayList<Task> list) {
        display(userDefaultView, list);
    }

    /**
     * Method to render a view, either in list or calendar.
     * 
     */
    public void display(String args, ArrayList<Task> list) {
        if (list == null) {
            list = new ArrayList<Task>();
        }
        try {
            if (args.compareToIgnoreCase(DEFAULT_VIEW_LIST) == 0) {
                rootController.displayList("All current tasks", list);
            } else if (args.compareToIgnoreCase(DEFAULT_VIEW_CALENDAR) == 0) {
                rootController.displayCalendar(list, Calendar.getInstance());
            } else {
                rootController.displayList(args, list);
            }
        } catch (IOException e) {
            rootController.showToast("IO error encountered!");
            e.printStackTrace();
        }
    }

    /**
     * Method to render a view in Task view.
     * 
     */
    public void display(Task t) {
        if (t == null) {
            rootController.showToast("No task to be displayed");
        } else {
            try {
                rootController.displayTask(t);
            } catch (IOException e) {
                rootController.showToast("IO error encountered!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to show the toast notification.
     * 
     * @param t
     *            String to be displayed
     */
    public void display(String t) {
        if (t != null && !t.isEmpty()) {
            rootController.showToast(t);
        }
    }

    public void onClose() {

    }

    @Override
    public void update(Observable arg0, Object arg1) {
        if (arg1 instanceof String) {
            if (((String) arg1).compareToIgnoreCase("WM_HOTKEY") == 0) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        rootController.giveFocus();
                        primaryStage.setIconified(false);
                        primaryStage.toFront();
                    }
                });
            } else {
                System.out.println("No Idea who called this event");
            }
        }
    }
}
