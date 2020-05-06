/**
 * 
 */
package ui;

import controller.Controller;
import share.Result;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;

/**
 * @author HE,HAORUI
 * @version 0.1
 * <p>CommandLine UI for FileManager
 */
public class CommandLine {
	private Controller ctrler;
	
	public static void main(String []args) {
		CommandLine cmdl= new CommandLine();
		cmdl.startEventLoop();
	}
	
	public CommandLine() {
		ctrler= new Controller();
	}
	
	public void startEventLoop() {
		boolean hold=true;
		InputStreamReader bis= new InputStreamReader(System.in);
		BufferedReader bfr= new BufferedReader(bis);
		String content;
		Result result;
		System.out.println("File manager started, welcome!\nAuthor: HE, HAORUI\nVersion: 0.1");
		while(hold) {
			System.out.print(ctrler.pwd()+">>");
			
			
			try {
				content = bfr.readLine();
				if(content.trim().toLowerCase().equals("exit")) {
					hold=false;
					break;
				}
				
				
				result = ctrler.execCmd(content);
				if(result.successful) {
					for(File f:result.Outputs) {
						System.out.println(f.getName());
					}
				}
				else {
					result.err.printStackTrace();
				}
				
				
				
			}
			catch(FileNotFoundException err){
				err.printStackTrace();
			}
			catch(IOException err) {
				err.printStackTrace();
			}
			
			
		}
		
		if(bfr!=null) {
			try {
				bfr.close();
			}
			catch(IOException err) {
				err.printStackTrace();
			}
		}
		
		System.out.println("file manager exited");
	}
}
