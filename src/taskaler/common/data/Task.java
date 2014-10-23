/**
 * 
 */
package taskaler.common.data;

/*
 * @author Quek Jie Ping, Weng Yuan
 */
public abstract class Task {

    // Possible workload values
    public static final String WORKLOAD_NONE = "0";
    public static final String WORKLOAD_LOW = "1";
    public static final String WORKLOAD_MEDIUM = "2";
    public static final String WORKLOAD_HIGH = "3";

    public enum WorkloadProperty {
        NONE, LOW, MEDIUM, HIGH
    }

    private String _taskName;
    private String _taskID;
    private String _taskStatus;
    private WorkloadProperty _taskWorkLoad;
    private String _taskDescription;

    public Task() {
    }

    /**
     * Overloaded constructor to create a new Task object
     * 
     * @param taskName
     *            Name of the task
     * @param taskID
     *            ID of the task
     * @param taskStatus
     *            Status of the task
     * @param taskWorkLoad
     *            Workload of the task
     * @param taskDescription
     *            Description of the task
     */
    public Task(String taskName, String taskID, String taskStatus, 
    		String taskWorkLoad, String taskDescription) {

        _taskName = taskName;
        _taskID = taskID;
        _taskStatus = taskStatus;
        _taskWorkLoad = workloadFromString(taskWorkLoad);
        _taskDescription = taskDescription;

    }

    /**************** Accessors ***********************/
    public String getTaskName() {
        return _taskName;
    }

    public String getTaskID() {
        return _taskID;
    }

    public String getTaskStatus() {
        return _taskStatus;
    }

    public String getTaskWorkLoad() {
        return workloadToString(_taskWorkLoad);
    }

    public String getTaskDescription() {
        return _taskDescription;
    }

    /**************** Mutators ************************/
    public void changeTaskName(String newTaskName) {
        _taskName = newTaskName;
    }

    /*
     * TaskID may be unique
     * 
     * public void changeTaskID(String newTaskID) { _taskID = newTaskID; }
     */

    public void changeTaskStatus(String newTaskStatus) {
        _taskStatus = newTaskStatus;
    }

    public void changeTaskWorkLoad(String newTaskWordLoad) {
        _taskWorkLoad = workloadFromString(newTaskWordLoad);
    }

    public void changeTaskDescription(String newTaskDescription) {
        _taskDescription = newTaskDescription;
    }

    /**************** Class Methods ************************/
    /**
     *Abstract method to create a new task object with the same values
     *To be implemented by the subclass
     */
    public abstract Task clone();

    /**
     * Method to map a string to workload property
     * 
     * @param input
     *            String to be mapped
     * @return WorkloadProperty mapped to string
     */
    protected static WorkloadProperty workloadFromString(String input) {
        if (input.compareToIgnoreCase(WORKLOAD_HIGH) == 0) {
            return WorkloadProperty.HIGH;
        } else if (input.compareToIgnoreCase(WORKLOAD_MEDIUM) == 0) {
            return WorkloadProperty.MEDIUM;
        } else if (input.compareToIgnoreCase(WORKLOAD_LOW) == 0) {
            return WorkloadProperty.LOW;
        } else {
            return WorkloadProperty.NONE;
        }
    }

    /**
     * Method to convert a workload property to its string representation
     * 
     * @param workload
     *            Property to be converted
     * @return String representation of workload
     */
    protected static String workloadToString(WorkloadProperty workload) {
        if(workload == null){
            return WORKLOAD_NONE;
        }
        switch (workload) {
        case HIGH:
            return WORKLOAD_HIGH;
        case MEDIUM:
            return WORKLOAD_MEDIUM;
        case LOW:
            return WORKLOAD_LOW;
        default:
            return WORKLOAD_NONE;
        }
    }
}