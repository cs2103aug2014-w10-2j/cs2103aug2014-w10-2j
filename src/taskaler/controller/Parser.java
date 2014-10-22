package taskaler.controller;

import static taskaler.controller.common.*;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import taskaler.common.util.parser.calendarToString;

public class Parser {
    // Local Variables
    private static String currentTaskID;
    private CmdType command;
    private String[] parameters;
    
    /**
     * @param commandString
     * 
     * Contructor for Parser
     */
    public Parser() throws Exception{
        ;
    }
    
    /**
     * Method to retrieve the command type
     * 
     * @return CmdType
     */
    public CmdType getCommand(){
        return command;
    }
    
    /**
     * Method to retrieve parameters
     * 
     * @return String[] array of parameters
     */
    public String[] getParameters(){
        return parameters;
    }
    
    /**
     * Public function to parse the command string
     * 
     * @param commandString
     */
    public void parseCMD(String commandString, String taskID) throws Exception{
        String CMD = getFirstWord(commandString);
        command = determineCMD_TYPE(CMD);
        parameters = getParams(command, commandString);
        currentTaskID = taskID;
    }
    
    /**
     * 
     * Method to differentiate between CMD_TYPE(s) when given the user command 
     * 
     * @param command
     * @return CmdType command type
     */
    private CmdType determineCMD_TYPE(String command) {
        switch (command.toLowerCase()) {
        case "add":
            return CmdType.ADD;
        case "put":
            return CmdType.ADD;
        case "delete":
            return CmdType.DELETE;
        case "remove":
            return CmdType.DELETE;
        case "clear":
            return CmdType.DELETE;
        case "edit":
            return CmdType.EDIT;
        case "date":
            return CmdType.DATE;
        case "workload":
            return CmdType.WORKLOAD;
        case "completed":
            return CmdType.COMPLETION_TAG;
        case "done":
            return CmdType.COMPLETION_TAG;
        case "view":
            return CmdType.VIEW;
        case "find":
            return CmdType.FIND;
        case "search":
            return CmdType.FIND;
        case "arch":
            return CmdType.ARCHIVE;
        case "history":
            return CmdType.ARCHIVE;
        case "undo":
            return CmdType.UNDO;
        case "next":
            return CmdType.GOTO;
        case "back":
            return CmdType.GOTO;
        case "goto":
            return CmdType.GOTO;
        default:
            return CmdType.INVALID;
        }
    }
    
    /**
     * 
     * Method to get the parameters for each command, differentiated by CMD_TYPE
     * 
     * @param commandType
     * @param commandString
     * @return String[] parameters
     */
    private static String[] getParams(CmdType commandType, String commandString) throws Exception{
        switch(commandType){
        case ADD:
            return getParam_ADD(commandString);
        case DELETE:
            return getParam_DELETE(commandString);
        case EDIT:
            return getParam_EDIT(commandString);
        case DATE:
            return getParam_DATE(commandString);
        case WORKLOAD:
            return getParam_WL(commandString);
        case COMPLETION_TAG:
            String TaskID_CT = getTaskID(commandString);
            return new String[]{TaskID_CT};
        case VIEW:
            return getParam_VIEW(commandString);
        case FIND:
            return getParam_FIND(commandString);
        case ARCHIVE:
            return getParam_ARCH(commandString);
        case GOTO:
            return getParam_GOTO(commandString);
        default:
            return null;
        }
    }
    
    /**
     * 
     * Method to retrieve parameters for ADD command specifically
     * 
     * @param commandString
     * @return String[] parameters for ADD command
     */
    private static String[] getParam_ADD(String commandString) {
        int name_index = 0;
        int description_index = 1;

        String paramString = removeFirstWord(commandString);
        String[] paramADD = new String[MAX_ADD_PARAMETERS];
        if (paramString.isEmpty()) {
            return paramADD;
        }
        int descriptionTagIndex = paramString.toLowerCase().indexOf(" -d ");

        if (descriptionTagIndex == INVALID_VALUE) {
            paramADD[name_index] = paramString;
        } else {
            paramADD[name_index] = paramString.substring(0, descriptionTagIndex);
            paramADD[description_index] = paramString.substring(descriptionTagIndex + TAG_LENGTH);
        }
        return paramADD;
    }
    
    private static String[] getParam_DELETE(String commandString){
        int taskID_index = 0;
        String[] paramDELETE = new String[DELETE_PARAMETERS];
        String taskID = getTaskID(commandString);
        if(taskID.isEmpty()){
            paramDELETE[taskID_index] = currentTaskID;
        }
        else {
            paramDELETE[taskID_index] = taskID;
        }
        return paramDELETE;
    }
    /**
     * 
     * Method to retrieve TaskID from the user's command
     * 
     * @param commandString
     * @return String TaskID
     */
    private static String getTaskID(String commandString) {
        String TaskID = getFirstWord(removeFirstWord(commandString));
        return TaskID;
    }
    
