import java.io.Serializable;
import java.util.Random;

/**
 * The serializable Activity object that is passed around remotely.
 */
public class Activity implements Serializable, ActivityInterface {

	private static final long serialVersionUID = 1L;
	private String id;
	private String title;
	private int min;
	private int attendance;

	/**
	 * Constructs a new activity object and generates a unique ID;
	 * @param title - The title of the activity.
	 */
	public Activity(String title, int min) {
		Random randomGen = new Random();
		id = String.valueOf(randomGen.nextInt(1000));
		this.title = title;
		this.min = min;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String toString() {
		return title;
	}

	@Override
	public int getMinPeople() {
		return min;
	}

	@Override
	public void incrementJoining() {
		attendance++;
	}

	@Override
	public int getNumJoined() {
		return attendance;
	}

}
