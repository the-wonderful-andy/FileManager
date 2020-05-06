/**
 * 
 */
package fileManipulator;
import java.io.File;
import java.io.IOException;
import share.Directory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import share.CompressAlgorithm;
import share.UnzipFile;
import java.util.Hashtable;
import java.util.function.BiFunction;

/**
 * @author HE,HAORUI
 * @version 0.1
 * <p>{@code FileManipulator} implements the required functions to process files and
 * the functions are implemented as static methods, all of which in the  <b>BiFunc(String, Directory[], File[])</b> form
 * 
 * <p>It can also return a function table containing all the provided file process functions 
 * in the {String, BiFunction<Srtirng, String[], String> form
 *
 */

public class FileManipulator {
	
	
	/**
	 * get subitems(file, diretory) of the designated directories
	 * @param opt not used
	 * @param dirs designated directories
	 * @return subitems
	 */
	private Hashtable<String, BiFunction<String, Directory[], File[]>> functable;//file process functions will be put into this table
	
	
	/**
	 * default constructor initializing the function table
	 */
	public FileManipulator() {
		functable = new Hashtable<String, BiFunction<String, Directory[], File[]>>();
		functable.put("getSubitems", FileManipulator::getSubitems);
		functable.put("createItem", FileManipulator::createItem);
		functable.put("removeItem", FileManipulator::removeItem);
		functable.put("copyItem", FileManipulator::copyItem);
		functable.put("moveItem", FileManipulator::moveItem);
		functable.put("encr"
				+ "yptFile", FileManipulator::encryptFile);
		functable.put("zipItem", FileManipulator::zipItem);
		functable.put("unzipItem", FileManipulator::unzipItem);
	}
	
	
	
