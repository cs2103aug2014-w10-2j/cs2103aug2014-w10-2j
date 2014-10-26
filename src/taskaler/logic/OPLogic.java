package taskaler.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import taskaler.archive.OperationRecord;
import taskaler.common.data.DeadLineTask;
import taskaler.common.data.FloatTask;
import taskaler.common.data.RepeatedTask;
import taskaler.common.data.Task;
import taskaler.common.data.TaskList;
import taskaler.common.util.CommonLogger;
import taskaler.logic.common.RepeatPattern;

/**
 * @author Weng Yuan
 *
 */

public class OPLogic extends Observable {

    private static OPLogic instance = null;

    /**
     * Inaccessible default constructor
     */
    private OPLogic() {

    }

    /**
     * Method to get an exist instance of this object
     * 
     * @return An instance of this object
     */
    public static OPLogic getInstance() {
        if (instance == null) {
            instance = new OPLogic();
        }

        return instance;
    }
    
    /**
     * Special method for undo feature
     * 
     * @param t Task to be re-added into the list
     * @return Task that has been re-added
     */
    public Task addTask(Task t){
        TaskList.getInstance().add(t);
        notifyObservers("UNDO", t);
        return t;
    }
    
    /**
     * Special method for undo feature
     * 
     * @param t Task to be deleted from the list
     * @return Task that has been deleted
     */
    public Task deleteTask(Task t){
    	int taskIDIndex = SearchLogic.findTaskByID(t.getTaskID());

        if (taskIDIndex == common.TAG_TASK_NOT_EXIST) {
            // fail to delete a task
            return null;
        }

        Task taskToBeRemoved = TaskList.getInstance().remove(taskIDIndex);
        
        notifyObservers("UNDO", taskToBeRemoved);
        return t;
    }
    
    /**
     * Special method for undo feature
     * 
     * @param t Task to be overridden in the list
     * @return Task that has been overridden
     */
    public Task editTask(Task t){
    	int taskIDIndex = SearchLogic.findTaskByID(t.getTaskID());

        if (taskIDIndex == common.TAG_TASK_NOT_EXIST) {
            // fail to delete a task
            return null;
        }
        
        Task oldTask = TaskList.getInstance().remove(taskIDIndex);
        
        notifyObservers("UNDO", oldTask);
        return oldTask;
    }

    /**
     * addTask(String name_ADD, String description_ADD) will add a new task with
     * specified task name and description into taskList:ArrayList<Task>. New
     * added task will have a unique task ID, and task status will be set to Not
     * Done
     * 
     * @param name_ADD
     * @param description_ADD
     * @return return null if task name is null, return the newly added task
     *         otherwise.
     * 
     */
    public Task addTask(String name_ADD, String description_ADD, String date_ADD, String workload_ADD) {

        // assume task name cannot be null
        if (name_ADD == null) {
            // fail to add a new task
            return null;
        } else {

            if (description_ADD == null) {
                // change to default value
                description_ADD = common.TASK_PARAMETER_DEFAULT_VALUE;
            }
            
            if(workload_ADD == null){
                workload_ADD = common.TASK_PARAMETER_DEFAULT_VALUE;
            }
            // generate a new task ID
            int newTaskID = generateTaskID();

            Task newTask;
            if(date_ADD == null) {
            	 //float task
	            newTask = new FloatTask(name_ADD, Integer.toString(newTaskID),
	                    common.TASK_INITIAL_STATUS, Calendar.getInstance(),
	                    workload_ADD, description_ADD);
            } else {
            	//deadline task
				Calendar startTime = Calendar.getInstance();
				Calendar endTime = setNewCalendarDate(date_ADD);
				
				newTask = new DeadLineTask(name_ADD,
						Integer.toString(newTaskID),
						common.TASK_INITIAL_STATUS, Calendar.getInstance(), workload_ADD,
						description_ADD, startTime, endTime);
            }
            
            TaskList.getInstance().add(newTask);
            notifyObservers("ADD", newTask);

            return newTask;
        }

    }

    /**
     * 
     * generateTaskID() will generate a unique new task ID for
     * taskList:ArrayList<Task>
     * 
     * @return return new taskID
     */
    private int generateTaskID() {
        int taskID = TaskList.getInstance().maxTaskID();

        return taskID + common.OFF_SET_BY_ONE;
    }

    /**
     * 
     * Task deleteTask(String taskID_DELETE) is to delete a specified task with
     * given ID in taskList:ArrayList<Task>.
     * 
     * @param taskID_DELETE
     * @return return null if task ID not exist, otherwise, return the deleted
     *         task
     * 
     */
    public Task deleteTask(String taskID_DELETE) {
    	
    	int taskIDIndex = SearchLogic.findTaskByID(taskID_DELETE);

        if (taskIDIndex == common.TAG_TASK_NOT_EXIST) {
            // fail to delete a task
            return null;
        }

        Task taskToBeRemoved = TaskList.getInstance().remove(taskIDIndex);

        notifyObservers("DELETE", taskToBeRemoved);
        return taskToBeRemoved;

    }
    
    
    
