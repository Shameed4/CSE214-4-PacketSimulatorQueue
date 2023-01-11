/**
 * The EmptyQueueException is thrown when an operation needs at least one item
 * in a Queue but the Queue is empty
 * 
 * @author Sean Erfan
 *
 */
public class EmptyQueueException extends Exception {
	/**
	 * @param msg The message that will be returned with this exception
	 */
	public EmptyQueueException(String msg) {
		super(msg);
	}
}
