/**
 * The Packet class stores information about a packet
 * 
 * @author Sean Erfan
 */
public class Packet {
	private static int packetCount = 0;
	private int id;
	private int packetSize;
	private int timeArrive;
	private int timeToDest;

	/**
	 * Instantiates a packet, initializes its variables, and increases
	 * packetCount
	 * 
	 * @param packetSize The size of the packet
	 * @param timeArrive The time that the packet has arrived
	 */
	public Packet(int packetSize, int timeArrive) {
		id = ++packetCount;
		this.packetSize = packetSize;
		this.timeArrive = timeArrive;
		timeToDest = packetSize / 100;
	}

	/**
	 * @return the total number of packets
	 */
	public static int getPacketCount() {
		return packetCount;
	}

	/**
	 * @param packetCount the packetCount to set
	 * @throws IllegalArgumentException
	 */
	public static void setPacketCount(int packetCount) {
		if (packetCount < 0)
			throw new IllegalArgumentException(
			        "Packet count cannot be negative.");
		Packet.packetCount = packetCount;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		if (packetCount <= 0)
			throw new IllegalArgumentException("ID must be positive.");
		this.id = id;
	}

	/**
	 * @return the packetSize
	 */
	public int getPacketSize() {
		return packetSize;
	}

	/**
	 * @param packetSize the packetSize to set
	 */
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

	/**
	 * @return the timeArrive
	 */
	public int getTimeArrive() {
		return timeArrive;
	}

	/**
	 * @param timeArrive the timeArrive to set
	 */
	public void setTimeArrive(int timeArrive) {
		this.timeArrive = timeArrive;
	}

	/**
	 * @return the timeToDest
	 */
	public int getTimeToDest() {
		return timeToDest;
	}

	/**
	 * @param timeToDest the timeToDest to set
	 */
	public void setTimeToDest(int timeToDest) {
		this.timeToDest = Math.max(0, timeToDest);
	}

	/**
	 * @return information about the packet
	 */
	public String toString() {
		return "[" + id + ", " + timeArrive + ", " + timeToDest + "]";
	}
}