    public boolean deleteAllTask() {
    	TaskList.getInstance().clear();
    	
    	return true;
    }

    /**
     * 
     * editTask(String taskID_EDIT, String name_EDIT, String description_EDIT)
     * is to edit a existing task with given ID in taskList:ArrayList<Task> by
     * given name and/or description
     * 
     * @param taskID_EDIT
     * @param name_EDIT
     * @param description_EDIT
     * @return return null if task ID not exist or both name and description
     *         given are null, edited task otherwise
     * 
     */
    public Task editTask(String taskID_EDIT, String name_EDIT,
            String description_EDIT) {

        int taskIDIndex = SearchLogic.findTaskByID(taskID_EDIT);

        if (isError(name_EDIT, description_EDIT, taskIDIndex)) {
            return null;
        }

        notifyObservers("EDIT", TaskList.getInstance().get(taskIDIndex));

        // assume name will not change to null
        if (name_EDIT != null) {
            TaskList.getInstance().get(taskIDIndex).changeTaskName(name_EDIT);
        }

        if (description_EDIT != null) {
            TaskList.getInstance().get(taskIDIndex)
                    .changeTaskDescription(description_EDIT);
        }

        return TaskList.getInstance().get(taskIDIndex);

    }

    /**
     * 
     * isError(String name_EDIT, String description_EDIT, int taskIDIndex) is to
     * for error checking. It will check validity of all the parameters given
     * 
     * @param name_EDIT
     * @param description_EDIT
     * @param taskIDIndex
     * @return return true if any of parameters cannot be use for editing, false
     *         otherwise
     * 
     */
    private static boolean isError(String name_EDIT, String description_EDIT,
            int taskIDIndex) {

        boolean errorFinder = false;

        if (taskIDIndex == common.TAG_TASK_NOT_EXIST) {
            errorFinder = true; // fail to edit a task
        } else if (name_EDIT == null && description_EDIT == null) {
            errorFinder = true; // fail to edit a task
        }

        return errorFinder;
    }

    /**
     * 
     * Task editDate(String taskID, String date) is to
     * edit a existing task with same given ID in taskList:ArrayList<Task> by
     * given name and/or description
     * 
     * @param taskID
     * @param date
     * @return return null if given task ID not exist, edited task otherwise
     */
    public Task editDate(String taskID, String date) {
        Calendar newDeadLine = setNewCalendarDate(date);
        int taskIDIndex = SearchLogic.findTaskByID(taskID);

        if (taskIDIndex == common.TAG_TASK_NOT_EXIST) {
            // fail to edit a task
            return null;
        }
        
        Task newTask = TaskList.getInstance().get(taskIDIndex);
        if (taskIDIndex < TaskList.getInstance().floatToArray().size()) {
        	//float task has no date attribute
        	//remove task from float task list and add to deadline task list
        	Task deletedTask = TaskList.getInstance().remove(taskIDIndex);
        	newTask = new DeadLineTask(deletedTask.getTaskName(),
					deletedTask.getTaskID(), common.TASK_INITIAL_STATUS,
					deletedTask.getTaskCreationDate(), deletedTask.getTaskWorkLoad(),
					deletedTask.getTaskDescription(), Calendar.getInstance(), newDeadLine);
        	TaskList.getInstance().add(newTask);
        	
        } else if (taskIDIndex < TaskList.getInstance().floatToArray().size() + TaskList.getInstance().deadlineToArray().size()) {
        	((DeadLineTask) newTask).setEndTime(newDeadLine);
       
        } else {
        	((RepeatedTask) newTask).setEndTime(newDeadLine);
        }

        notifyObservers("EDIT", newTask);

        

        return newTask;
    }
    
    /**
     * 
     * setNewCalenderDate(String date) is to create a new
     * calendar date will given day, month and year
     * 
     * @param day
     * @param month
     * @param year
     * @return return new calendar date
     */
    private static Calendar setNewCalendarDate(String date) {
        Calendar newDeadLine = Calendar.getInstance();
        try{
            newDeadLine.setTime(common.DEFAULT_DATE_FORMAT.parse(date));
        }
        catch(Exception e){
            ;
        }
        return newDeadLine;
    }
    
