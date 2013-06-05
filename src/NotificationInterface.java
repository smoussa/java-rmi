import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The notification interface for the Notification object for remote communication.
 */
public interface NotificationInterface extends Remote {

	/**
	 * Creates an activity using the source's remote control object.
	 * @param activity - The activity to create.
	 * @throws RemoteException
	 */
	public void createActivity(Activity activity) throws RemoteException;

	/**
	 * Returns the activity this notification object holds.
	 * @return the activity object.
	 * @throws RemoteException
	 */
	public Activity getActivity() throws RemoteException;

	/**
	 * Uses the remote control object to get all the source's activities.
	 * @return an ArrayList of activities.
	 * @throws RemoteException
	 */
	public ArrayList<Activity> getAllActivities() throws RemoteException;
	
	/**
	 * Updates the passed in sink with new notifications.
	 * @param sinkID - The sink to notify.
	 * @return the notification object to call.
	 * @throws RemoteException
	 */
	public NotificationInterface updateMe(String sinkID) throws RemoteException;

	/**
	 * Increments the number of sinks registered to the source.
	 * @throws RemoteException
	 */
	public void incrementSinkCount() throws RemoteException;
	
	/**
	 * Decrements the number of sinks registered to the source.
	 * @throws RemoteException
	 */
	public void decrementSinkCount() throws RemoteException;

}
