import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * The notification source class that serves multiple sinks.
 */
public class NotificationSource {

	private static Registry registry;
	private ArrayList<Activity> activities; // list of activities stored
	private HashMap<String, Boolean> sinksRequested; // sinks waiting to receive a notification (true if waiting)
	private ArrayBlockingQueue<NotificationInterface> notificationQueue; // notification queue
	private int fetchCount; // a count of how many sinks have fetched
	private int registeredSinksCount; // a count of how many sinks are registered.

	/**
	 * Constructs a new NotificationSource object.
	 * @param name - The name of the source.
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public NotificationSource(String name) throws RemoteException, UnknownHostException {
		//init(name);
	}

	public static void main(String[] args) {
		try {
			registry = LocateRegistry.createRegistry(1099);
			new NotificationSource("Running");
			new NotificationSource("Football");
			new NotificationSource("Oceana");
		} catch (RemoteException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialises data structures and variables.
	 * @param name
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	private void init(String name) throws RemoteException, UnknownHostException {

		activities = new ArrayList<Activity>();
		sinksRequested = new HashMap<String, Boolean>();
		notificationQueue = new ArrayBlockingQueue<NotificationInterface>(10);
		fetchCount = 0;
		registeredSinksCount = 0;
		Notification stub = new Notification(this); // create a new remote object to access this source
		registry.rebind(name, stub);

	}

	/**
	 * Returns the list of activities stored by the source before a NotificationSink has registered.
	 * @return the list of activities.
	 */
	public synchronized ArrayList<Activity> getActivities() {
		return activities;
	}

	/**
	 * Registers a notification object in the registry.
	 * @param notification - The Notification object to register.
	 */
	public synchronized void registerObject(Notification notification) {
		try {
			notificationQueue.add(notification);
			Activity activity = notification.getActivity();
			String activityID = activity.getID();
			registry.rebind(activityID, notification);
			activities.add(activity);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the next notification on the queue of notifications not yet distributed.
	 * @param sinkID - The NotificationSink requesting a Notification object.
	 * @return
	 */
	public synchronized NotificationInterface getNotification(String sinkID) {

		NotificationInterface notification = null;
		if (!notificationQueue.isEmpty()) {
			if ((fetchCount + 1) == registeredSinksCount) {
				if (!sinksRequested.containsKey(sinkID) || sinksRequested.get(sinkID) == false) {
					sinksRequested.put(sinkID, false);
					notification = notificationQueue.poll();
					fetchCount = 0;
					Iterator iterator = sinksRequested.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, Boolean> sink = (Map.Entry<String, Boolean>) iterator.next();
						sink.setValue(false);
					}
					iterator.remove();
				}
			} else if (!sinksRequested.containsKey(sinkID) || sinksRequested.get(sinkID) == false) {
				notification = notificationQueue.peek();
				fetchCount++;
				sinksRequested.put(sinkID, true);
			}
		}

		return notification;
	}

	/**
	 * Increments the number of sinks registered to the source.
	 */
	public synchronized void incrementSinkNum() {
		registeredSinksCount++;
	}

	/**
	 * Decrements the number of sinks registered to the source.
	 */
	public synchronized void decrementSinkNum() {
		registeredSinksCount--;
	}

}
