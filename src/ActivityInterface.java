
/**
 * The Activity object interface that provides getters and setters for Activity attributes.
 */
public interface ActivityInterface {

	/**
	 * Gets the ID of the activity.
	 * @return the ID.
	 */
	public String getID();

	/**
	 * Gets the minimum number of people for an activity to happen.
	 * @return the minimum number of people.
	 */
	public int getMinPeople();
	
	/**
	 * Increments the number of people attending the activity.
	 */
	public void incrementJoining();
	
	/**
	 * Gets the number of people attending the activity.
	 * @return the number of people.
	 */
	public int getNumJoined();

}