    /**
     * 
     * Method to remove the command type and task id from the user's command
     * effectively getting the parameters for the command
     * 
     * @param commandString
     * @return String paramString
     */
    private static String removeCMD_N_TaskID(String commandString){
        return removeFirstWord(removeFirstWord(commandString));
    }

    /**
     * 
     * Method to retrieve parameters for EDIT command specifically
     * 
     * @param commandString
     * @return String[] parameters for EDIT command
     */
    private static String[] getParam_EDIT(String commandString) {
        int taskID_index = 0;
        int name_index = 1;
        int description_index = 2;
        String[] paramEDIT = new String[MAX_EDIT_PARAMETERS];
        String taskID = getTaskID(commandString);
        if(taskID.equals("-n") || taskID.equals("-d")){
            paramEDIT[taskID_index] = currentTaskID;
        }
        else {
           paramEDIT[taskID_index] = taskID; 
        }
        int nameTagIndex = commandString.toLowerCase().indexOf(" -n ");
        int descriptionTagIndex = commandString.toLowerCase().indexOf(" -d ");

        if (nameTagIndex > descriptionTagIndex) {
            paramEDIT[name_index] = 
                    commandString.substring(nameTagIndex + TAG_LENGTH);
            if (descriptionTagIndex != INVALID_VALUE) {
                paramEDIT[description_index] = 
                        commandString.substring(descriptionTagIndex + TAG_LENGTH, 
                                nameTagIndex);
            }
        } else if (nameTagIndex < descriptionTagIndex) {
            paramEDIT[description_index] = 
                    commandString.substring(descriptionTagIndex + TAG_LENGTH);
            if (nameTagIndex != INVALID_VALUE) {
                paramEDIT[name_index] = 
                        commandString.substring(nameTagIndex + TAG_LENGTH, 
                                descriptionTagIndex);
            }
        }
        return paramEDIT;
    }

    /**
     * 
     * Method to retrieve parameters for DATE command specifically
     * 
     * @param commandString
     * @return String[] parameters for DATE command
     */
    /*private static String[] getParam_DATE(String commandString) throws Exception {
        int taskID_index = 0;
        String date = removeCMD_N_TaskID(commandString);
        String[] paramDATE = new String[MAX_DATE_PARAMETERS];
        paramDATE[taskID_index] = getTaskID(commandString);
        
        String[] dateArray = date.split("/");
        if (dateArray.length != MAX_DATE_PARAMETERS-1) {
            throw new Exception("Invalid Format");
        } else {
            for (int i = 0; i < dateArray.length; i++) {
                try {
                    int num = Integer.parseInt(dateArray[i]);
                    paramDATE[i+1] = String.valueOf(num);
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid Number");
                }
            }
        }
        return paramDATE;
    }*/
    private static String[] getParam_DATE(String commandString) throws Exception{
        int taskID_index = 0;
        int date_index = 1;
        String[] paramArray = removeFirstWord(commandString).split("\\s+");
        String date = removeCMD_N_TaskID(commandString);
        String[] paramDATE = new String[DATE_PARAMETERS];
        if(paramArray.length == 1 || (paramArray.length == 3)){
            paramDATE[taskID_index] = currentTaskID;
            date = removeFirstWord(commandString);
        }
        else if(paramArray.length == 2){
            if(paramArray[1].matches("^.*[^a-zA-Z0-9 ].*$")){
                paramDATE[taskID_index] = getTaskID(commandString);
            }
            else {
                paramDATE[taskID_index] = currentTaskID;
                date = removeFirstWord(commandString);
            }
        }
        else if(paramArray.length == 4){
            paramDATE[taskID_index] = getTaskID(commandString);
        }
        else {
            throw new Exception("Invalid date syntax");
        }
        String dateInFormat = parseDate(date);
        paramDATE[date_index] = dateInFormat;
        return paramDATE;
    }

    /**
     * 
     * Method to retrieve parameters for WORKLOAD command specifically
     * 
     * @param commandString
     * @return String[] parameters for WORKLOAD command
     */
    private static String[] getParam_WL(String commandString) throws Exception{
        int taskID_index = 0;
        int workload_index = 1;
        String paramString = removeCMD_N_TaskID(commandString);
        String[] paramArray = new String[WORKLOAD_PARAMETERS];
        paramArray[taskID_index] = getTaskID(commandString);
        String paramWL = "0";
        try {
            int WL = Integer.parseInt(paramString);
            if(WL >= 1 && WL <= 3){
                paramWL = String.valueOf(WL);
            } 
            else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new Exception("Invalid Workload Attribute");
        }
        paramArray[workload_index] = paramWL;
        return paramArray;
    }