    /* This is a stub for the setRepeat function Controller will call for the "repeat" command
       parameters are as follows, syntax for "pattern" is in ParserLibrary.java if u need to 
       take a look, but briefly, for pattern, i will pass you:
        - to repeat every <num> days    : "d", "alter"
        - to repeat every <num> weeks   : "w"
        - to repeat every <num> months  : "m"
        - to repeat every <num> years   : "y"
        - to repeat on all weekdays     : "wd"
        - to repeat on all weekends     : "wk"
          ("1 dow" represents sunday, so on so forth, to match with the Calendar implementation) 
    */
    public Task setRepeat(String taskID, String pattern, String startDate, String endDate, String endRepeatedDate){
        Calendar startTime = setNewCalendarDate(startDate);
        Calendar endTime = setNewCalendarDate(endDate);
        Calendar endRepeatedTime = setNewCalendarDate(endRepeatedDate);
    	RepeatPattern repeatPattern = getPattern(pattern);
    	ArrayList<Calendar> repeatedDate = getRepeatDay(startTime, endRepeatedTime, repeatPattern);
        int taskIDIndex = SearchLogic.findTaskByID(taskID);
        
        if (taskIDIndex == common.TAG_TASK_NOT_EXIST || repeatPattern == RepeatPattern.NONE) {
            // fail to edit a task
            return null;
        }
        
        //remove task from float task list or deadline task list
        Task deletedTask = TaskList.getInstance().remove(taskIDIndex);
        
		Task newTask = new RepeatedTask(deletedTask.getTaskName(), deletedTask.getTaskID(),
				deletedTask.getTaskStatus(), deletedTask.getTaskCreationDate(), 
				deletedTask.getTaskWorkLoad(), deletedTask.getTaskDescription(),
				startTime, endTime, repeatedDate, endRepeatedTime, 
				TaskList.getInstance().repeatedToArray().size() + 1);
		
		TaskList.getInstance().add(newTask);
        return newTask;
    }
    
    private ArrayList<Calendar> getRepeatDay(Calendar startTime, Calendar endRepeatedTime, RepeatPattern repeatPattern) {
    	ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	switch(repeatPattern) {
    	case DAY:
    		repeatDays = computeDaily((Calendar) startTime.clone(), endRepeatedTime);
    		break;
    	case ALTER:
    		repeatDays = computeAlter((Calendar) startTime.clone(), endRepeatedTime);
    		break;
    	case WEEK:
    		repeatDays = computeWeekly((Calendar) startTime.clone(), endRepeatedTime);
    		break;
    	case WEEKDAY:
    		repeatDays = computeWeekday((Calendar) startTime.clone(), endRepeatedTime);
    		break;
    	case WEEKEND:
    		repeatDays = computeWeekend((Calendar) startTime.clone(), endRepeatedTime);
    		break;
    	case MONTH:
    		repeatDays = computeMonthly((Calendar) startTime.clone(), endRepeatedTime);
    		break;
    	case YEAR:
    		repeatDays = computeYearly((Calendar) startTime.clone(), endRepeatedTime);
    		break;
    	}
		return repeatDays;
	}

    
    private ArrayList<Calendar> computeYearly(Calendar startTime, Calendar endRepeatedTime) {
    	ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	while(startTime.compareTo(endRepeatedTime) <= 0) {
    		Calendar newDay = (Calendar) startTime.clone();
    		repeatDays.add(newDay);
            startTime.add(Calendar.YEAR, common.OFF_SET_BY_ONE);
    	}
		return repeatDays;
	}

	private ArrayList<Calendar> computeMonthly(Calendar startTime, Calendar endRepeatedTime) {
		ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	while(startTime.compareTo(endRepeatedTime) <= 0) {
    		Calendar newDay = (Calendar) startTime.clone();
    		repeatDays.add(newDay);
            startTime.add(Calendar.MONTH, common.OFF_SET_BY_ONE);
    	}
		return repeatDays;
	}

