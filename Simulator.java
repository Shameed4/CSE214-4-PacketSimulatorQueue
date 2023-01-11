
/**
 * The Simulator class simulate packets being sent between a dispatcher, router,
 * and destination
 * 
 * @author Sean Erfan
 */
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Simulator {
	public static Scanner s;
	public static final int MAX_PACKETS = 3;

	private Router dispatcher;
	private LinkedList<Router> routers;
	private int totalServiceTime;
	private int totalPacketsArrived;
	private int packetsDropped;
	private double arrivalProb;
	private int numIntRouters;
	private int maxBufferSize;
	private int minPacketSize;
	private int maxPacketSize;
	private int bandwidth;
	private int duration;

	/**
	 * Instantiates a Simulator object, initializes its variables, and runs the
	 * simulation
	 * 
	 * @param numIntRouters number of routers
	 * @param arrivalProb   the chance that a packet will be added
	 * @param maxBufferSize the number of items that an intermediate router can
	 *                      store
	 * @param minPacketSize the minimum possible size of a packet
	 * @param maxPacketSize the maximum possible size of a packet
	 * @param bandwidth     the number of items that can be sent to the
	 *                      destination.
	 * @param duration      the number of time units that the simultaion uses
	 */
	public Simulator(int numIntRouters, double arrivalProb, int maxBufferSize,
	        int minPacketSize, int maxPacketSize, int bandwidth, int duration) {
		this.numIntRouters = numIntRouters;
		this.arrivalProb = arrivalProb;
		this.maxBufferSize = maxBufferSize;
		this.minPacketSize = minPacketSize;
		this.maxPacketSize = maxPacketSize;
		this.bandwidth = bandwidth;
		this.duration = duration;
		routers = new LinkedList<Router>();
		for (int i = 1; i <= this.numIntRouters; i++)
			routers.add(new Router(i));
		dispatcher = new Router(-1);
		totalServiceTime = 0;
		totalPacketsArrived = 0;
		Router.maxSize = this.maxBufferSize;
	}

	/**
	 * Runs the simulation
	 * 
	 * @return the average time spent per packet
	 */
	public double simulate() {
		int time = 0;
		Packet.setPacketCount(0);

		while (time++ < duration) {
			System.out.println("Time: " + time);
			int rand = 0;
			for (int i = 0; i < MAX_PACKETS; i++) {
				if (Math.random() < arrivalProb)
					rand++;
			}
			if (rand == 0)
				System.out.println("No packets arrived.");
			else {
				Packet[] packets = new Packet[rand];
				for (int i = 0; i < packets.length; i++) {
					dispatcher.enqueue(new Packet(
					        randInt(minPacketSize, maxPacketSize), time));
				}
				for (Packet p : dispatcher)
					System.out.println("Packet " + p.getId()
					        + " arrives at dispatcher with size "
					        + p.getPacketSize() + ".");
				while (dispatcher.size() > 0) {
					Packet p = null;
					try {
						p = dispatcher.dequeue();
					}
					catch (EmptyQueueException e) {
						// This should never dequeue because then size() == 0
						// and loop wouldn't iterate
						System.out.println("This should never print");
					}
					try {
						int routerId = Router.sendPacketTo(routers);
						Router r = routers.get(routerId);
						if (r.size() == 0)
							p.setTimeToDest(p.getTimeToDest() + 1);
						r.enqueue(p);
						System.out.println("Packet " + p.getId()
						        + " sent to router " + (routerId + 1) + ".");
					}
					catch (NoAvailableRouterException e) {
						System.out.println("Network is congested. Packet "
						        + p.getId() + " is dropped.");
						packetsDropped++;
					}
				}
			}
			Iterator<Router> it = routers.iterator();
			while (it.hasNext()) {
				Packet p = it.next().peek();
				if (p != null)
					p.setTimeToDest(p.getTimeToDest() - 1);
			}
			for (int i = 0; i < bandwidth; i++) {
				try {
					int index = Router.sendPacketFrom(routers);
					if (index != -1) {
						try {
							Packet p = routers.get(index).dequeue();
							System.out.println("Packet " + p.getId()
							        + " has successfully reached its "
							        + "destination: +"
							        + (time - p.getTimeArrive()));
							totalPacketsArrived++;
							totalServiceTime += time - p.getTimeArrive();
						}
						catch (EmptyQueueException e) {
							// shouldn't happen
						}
					}
				}
				catch (NoAvailableRouterException e) {
					break;
				}
			}

			for (int i = 1; i <= routers.size(); i++) {
				System.out.println("R" + i + ": " + routers.get(i - 1));
			}
			System.out.println();

		}
		System.out.println();
		System.out.println("Simulation ending...");
		System.out.println("Total service time: " + totalServiceTime);
		System.out.println("Total packets served: " + totalPacketsArrived);

		double averageTime;
		if (totalPacketsArrived != 0)
			averageTime = (float) totalServiceTime / totalPacketsArrived;
		else
			averageTime = 0;
		System.out.println("Average service time per packet: "
		        + String.format("%.2f", averageTime));
		System.out.println("Total packets dropped: " + packetsDropped);
		return averageTime;
	}

	/**
	 * Calls all methods necessary for running the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		s = new Scanner(System.in);
		do {
			System.out.println("Starting simulator...");
			int numIntRouters;
			while (true) {
				numIntRouters = promptNextInt(
				        "Enter the number of Intermediate routers: ");
				if (numIntRouters > 0)
					break;
				System.out.println("Number must be greater than 0");
			}
			double arrivalProb;
			while (true) {
				arrivalProb = promptNextDouble(
				        "Enter the arrival probability of a packet: ");
				if (arrivalProb >= 0 && arrivalProb <= 1)
					break;
				System.out.println("Number must be between 0 and 1");
			}
			int maxBufferSize;
			while (true) {
				maxBufferSize = promptNextInt(
				        "Enter the maximum buffer size of a router: ");
				if (maxBufferSize > 0)
					break;
				System.out.println("Number must be greater than 0");
			}
			int minPacketSize;
			int maxPacketSize;
			while (true) {
				minPacketSize = promptNextInt(
				        "Enter the minimum size of a packet: ");
				if (minPacketSize <= 0) {
					System.out.println("Size must be greater than 0!");
					continue;
				}
				maxPacketSize = promptNextInt(
				        "Enter the maximum size of a packet: ");
				if (maxPacketSize <= 0) {
					System.out.println("Size must be greater than 0!");
					continue;
				}
				if (minPacketSize <= maxPacketSize)
					break;
				System.out.println(
				        "Maximum size cannot be greater than minimum!");
			}
			int bandwidth;
			while (true) {
				bandwidth = promptNextInt("Enter the bandwidth size: ");
				if (bandwidth > 0)
					break;
				System.out.println("Size must be greater than 0!");
			}
			int duration;
			while (true) {
				duration = promptNextInt("Enter the simulation duration: ");
				if (duration > 0)
					break;
				System.out.println("Duration must be greater than 0!");
			}
			Simulator s = new Simulator(numIntRouters, arrivalProb,
			        maxBufferSize, minPacketSize, maxPacketSize, bandwidth,
			        duration);
			s.simulate();
		}
		while (askIfContinue());
	}

	/**
	 * Prints out a message and returns a double input. If the input is not a
	 * double, it will try again.
	 * 
	 * @param msg The message to print
	 * @return The user input
	 */
	public static int promptNextInt(String msg) {
		int i;
		while (true) {
			try {
				System.out.print(msg);
				i = Integer.parseInt(s.nextLine());
			}
			catch (NumberFormatException e) {
				System.out.println("Must enter an integer.");
				continue;
			}
			break;
		}
		System.out.println();
		return i;
	}

	/**
	 * Prints out a message and returns a double input. If the input is not a
	 * double, it will try again.
	 * 
	 * @param msg The message to print
	 * @return The user input
	 */
	public static double promptNextDouble(String msg) {
		double d;
		while (true) {
			try {
				System.out.print(msg);
				d = Double.parseDouble(s.nextLine());
			}
			catch (NumberFormatException e) {
				System.out.println("Must enter a number.");
				continue;
			}
			break;
		}
		System.out.println();
		return d;
	}

	/**
	 * @param minVal The minimum value that could be returned
	 * @param maxVal The maximum value that could be returned
	 * @return A random number between minVal and maxVal
	 */
	public static int randInt(int minVal, int maxVal) {
		return (int) (Math.random() * (maxVal - minVal + 1)) + minVal;
	}

	/**
	 * Asks user if they want to continue simulating
	 * 
	 * @return true if user inputs y, false if user inputs n
	 */
	public static boolean askIfContinue() {
		String input;
		while (true) {
			System.out.print("Do you want to try another simulation? ");
			String answer = s.nextLine().toLowerCase().trim();
			if (answer.equals("y")) {
				System.out.println();
				return true;
			}
			if (answer.equals("n"))
				return false;
			System.out.println("Invalid input. Please enter 'y' or 'n'.");
		}
	}
}