	/**
	 * get the function table of the FileManipulator
	 * @return
	 */
	public Hashtable<String, BiFunction<String, Directory[], File[]>> getFunctalbe(){
		return functable;
	}
	
	
	public static File[] getSubitems(String opt, Directory[] dirs)throws IllegalArgumentException {
		if(opt.length()!=0) {
			throw new IllegalArgumentException("invalid option");
		}
		
		File result[]=new File[0];
		File temp;
		
		for(Directory d:dirs) {
			temp= d.toFile();
			if(!temp.exists()) {
				throw new IllegalArgumentException(d.toString()+" doesn't exist");
			}
			else if(temp.isFile()) {
				throw new IllegalArgumentException(d.toString()+" is a file, instead of a directory");
			}
			
			result = joinFileArray(result, temp.listFiles());
		}
		return result;
	}
	
	
	/**
	 * create designated directory or file
	 *  if 'd' is in opt, process as directory
	 *  else process as file
	 *  char other than 'd' in opt will be ignored
	 * @param opt option string
	 * @param dirs designated directory
	 * @return not used
	 * @throws IOException
	 */
	public static File[] createItem(String opt, Directory[] dirs){
		try {
			if(opt.indexOf('d')>=0) {//process as directory
				for(Directory d:dirs) {
					if(!d.toFile().mkdirs()) {
						throw new RuntimeException("failed to make the directory: "+d.toString());
					}
				}
			}
			else {//process as file
				for(Directory d:dirs) {
					d.toFile().createNewFile();
				}
			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
		
		return new File[0];
	}
	
	
	/**
	 * remove the designated file or directory
	 * Throws RuntimesException if designated item is a directory unless 'r' is in opt
	 * char other than 'r' will be ignored
	 * @param opt option string
	 * @param dirs designated directory
	 * @return not used
	 * @throws RuntimeException
	 */
	public static File[] removeItem(String opt, Directory[] dirs) {
		boolean r;
		r=(opt.indexOf('r')>=0);
		File f;
		try {
			for(Directory d:dirs) {
				f=d.toFile();
				if(f.isFile()||r) {
					deleteItem(f);
				}
				else {
					throw new RuntimeException(d.toString()+" is a dirctroy, specify -r to remove it");
				}
			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
		
		return new File[0];
	}
	
	
	
	/**
	 * copy a Item to a designated directory
	 * @param opt 'r'(recursive) should be set if allow copy folder recursively
	 * @param dirs source directory at dirs[0] and destination directory at dirs[1]
	 * @return not used
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public static File[] copyItem(String opt, Directory[] dirs)
			throws IllegalArgumentException, RuntimeException
	{
		File f;
		boolean r= (opt.indexOf('r')>=0);// recursive
		File source;
		Directory desti;
		Directory targetItem;
		
		if(dirs.length!=2) {//argument number mismatch
			throw new IllegalArgumentException("require two directories, but "+dirs.length+" is given");
		}
		
		source= dirs[0].toFile();
		desti= dirs[1];
		targetItem=desti.appended(source.getName());
		
		for(Directory d: dirs) {
			if(!d.toFile().exists()) {//both directory should exist
				throw new IllegalArgumentException(d.toString()+" dosen't exist");
			}
		}
		
		if(targetItem.toFile().exists()) {
			throw  new IllegalArgumentException(targetItem.toString()+" already exists");
		}
		
		
		if(desti.toFile().isFile()) {//destination can't be a file
			throw new IllegalArgumentException(desti.toString()+" is a file, not a directory");
		}
		
		try {
			if(source.isDirectory()) {
				if(!r) {// if recursive is not specified
					throw new RuntimeException(source.toString()+" is a directory, specify -r to copy it");
				}
				else {
					copyFolder(desti, source);				
				}
			}
			
			else if(source.isFile()) {
				copyFile(desti, source);
			}
			
		}catch(IOException err) {
			err.printStackTrace();
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new RuntimeException("operation failed");
		}
		
		
		return new File[0];
		
		
	}
	
	
	/**
	 * move a item to the designated directory
	 * @param opt 'r' should be set if allow move folder recursively
	 * @param dirs source directory at dirs[0] and destination directory at dirs[1]
	 * @return not used
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public static File[] moveItem(String opt, Directory[] dirs) 
			throws IllegalArgumentException, RuntimeException
	{
		copyItem(opt, dirs);
		if(!dirs[0].toFile().delete()) {
			throw new RuntimeException("failed to remove original file");
		}
		return new File[0];
	}
	
	
	/**
	 * encrypt designated file
	 * @param skey the string as key
	 * @param dirs designated file
	 * @return not used
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws RuntimeException
	 */
	public static File[] encryptFile(String skey, Directory[] dirs) 
			throws IllegalArgumentException, RuntimeException
	{
		byte bkey = (byte) ((skey.hashCode())%256);//convert to the 8bit key
		byte buffer[] = new byte[8*1024];//buffer of the convert
		File f;
		int len;
		try {
			for(Directory d:dirs) {
				f=d.toFile();//file to encrypt
				
				if(!f.exists()) {//designated file should exist
					throw new IllegalArgumentException(d.toString()+" doesn't exist");
				}
				else if(f.isDirectory()) {//designated file can't be directory
					throw new IllegalArgumentException(d.toString()+" is a Directory");
				}
				
				String sfilename= f.getName();
				String stempfilename= sfilename.substring(0, sfilename.lastIndexOf('.')+1)+"temp";//get the name of the .temp file
				
				FileInputStream fis= new FileInputStream(f);
				FileOutputStream fos =new FileOutputStream(stempfilename);
				
				while((len=fis.read(buffer))!=-1){
					for(int i=0; i<len; i++) {
						buffer[i]=(byte)(buffer[i]^bkey);//encrypt 
					}
					
					fos.write(buffer, 0, len);
				}
				
				
				if(fis!=null) {//try to close the input\output stream
					try {
						fis.close();
					}
					catch(IOException err) {
						err.printStackTrace();
					}
				}
				
				if(fos != null) {
					try {
						fos.flush();
						fos.close();
					}
					catch(IOException err) {
						err.printStackTrace();
					}
				}
				
				
				
				fis= new FileInputStream(stempfilename);
				fos= new FileOutputStream(f);//change the i/o role
				
				while((len=fis.read(buffer))!=-1){					
					fos.write(buffer, 0, len);//write the encrypted content back to original file
				}
				
				
				
				
				if(fis!=null) {//try to close the input\output stream
					try {
						fis.close();
					}
					catch(IOException err) {
						err.printStackTrace();
					}
				}
				
				if(fos != null) {
					try {
						fos.flush();
						fos.close();
					}
					catch(IOException err) {
						err.printStackTrace();
					}
				}
				
				
				f= new File(stempfilename);
				if(!f.delete()) {//clean up the .temp file
					throw new RuntimeException("failed to remove temporary file");
				}
				
			}
		}catch(FileNotFoundException err) {
			err.printStackTrace();
			
		}
		catch(SecurityException err) {
			err.printStackTrace();
		}
		catch(IOException err) {
			err.printStackTrace();
		}
		
		return new File[0];
	}
	
	
	/**
	 * zip the designated item to the designated directory
	 * @param opt not used
	 * @param dirs can accept 1 or 2 arguments, 
	 * the designated directory is set to the parent directory of the source file if only 1 argument is given
	 * @return not used
	 * @throws IllegalArgumentException
	 * @throws RuntimeException
	 */
	public static File[] zipItem(String opt, Directory[] dirs)
			throws IllegalArgumentException, RuntimeException	
	{
		File sourceFile;
		File targetFile;
		Directory temp;
		if(dirs.length==1) {
			sourceFile=dirs[0].toFile();
		
			if(!sourceFile.exists()) {
				throw new IllegalArgumentException(sourceFile.getPath()+" dosen't exist");
			}
		
			temp= new Directory(dirs[0]);
			temp.cut(1);
			temp.append(Directory.castExtendname(dirs[0].getName(), "zip"));
			targetFile=temp.toFile();
		}
		
		else if(dirs.length==2) {
			sourceFile=dirs[0].toFile();
			
			if(!sourceFile.exists()) {
				throw new IllegalArgumentException(sourceFile.getPath()+" doesn't exist");
			}
			
			temp= dirs[1];
			
			if(!temp.toFile().exists()) {
				throw new IllegalArgumentException(temp.toString()+" doesn't exist");
			}
			else if(temp.toFile().isFile()) {
				throw new IllegalArgumentException(temp.toString()+" is a file instead of a directory");
			}
			temp.append(Directory.castExtendname(sourceFile.getName(), "zip"));
			targetFile= temp.toFile();
			
			
		}
		
		else {
			throw new IllegalArgumentException("requires 1 or argument, but "+dirs.length+" was given");
		}
		
		
		try {
			new CompressAlgorithm(targetFile).zipFiles(sourceFile);
		}
		catch(IllegalArgumentException err) {
			throw err;
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new RuntimeException("failed to compress");
		}
		
		return new File[0];

	}
	
	
	/**
	 * upzip the designated file to the designated directory
	 * @param opt not used
	 * @param dirs the designated source file and target directory
	 * the target directory is set to the parent directory if only 1 argument is given
	 * @return not used
	 * @throws IllegalArgumentException
	 * @throws RuntimeException
	 */
	public static File[] unzipItem(String opt, Directory[] dirs)
			throws IllegalArgumentException, RuntimeException
	{
		File sourcefile;
		Directory targetDir;
		sourcefile=dirs[0].toFile();
		
		if(!sourcefile.exists()) {
			throw new IllegalArgumentException(sourcefile.getPath()+" dosen't exist");
		}
		if(!sourcefile.isFile()) {
			throw new IllegalArgumentException(sourcefile.getPath()+ " is not a file");
		}
		
		switch(dirs.length) {
		case 1:
			targetDir=dirs[0].getParent();
			break;
		case 2:
			targetDir=dirs[1];
			if(!targetDir.toFile().exists()) {
				throw new IllegalArgumentException(targetDir.toString()+ " doesn't exist");
			}
			
			else if(targetDir.toFile().isFile()) {
				throw new IllegalArgumentException(targetDir.toString()+" is a file instead of a directory");
				
			}
			break;
		default:
			throw new IllegalArgumentException("require 1 or 2 arguments but "+dirs.length+" was given");
		}
		
		try {
			UnzipFile.unZipFiles(sourcefile, targetDir.toString());
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new RuntimeException("failed to upzip file");
		}
		
		return new File[0];
		
		
	}
	
	
	private static File[] joinFileArray(File[] a, File[] b) {
		File[] result=new File[a.length+b.length];
		int i;
		for(i=0;i<a.length;i++) {
			result[i]=a[i];
		}
		
		for(;i<result.length;i++) {
			result[i]=b[i];
		}
		
		return result;
	}
	
	
	/**
	 * copy the File specified by scr to the directory specified by dsti
	 * @param dsti destination directory
	 * @param src source file
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws SecurityException
	 */
	private static void copyFile(Directory dsti, File src) 
			throws IOException, FileNotFoundException, SecurityException 
	{
		//get destination file directory
		Directory d= new Directory(dsti);
		d.append(src.getName());
	
		FileInputStream fis= new FileInputStream(src.toString());
		FileOutputStream fos= new FileOutputStream(d.toString());
		
		try {
			
			
			byte datas[]= new byte[1024*8];//data buffer
			
			int len=-1;
			
			while((len=fis.read(datas))!=-1) {//copy
				fos.write(datas, 0, len);
			}
			fos.flush();
			
		}
		catch(IOException err) {
			throw err;
			
			
		}
		finally {//try to close the input\output stream
			if(fis!=null) {
				try {
					fis.close();
				}
				catch(IOException err) {
					err.printStackTrace();
				}
			}
			
			if(fos != null) {
				try {
					fos.close();
				}
				catch(IOException err) {
					err.printStackTrace();
				}
			}
		}
		
	}
	
	
	
	/**
	 * copy a Folder to the designated directory
	 * @param dsti the designated directory
	 * @param src the source file
	 * @throws IOException 
	 * @throws FileNotFoundException
	 * @throws SecurityException
	 */
	private static void copyFolder(Directory dsti, File src) 
			throws IOException, FileNotFoundException, SecurityException 	
	{
		Directory d= new Directory(dsti);
		d.append(src.getName());//get the directory of the copied folder
		try {
			File destf=d.toFile();
			if(!destf.mkdir()) {
				throw new IOException("failed to make the directory");
			}
			
			for(File f: src.listFiles()) {
				if(f.isFile()) {
					copyFile(d, f);//copy the file
					
				}
				else if(f.isDirectory()) {
					copyFolder(d, f);//recursively copy the folder
				}
			}
		}
		catch(Exception err) {
			d.toFile().delete();//if exception occurs, undo the copy
			throw err;
		}
		
	}
	
	private static void deleteItem(File target) {
		if(target.isFile()) {//target denotes a file
			target.delete();
			return;
		}
		else {//target denote a directory whether empty or not
			File temp[]=target.listFiles();
			for(File f:temp) {
				deleteItem(f);
			}
			target.delete();
			return;
		}
	}
	
	
}
