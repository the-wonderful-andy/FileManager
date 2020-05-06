/**
 * 
 */
package controller;

import share.Directory;
import share.Result;

import java.io.File;
import java.util.Hashtable;
import java.util.Queue;
import java.util.function.BiFunction;

import fileManipulator.FileManipulator;
import share.Trituple;
import java.util.Vector;
import java.util.LinkedList;

/**
 * @author HE,HAORUI
 * @version 0.1
 * <p>{@code Controller} process the input(in String forms) form the command line,
 * calling the corresponding function and return the {@code Result} object, which represents the execution result 
 *
 */
public class Controller {
	private Directory root;// root of the virtual file system
	private Directory wdir;// working directory
	private FileManipulator mani;
	private Hashtable<String, BiFunction<String, Directory[], File[]>> functable;
	private Hashtable<String, String> cmdtable;
	
	
	/**
	 * constructor function, setting root to what sroot specifies
	 * setting swdir to what swdir specifies
	 * @param sroot root directory described as a String
	 * @param swdir working directory described as a String
	 */
	public Controller(String sroot, String swdir) {
		root= new Directory(sroot);
		wdir= new Directory(swdir);
		mani= new FileManipulator();
		functable = mani.getFunctalbe();
		functable.put("chDirectory", this::chDirectory);
		
		cmdtable= new Hashtable<String, String>();
		cmdtable.put("cd", "chDirectory");
		cmdtable.put("ls", "getSubitems");
		cmdtable.put("mk", "createItem");
		cmdtable.put("rm", "removeItem");
		cmdtable.put("cp", "copyItem");
		cmdtable.put("mv", "moveItem");
		cmdtable.put("ecpt", "encryptFile");
		cmdtable.put("zip", "zipItem");
		cmdtable.put("upzip", "unzipItem");
	}
	
	/**
	 * default constructor
	 */
	public Controller() {
		root =new Directory("D:\\Eclipse_Workspace\\FileManager");
		wdir= new Directory("");
		mani= new FileManipulator();
		functable = mani.getFunctalbe();
		functable.put("chDirectory", this::chDirectory);
		
		cmdtable= new Hashtable<String, String>();
		cmdtable.put("cd", "chDirectory");
		cmdtable.put("ls", "getSubitems");
		cmdtable.put("mk", "createItem");
		cmdtable.put("rm", "removeItem");
		cmdtable.put("cp", "copyItem");
		cmdtable.put("mv", "moveItem");
		cmdtable.put("ecpt", "encryptFile");
		cmdtable.put("zip", "zipItem");
		cmdtable.put("unzip", "unzipItem");
	}
	
	
	
	/**
	 * print current working directory
	 * @return current working directory
	 */
	public String pwd() {
		return wdir.toString();
	}
	
	
	/**
	 * process the command, turning it from String to a {@code Trituple}
	 * the {@code first} field of the Trituple contains the command specifier
	 * the {@code second} field of the Trituple contains the command option
	 * the {@code third} field of the Trituple contains the directories
	 * @param cmd
	 * @return
	 */
	private Trituple<String, String, Directory[]> processCommand(String cmd)
			throws IllegalArgumentException {
		String[] temp=cmd.split(" ");
		Queue<String> commands = new LinkedList<String>();
		Trituple<String, String, Directory[]> result= new Trituple<String, String, Directory[]>();
		Directory[] dirs;
		
		for(String s:temp) {//only select the nonblank entries
			if(!s.isBlank()&&s!=null) {
				commands.offer(s);
			}
		}
		
		if(commands.peek()!=null) {//get the operation code
			result.first= commands.poll();
		}
		else {
			throw new IllegalArgumentException("the command can't be empty");
		}
		
		
		if(commands.peek()!=null&& commands.peek().charAt(0)=='-') {//get the option
			result.second=commands.poll().substring(1);
		}
		else {
			result.second="";
		}
		
		dirs= new Directory[commands.size()];
		int i=0;//count of dir
		while(!commands.isEmpty()) {//process the dirs
			dirs[i]= this.processDir(commands.poll());
			i++;
		}
		if(dirs.length==0) {//if no directory is offered, use the current working directory as the argument
			dirs=new Directory[] {this.processDir(wdir.toString())};
		}
		
		result.third=dirs;
		return result;
		
	}
	
	
	/**
	 * process the directories in the argument, this method will return absolute directories of the host system
	 * @param dir input directory as String
	 * @return absolute directory of the host system
	 * @throws RuntimeException
	 */
	private Directory processDir(String dir)throws RuntimeException {
		if(dir.isBlank()) {
			return root.joined(new Directory(dir));
		}
		else if(dir.matches("\\"+File.separator+".*")) {
			return root.joined(new Directory(dir));
		}
		else {
			Directory temp =new Directory(dir);
			
			if(temp.dirs[0].matches("\\.")) {
				temp.cutFront(1);
				return root.joined(wdir.joined(temp));
			}
			else if(temp.dirs[0].matches("\\.\\.")) {
				temp.cutFront(1);
				try {
					return root.joined(wdir.getParent().joined(temp));
				}
				catch(RuntimeException err) {
					throw err;
				}
			}
			else {
				return root.joined(wdir.joined(temp));
			}
		}
	}
	
	
	
	/**
	 * call the corresponding function specified by the command
	 * @param cmd command as a String
	 * @return execute {@code Result}
	 */
	public Result execCmd(String cmd) {
		
		File[] temp;
		Result result;
		String stdcmd;
		
		try {
			Trituple<String, String, Directory[]> command= processCommand(cmd);
			stdcmd=cmdtable.get(command.first);
			temp = functable.get(stdcmd).apply(command.second, command.third);
			result= new Result(true, temp, null);
			
		}catch(Exception err){
			result= new Result(false, null, err);
		}
		
		return result;
	}
	
	
	
	/**
	 * change the working directory
	 * @param opt not used
	 * @param dirs target directory
	 * @return not used
	 * @throws IllegalArgumentException
	 */
	public  File[] chDirectory(String opt, Directory[]dirs)
			throws IllegalArgumentException
	{
		if(dirs.length!=1) {
			throw new IllegalArgumentException("require 1 argument, but "+dirs.length+" was given");
		}
		
		if(!dirs[0].toFile().exists()) {
			throw new IllegalArgumentException(dirs[0].toString()+" doesn't exist");
		}
		
		if(dirs[0].toFile().isFile()) {
			throw new IllegalArgumentException(dirs[0].toString()+" is a file, instead of a directory");
		}
		
		
		wdir=dirs[0].cuttedFront(root.dirs.length);
		return new File[0];
	}
	
}
