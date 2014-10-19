
package taskaler.controller;

import taskaler.undo.UndoFunction;
import taskaler.archive.history;
import taskaler.logic.OPLogic;
import taskaler.logic.SearchLogic;
import taskaler.storage.Storage;
import taskaler.ui.UIFacade;
import taskaler.common.data.Task;
import taskaler.common.data.TaskList;
import taskaler.controller.Parser.CmdType;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Brendan
 *
 */
public class Controller {

    private static final String TASK_LIST_FILE = "task_list";

    private static UIFacade ui = null;

    private static TaskList list = null;

    private static Controller instance = null;
    
    private static UndoFunction undoStack = null;

    /*********************************** Public Functions ***********************************/

    /**
     * 
     * Method to parse user commands, and pass parameters to UI, Storage and
     * Logic
     * 
     * @param commandString
     */
    public void executeCMD(String commandString) {
        try {
            Parser values = new Parser(commandString);
            CmdType commandType = values.getCommand();
            String[] params = values.getParameters();
            Task result = null;
            switch (commandType) {
            case ADD:
                String name_ADD = params[0];
                String description_ADD = params[1];
                result = OPLogic.addTask(name_ADD, description_ADD);
                undoStack.saveOperation(result, "ADD");
                ui.display(result);
                break;
            case DELETE:
                String taskID_DELETE = params[0];
                assert (taskID_DELETE != null);
                result = OPLogic.deleteTask(taskID_DELETE);
                undoStack.saveOperation(result, "DELETE");
                String name_DELETED = result.getTaskName();
                ui.display("The task \"" + name_DELETED
                        + "\" has been deleted.",
                        list.toArray(new ArrayList<Task>()));
                break;
            case EDIT:
                String taskID_EDIT = params[0];
                String name_EDIT = params[1];
                String description_EDIT = params[2];
                assert (taskID_EDIT != null);
                result = OPLogic.editTask(taskID_EDIT, name_EDIT,
                        description_EDIT);
                undoStack.saveOperation(result, "EDIT");
                ui.display(result);
                break;
            case DATE:
                String taskID_DATE = params[0];
                String day = params[1];
                String month = params[2];
                String year = params[3];
                assert (taskID_DATE != null);
                result = OPLogic.editDate(taskID_DATE, day, month, year);
                ui.display(result);
                break;
            case WORKLOAD:
                String taskID_WORKLOAD = params[0];
                String workloadAttribute = params[1];
                assert (taskID_WORKLOAD != null);
                result = OPLogic.editWorkload(taskID_WORKLOAD,
                        workloadAttribute);
                ui.display(result);
                break;
            case COMPLETION_TAG:
                String taskID_CT = params[0];
                assert (taskID_CT != null);
                result = OPLogic.switchTag(taskID_CT);
                ui.display(result);
                break;
            case VIEW:
                if (params[0] != null && params[0].equals("TASK")) {
                    result = SearchLogic.findByID(params[1]);
                    ui.display(result);
                } else {
                    ui.display(params[0], list.toArray(new ArrayList<Task>()));
                }
                break;
            case FIND:
                String tagTypeFIND = params[0];
                String toSearch = params[1];
                ArrayList<Task> searchResult = SearchLogic.find(tagTypeFIND,
                        toSearch);
                ui.display("Search Result for " + toSearch, searchResult);
                break;
            case ARCHIVE:
                String date = params[0];
                history.retrieveHistory(date);
                break;
            case UNDO:
                undoStack.undo();
                ui.display("LIST", list.toArray(new ArrayList<Task>()));
                break;
            case INVALID:
                throw new Exception("Invalid Command");
            default:
                throw new Error("Unknown Error");
            }
            Storage.writeToFile(TASK_LIST_FILE, list.toArray(new ArrayList<Task>()));
        } catch (AssertionError e) {
            Error er = new Error("Task ID was invalid");
            handleError(er);
        } catch (Exception e) {
            handleError(e);
        }
    }

    /**
     * Method to handle Errors
     * 
     * @param err
     *            Error to be handled
     */
    private static void handleError(Error err) {
        ui.display(err.getMessage());
    }

    /**
     * Method to handle Exceptions
     * 
     * @param err
     *            Exception to be handled
     */
    private static void handleError(Exception err) {
        ui.display(err.getMessage());
    }

    /**
     * Method to get a static instance of Controller object
     * 
     * @return A static reference of a Controller object
     */
    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /**
     * Private default constructor
     * 
     */
    private Controller() {
        list = TaskList.getInstance();
        list.addAll(Storage.readFromFile(TASK_LIST_FILE));
        ui = new UIFacade();
        undoStack = new UndoFunction();
    }

    /**
     * Method to start the UI
     * 
     * @param primaryStage
     *            Window of the application
     * @throws IOException
     *             Thrown if an IO error is encountered while rendering the UI
     */
    public void start(Stage primaryStage) throws IOException {
        // System.out.println(getTest());
        // taskList = Storage.readFromFile(file);
        // if (taskList == null) {
        // taskList = new ArrayList<Task>();
        // }
        ui.start(primaryStage);
        ui.display(list.toArray(new ArrayList<Task>()));
    }
}