	private ArrayList<Calendar> computeWeekend(Calendar startTime, Calendar endRepeatedTime) {
		ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	while(startTime.compareTo(endRepeatedTime) <= 0) {
    		Calendar newDay = (Calendar) startTime.clone();
    		int day = newDay.get(Calendar.DAY_OF_WEEK);
    		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
    			repeatDays.add(newDay);
    		}
			startTime.add(Calendar.DAY_OF_MONTH, common.OFF_SET_BY_ONE);
    	}
		return repeatDays;
	}

	private ArrayList<Calendar> computeWeekday(Calendar startTime, Calendar endRepeatedTime) {
		ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	while(startTime.compareTo(endRepeatedTime) <= 0) {
    		Calendar newDay = (Calendar) startTime.clone();
    		int day = newDay.get(Calendar.DAY_OF_WEEK);
    		if (!(day == Calendar.SATURDAY || day == Calendar.SUNDAY)) {
    			repeatDays.add(newDay);
    		}
			startTime.add(Calendar.DAY_OF_MONTH, common.OFF_SET_BY_ONE);
    	}
		return repeatDays;
	}

	private ArrayList<Calendar> computeWeekly(Calendar startTime, Calendar endRepeatedTime) {
		ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	while(startTime.compareTo(endRepeatedTime) <= 0) {
    		Calendar newDay = (Calendar) startTime.clone();
    		repeatDays.add(newDay);
            startTime.add(Calendar.DAY_OF_MONTH, common.DAYS_IN_A_WEEK);
    	}
		return repeatDays;
	}

	private ArrayList<Calendar> computeAlter(Calendar startTime, Calendar endRepeatedTime) {
		ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	while(startTime.compareTo(endRepeatedTime) <= 0) {
    		Calendar newDay = (Calendar) startTime.clone();
    		repeatDays.add(newDay);
            startTime.add(Calendar.DAY_OF_MONTH, common.DAYS_OF_ALTER);
    	}
		return repeatDays;
	}

	private ArrayList<Calendar> computeDaily(Calendar startTime, Calendar endRepeatedTime) {
		ArrayList<Calendar> repeatDays = new ArrayList<Calendar>();
    	while(startTime.compareTo(endRepeatedTime) <= 0) {
    		Calendar newDay = (Calendar) startTime.clone();
    		repeatDays.add(newDay);
            startTime.add(Calendar.DAY_OF_MONTH, common.OFF_SET_BY_ONE);
    	}
		return repeatDays;
	}

	
	private RepeatPattern getPattern(String pattern) {
    	pattern = pattern.toUpperCase();
		RepeatPattern repeatPattern = RepeatPattern.NONE;
    	if(pattern.equals("DAY")) {
    		repeatPattern = RepeatPattern.DAY;
    	} else if (pattern.equals("ALTER")) {
    		repeatPattern = RepeatPattern.ALTER;
    	} else if (pattern.equals("WEEK")) {
    		repeatPattern = RepeatPattern.WEEK;
    	} else if (pattern.equals("WEEKDAY")) {
    		repeatPattern = RepeatPattern.WEEKDAY;
    	} else if (pattern.equals("WEEKEND")) {
    		repeatPattern = RepeatPattern.WEEKEND;
    	} else if (pattern.equals("MONTH")) {
    		repeatPattern = RepeatPattern.MONTH;
    	} else if (pattern.equals("YEAR")) {
    		repeatPattern = RepeatPattern.YEAR;
    	} else {
    		repeatPattern = RepeatPattern.NONE;
    	}
    	
		return repeatPattern;
	}

	/**
     * 
     * Task editWorkload(String taskID, String workloadAttribute) is to change
     * the workload attribute of a task with same given task ID in
     * taskList:ArrayList<Task> to new workload attribute
     * 
     * @param taskID
     * @param workloadAttribute
     * @return return null if given task ID not exist, edited task otherwise
     */
    public Task editWorkload(String taskID, String workloadAttribute) {
        int workloadAtt = Integer.parseInt(workloadAttribute);
        int taskIDIndex = SearchLogic.findTaskByID(taskID);

        if (taskIDIndex == common.TAG_TASK_NOT_EXIST) {
            // fail to edit a task
            return null;
        }

        notifyObservers("EDIT", TaskList.getInstance().get(taskIDIndex));

        // assume workloadAtt is within the range of 1-3
        TaskList.getInstance().get(taskIDIndex)
                .changeTaskWorkLoad(Integer.toString(workloadAtt));

        return TaskList.getInstance().get(taskIDIndex);
    }

    /**
     * 
     * switchTag(String taskID) will change the status of a task with given ID
     * in taskList:ArrayList<Task> to "Done"
     * 
     * @param taskID
     * @return return null if given task ID not exist, edited task otherwise
     */
    public Task switchTag(String taskID) {
        int taskIDIndex = SearchLogic.findTaskByID(taskID);

        if (taskIDIndex == common.TAG_TASK_NOT_EXIST) {
            // fail to edit a task
            return null;
        }

        notifyObservers("EDIT", TaskList.getInstance().get(taskIDIndex));

        toggleStatus(taskIDIndex);
        
        return TaskList.getInstance().get(taskIDIndex);
    }

    
	private void toggleStatus(int taskIDIndex) {
		
		if(TaskList.getInstance().get(taskIDIndex).getTaskStatus().equals(common.TASK_COMPLETED_STATUS)) {
        	TaskList.getInstance().get(taskIDIndex).changeTaskStatus(common.TASK_INITIAL_STATUS);
        } else {
        	TaskList.getInstance().get(taskIDIndex).changeTaskStatus(common.TASK_COMPLETED_STATUS);
        }
		
	}

	
    private void notifyObservers(String type, Task task) {
        setChanged();
        OperationRecord<Task, String> record = new OperationRecord<Task, String>(
                task.clone(), type);
        notifyObservers(record);
    }

}
