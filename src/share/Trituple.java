/**
 * 
 */
package share;

/**
 * @author HE,HAORUI
 * @version 0.1
 * <p>A simple trituple with generic type
 *
 */
public class Trituple<A, B, C> {
	public A first;
	public B second;
	public C third;
	
	public Trituple() {
		first=null;
		second=null;
		third=null;
	}
	public Trituple(A first, B second, C third) {
		this.first=first;
		this.second=second;
		this.third=third;
	}

}
