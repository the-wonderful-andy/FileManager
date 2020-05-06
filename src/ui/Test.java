package ui;
import fileManipulator.FileManipulator;
import java.util.function.BiFunction;
import share.Directory;
import java.io.File;



public class Test {
	public static void main(String args[]) {
		Directory[] dirs= new Directory[1];
		dirs[0]= new Directory("D:\\Eclipse_Workspace\\FileManager\\src");
		File[] files= FileManipulator.getSubitems("", dirs);
		System.out.println(files.length);
		for(File f:files) {
			System.out.println(f.getName());
		}
	}
}
