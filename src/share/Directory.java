/**
 * 
 */
package share;
import java.io.File;

/**
 * @author HE,HAORUI
 * @version 0.1
 * <p>{@code Directory} describes an path passed to file manipulator as a parameter 
 *
 */
public class Directory {
	public String[] dirs;// subdirectories, each level stored in one string, organized in top-down order  
	
	/**
	 * constructor root and dirs as parameters
	 * @param root attribute root
	 * @param dirs attribute dirs
	 */
	public Directory(String[] dirs) {
		this.dirs= dirs.clone();
		
	}
	
	/**
	 * contructor accepting a String
	 * @param dir string which describes the Directory
	 */
	public Directory(String dir) {
		if(dir.isBlank())
		dirs = new String[0];
		else
		dirs = dir.split("\\"+File.separator);
	}
	
	/**
	 * copy constructor
	 * @param src source object to copy
	 */
	public Directory(Directory src) {
		dirs=src.dirs.clone();
	}
	
	
	/**
	 * default constructor setting root and dirs to null
	 */
	public Directory() {
		dirs = null;
	}
	
	
	/**
	 *convert to {@code String}
	 */
	@Override
	public String toString() {
		String result="";
		for(String s:dirs) {
			result+=File.separator+s;
		}
		
		return result;
	}
	
	
	/**
	 * convert to {@code File}
	 * @return the corresponding {@code File}
	 */
	public File toFile() {
			return new File(this.toString());		
	}
	
	
	/**
	 * cut off the last n entries, this function will change the original {@code directory}
	 * @param number of entries that needs to be cut of
	 * @return the modified {@code Directory}
	 */
	public Directory cut(int n)throws IllegalArgumentException {
		if(n>dirs.length)
			throw new IllegalArgumentException("n should not be larger than the length of dirs");
		else {
			String temp[] = new String[this.dirs.length-n];
			for(int i=0;i<this.dirs.length-n; i++) {
				temp[i]=this.dirs[i];
			}
			this.dirs=temp;
			
			return new Directory(temp);
		}
	}
	
	
	/**
	 * return a new {@code Directory} acquired by cutting off the last n entries of the original {@code Directory}
	 * this function will not change the original {@code Directory} 
	 * @param n
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Directory cutted(int n)throws IllegalArgumentException {
		if(n>dirs.length)
			throw new IllegalArgumentException("n should not be larger than the length of dirs");
		else {
			String temp[] = new String[this.dirs.length-n];
			for(int i=0;i<this.dirs.length-n; i++) {
				temp[i]=this.dirs[i];
			}
			
			return new Directory(temp);
		}
	}
	
	
	public Directory getParent()throws RuntimeException {
		try {
			return this.cutted(1);
		}
		catch(IllegalArgumentException err) {
			throw new RuntimeException("this directory is root");
		}
	}
	
	
	/**
	 * append a entry to the end of the current directory, this function will change the orginal {@code Directory}
	 * @param newEntry the name of the new Entry
	 * @return the modified {@code Directory}
	 */
	public void append(String newEntry) {
		String temp[] = new String[dirs.length+1];
		for(int i=0; i<dirs.length; i++)
			temp[i]=dirs[i];
		temp[temp.length-1] = newEntry;
		dirs=temp;
		
	}
	
	
	/**
	 * return a new {@code Directory} acquired by appending a new entry to the end of the original directory
	 * this function will change the original {@code Directory}
	 * @param newEntry new entry to add to the end of the original {@code Directory}
	 * @return the new {@code Directory} after appending
	 */
	public Directory appended(String newEntry) {
		String temp[] = new String[dirs.length+1];
		for(int i=0; i<dirs.length; i++)
			temp[i]=dirs[i];
		temp[temp.length-1] = newEntry;
		return new Directory(temp);
	}
	
	
	/**
	 * join tow {@code Directory} together with the parameter serving as subpath
	 * this function will change the original {@code Directory}
	 * @param subpath the subpath to be joined
	 * @return the new {@code Directory} after joining
	 */
	public void join(Directory subpath) {
		String temp[]= new String[dirs.length+subpath.dirs.length];
		int i;
		for(i=0;i<dirs.length;i++) {
			temp[i]=dirs[i];
			
		}
		for(;i<temp.length;i++) {
			temp[i]=subpath.dirs[i];
		}
		dirs=temp;
	}
	
	
	/**
	 * return a new {@code Directory} acquired by joining two {@code Directory} together, and the parameter will serve as the sub path
	 * this function will not change the original {@code Directory} 
	 * @param subpath
	 * @return
	 */
	public Directory joined(Directory subpath) {
		String temp[]= new String[dirs.length+subpath.dirs.length];
		int i;
		for(i=0;i<dirs.length;i++) {
			temp[i]=dirs[i];
			
		}
		for(int j=i;j<temp.length;j++) {
			temp[j]=subpath.dirs[j-i];
		}
		return new Directory(temp);
	}
	
	
	/**
	 * get the name of the Directory
	 * @return the last name in the Directory's name sequence
	 */
	public String getName() {
		return dirs[dirs.length-1];
	}
	
	
	/**
	 * cut off the first n entries in the dirs sequence
	 * the original directory will be changed
	 * @param n the number to be cut
	 */
	public void cutFront(int n) {
		String temp[]=new String[dirs.length-n];
		for(int i=n; i<dirs.length; i++) {
			temp[i-n]=dirs[i];
		}
		dirs=temp;
	}
	
	
	/**
	 * return a new Directory acquired by cutting the first n entries of the original Directory
	 * the original directory will not be changed
	 * @param n
	 * @return
	 */
	public Directory cuttedFront(int n) {
		String temp[]=new String[dirs.length-n];
		for(int i=n; i<dirs.length; i++) {
			temp[i-n]=dirs[i];
		}
		return new Directory(temp);
	}
	
	
	
	/**
	 * change the extend name of the source name
	 * if the source name dosen't have extend name, simply add the extend name to the of the source name
	 * @param sourcename source name
	 * @param extendname designated extend name
	 * @return full name after modifying
	 */
	public static String castExtendname(String sourcename, String extendname) {
		int sepIndex=sourcename.lastIndexOf('.');
		if(sepIndex==-1) {
			return sourcename+'.'+extendname;
		}
		else {
			return sourcename.substring(0, sepIndex+1)+extendname;
		}
	}
	

	
	
}
