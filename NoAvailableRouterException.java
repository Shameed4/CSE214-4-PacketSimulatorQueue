/**
 * NoAvailableRouterException indicates that there are no routers that can do
 * the requested task
 * 
 * @author Sean Erfan
 */
public class NoAvailableRouterException extends Exception {
	/**
	 * @param msg The message that will be returned by this exception
	 */
	public NoAvailableRouterException(String msg) {
		super(msg);
	}
}
