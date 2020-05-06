/**
 * 
 */
package share;
import java.io.File;

/**
 * @author HE,HAORUI
 * @version 0.1
 * <p>{@code Result} describes the operation result fed to view
 *
 */
public class Result {
	public boolean successful;// 0 for failed executions and 1 for successful ones
	public File[] Outputs;//exception message will be put at Outputs[0] if exception occurs
	public Exception err;
	
	
	
	public Result(boolean successful, File[] Outputs, Exception err) {
		this.successful=successful;
		this.Outputs=Outputs;
		this.err=err;
	}
	/**
	 * cast the execute output to {@code String}
	 */
	@Override
	public String toString() {
		if(successful) {
			String result=new String();
			for(File f:Outputs) {
				result += f.getName();
				result += "\n";
			}
			return result;
		}
		
		else {
			return err.toString();
		}
	}
	
	
}
