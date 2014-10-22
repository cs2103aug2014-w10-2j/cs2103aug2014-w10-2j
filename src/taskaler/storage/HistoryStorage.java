package taskaler.storage;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.logging.Level;

import taskaler.common.util.CommonLogger;

/*
 * @author Quek Jie Ping A0111798X
 */
public class HistoryStorage {
	
	private static HistoryStorage instance=null;
	private static CommonLogger log= CommonLogger.getInstance();
	
	private HistoryStorage(){
		
	}
	
	public static HistoryStorage getInstance(){
		if(instance==null){
			instance=new HistoryStorage();
		}
		return instance;
	}
	
	/*
	 * Method to write history to the text file
	 * @param fileName
	 * 			The directory of the text file
	 * @param message
	 * 			The message to be stored in the history file
	 * 
	 * @return return a boolean value indicating whether the write operation 
	 * is success or fail
	 */
	public boolean writeToHistory(String fileName, String message){
		if(message==null || message.isEmpty()){
			return false;
		}
		try{
			FileWriter fw=new FileWriter(fileName,true);
			fw.append(message+"\n");
			fw.close();
			return true;
		}catch(Exception e){
			log.exceptionLogger(e, Level.SEVERE);
			return false;
		}
	}
	
	/*
	 * Method to read in history record from the text file
	 * @param fileName
	 * 			The directory of the text file
	 * 
	 * @return return a String of all the history records
	 */
	public String readFromHistory(String fileName){
		try{
			File f= new File(fileName);
			Scanner reader=new Scanner(f);
			String result="";
			while(reader.hasNextLine()){
				result+=reader.nextLine()+"\n";
			}
			reader.close();
			return result.trim();
		}catch(Exception e){
			log.exceptionLogger(e, Level.SEVERE);
			return null;
		}
	}
	
}
