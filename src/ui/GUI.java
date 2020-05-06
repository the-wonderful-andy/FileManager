/**
 * 
 */
package ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 * @author HE,HAORUI
 * @version 0.1
 * <p>{@code GUI} is the graphic user interface for the file manager
 */
public class GUI{
	public static void main(String args[]) {
		new GUI();
	}
	
	private JFrame frame;
	
	public GUI() {
		frame= new JFrame();
		frame.setSize(800, 600);
		BorderLayout blo= new BorderLayout();
		frame.setLayout(blo);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JButton(), BorderLayout.NORTH);
		frame.add(new JButton(), BorderLayout.CENTER);
		frame.add(new JButton(), BorderLayout.EAST);
		frame.add(new JButton(), BorderLayout.SOUTH);
		frame.setVisible(true);
	}
}