    /**
     * 
     * Method to retrieve parameters for VIEW command specifically
     * 
     * @param commandString
     * @return String[] parameters for VIEW command
     */
    private static String[] getParam_VIEW(String commandString) {
        String paramString = removeFirstWord(commandString);
        String[] paramArray = new String[VIEW_PARAMETERS];
        if (paramString.equalsIgnoreCase("-l") ||
                paramString.equalsIgnoreCase("list") || 
                paramString.equalsIgnoreCase("all")) {
            paramArray[0] =  "LIST";
        } else if (paramString.equalsIgnoreCase("-c") ||
                paramString.equalsIgnoreCase("calendar")) {
            paramArray[0] = "CALENDAR";
        } else {
            paramArray[0] = "TASK";
            paramArray[1] = paramString;
        }
        return paramArray;
    }

    /**
     * 
     * Method for retrieving parameters for FIND command specifically
     * 
     * @param commandString
     * @return String[] parameters for FIND command
     */
    private static String[] getParam_FIND(String commandString) {
        int tag_index = 0;
        int toSearch_index = 1;

        String[] paramArray = new String[FIND_PARAMETERS];
        String paramString = removeFirstWord(commandString);

        String tagType = getFirstWord(paramString);

        if (tagType.equalsIgnoreCase("-t")) {
            paramArray[tag_index] = "DATE";
            paramArray[toSearch_index] = removeFirstWord(paramString);
        } 
        else if (tagType.equalsIgnoreCase("-w")) {
            paramArray[tag_index] = "WORKLOAD";
            paramArray[toSearch_index] = removeFirstWord(paramString);
        } 
        else {
            paramArray[tag_index] = "KEYWORD";
            paramArray[toSearch_index] = paramString;
        }
        
        return paramArray;
    }
    
    private static String[] getParam_ARCH(String commandString){
        String paramString = removeFirstWord(commandString);
        String[] paramArray = new String[ARCHIVE_PARAMETERS];
        if(paramString.equals("")){
            paramArray[0] = null;
        }
        else {
            paramArray[0] = paramString;  
        }
        return paramArray;
    }
    
    private static String[] getParam_GOTO(String commandString){
        String[] paramArray = new String[GOTO_PARAMETERS];
        String command = getFirstWord(commandString).toLowerCase();
        String paramString = removeFirstWord(commandString);
        switch(command){
        case "next":
            ;
        case "back":
            ;
        case "goto":
            ;
        }
        return paramArray;
    }

    /********************************** Helper Functions *************************************/

    /**
     * Method for removing the first word(separated by a whitespace) from a string
     * 
     * @param line
     * @return String remaining string
     */
    private static String removeFirstWord(String line) {
        return line.replaceFirst(getFirstWord(line), "").trim();
    }

    /** 
     * Method for retrieving the first word(separated by a whitespace) from a string
     * 
     * @param commandString
     * @return String first word
     */
    private static String getFirstWord(String commandString) {
        String firstWord = commandString.trim().split("\\s+")[0];
        return firstWord;
    }
    
    /**
     * Method to check whether the date is in correct syntax, 
     * and translates it into the default date syntax 
     * 
     * @param paramDate
     * @return
     */
    private static String parseDate(String paramDate) throws Exception{
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + OFFSET_OF_MONTH;
        int currentYear = cal.get(Calendar.YEAR);
        SimpleDateFormat sdf;
        Date date = null;
        int numOfParams = 0;
        for(int i = 0; i < availableDateFormats.size(); i++){
            try{
                sdf = new SimpleDateFormat(availableDateFormats.get(i)[FORMAT_INDEX]);
                date = sdf.parse(paramDate);
                numOfParams = Integer.parseInt(availableDateFormats.get(i)[NUM_OF_PARAMS_INDEX]);
                break;
            }
            catch(Exception e){
                ;
            }
        }
        if(numOfParams == 0){
            throw new Exception("Invalid date syntax");
        }
        cal.setTime(date);
        if(numOfParams == 1){
            cal.set(Calendar.MONTH, currentMonth);
            cal.set(Calendar.YEAR, currentYear);
        }
        else if(numOfParams == 2){
            cal.set(Calendar.YEAR, currentYear);
        }
        String dateString = calendarToString.parseDate(cal);
        return dateString;
    }
    
}
