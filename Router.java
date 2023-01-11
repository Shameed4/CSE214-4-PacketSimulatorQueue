import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Router is a Queue of Packets
 * @author Sean Erfan
 *
 */
public class Router extends LinkedList<Packet> {
	public static int maxSize;
	private int id;
	private int waitTime = 0;

	/**
	 * Instantiates a Router without initializing any variables
	 */
	public Router(int myId) {
		id = myId;
	}

	/**
	 * Adds a packet to the end of the queue
	 * 
	 * @param p The packet to be added
	 */
	public void enqueue(Packet p) {
		add(p);
	}

	/**
	 * Removes a packet from the queue
	 * 
	 * @return The packet that has been removed
	 * @throws EmptyQueueException Indicates that there is no packets in the
	 *                             queue
	 */
	public Packet dequeue() throws EmptyQueueException {
		if (size() == 0)
			throw new EmptyQueueException(
			        "Cannot dequeue because queue is empty");
		return remove();
	}

	/**
	 * @return A string containing information of all Packets in the queue
	 */
	public String toString() {
		String s = "{";
		Iterator<Packet> it = this.iterator();
		if (it.hasNext())
			s += it.next();
		while (it.hasNext())
			s += ", " + it.next();
		return s + "}";
	}

	/**
	 * Sends a packet to the emptiest intermediate router
	 * 
	 * @param routers The collection of intermediate routers
	 * @return The index of the router that was filled
	 * @throws NoAvailableRouterException Indicates that all routers are full.
	 */
	public static int sendPacketTo(Collection<Router> routers)
	        throws NoAvailableRouterException {
		int index = -1;
		int emptiestSize = maxSize;
		Iterator<Router> it = routers.iterator();
		int counter = 0;
		while (it.hasNext()) {
			Router r = it.next();
			if (r.size() < emptiestSize) {
				index = counter;
				emptiestSize = r.size();
			}
			counter++;
		}
		if (index != -1)
			return index;
		throw new NoAvailableRouterException("All routers are full!");
	}

	/**
	 * Finds the router with the least wait time that can send a router
	 * 
	 * @param routers The list of routers
	 * @return The index of the best router to send from
	 * @throws NoAvailableRouterException Indicates that no routers can send a
	 *                                    packet
	 */
	public static int sendPacketFrom(LinkedList<Router> routers)
	        throws NoAvailableRouterException {
		Iterator<Router> it = routers.iterator();
		int greatestWaitIndex = -1;
		int greatestWaitTime = -1;
		int index = -1;
		while (it.hasNext()) {
			index++;
			Router r = it.next();
			if (r.peek() == null)
				break;
			if (r.peek().getTimeToDest() > 0) {
				r.waitTime = 0;
			}
			else {
				if (greatestWaitIndex == -1 || r.waitTime > greatestWaitTime) {
					greatestWaitTime = r.waitTime;
					greatestWaitIndex = index;
				}
				r.waitTime++;
			}
		}
		if (greatestWaitIndex == -1)
			throw new NoAvailableRouterException("No routers can send");
		routers.get(greatestWaitIndex).waitTime = 0;
		return greatestWaitIndex;
	}
}