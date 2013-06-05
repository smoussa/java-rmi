import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * The notification sink class that requests notifications from sources.
 */
public class NotificationSink {

	private static String hostAddress = "rmi://localhost:1099/";
	public static String[] stubNames;
	private NotificationInterface[] stubs;
	private Boolean[] registered;
	private HashMap<Integer, ArrayList<Activity>> sourceActivities;
	private static Random randomGen = new Random();
	private String sinkID;

	/**
	 * Constructs a new NotificationSink object and starts the GUI.
	 */
	public NotificationSink(int numSources) {
		init(numSources);
		new SinkFrame(this, "Sink");
	}

	public static void main(String[] args) {
		new NotificationSink(3);
	}

	/**
	 * Initialises data structures and variables.
	 */
	private void init(int num) {

		sinkID = "sink" + String.valueOf(randomGen.nextInt(1000));
		sourceActivities = new HashMap<Integer, ArrayList<Activity>>();
		stubNames = new String[num];
		stubs = new NotificationInterface[num];
		registered = new Boolean[num];

		stubNames[0] = "Running";
		stubNames[1] = "Football";
		stubNames[2] = "Oceana";
		registered[0] = false;
		registered[1] = false;
		registered[2] = false;

	}

	/**
	 * Registers this sink with the specified source.
	 * @param index - The index representing the source to connect to.
	 * @return the name of the remote notification control object.
	 * @throws RemoteException 
	 * @throws NotBoundException 
	 * @throws MalformedURLException 
	 */
	public synchronized String register(int index) throws RemoteException, MalformedURLException, NotBoundException {

		registered[index] = true;
		String stubName = stubNames[index];
		stubs[index] = ((NotificationInterface) Naming.lookup(stubName));
		stubs[index].incrementSinkCount();
		sourceActivities.put(index, new ArrayList<Activity>()); // add a new list to hold activities stored by the source
		if (stubs[index].getAllActivities() != null) {
			sourceActivities.get(index).addAll(stubs[index].getAllActivities()); // get the activities of that source
		}

		return stubName;
	}

	/**
	 * Unregisters this sink from the specified source.
	 * @param index - The index representing the source to disconnect from.
	 * @return the name of the remote notification control object.
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public synchronized String deregister(int index) throws MalformedURLException, RemoteException, NotBoundException {

		registered[index] = false;
		String stubName = stubNames[index];
		stubs[index] = ((NotificationInterface) Naming.lookup(stubName));
		stubs[index].decrementSinkCount();
		sourceActivities.remove(index);

		return stubName;
	}


	/**
	 * Unregisters this sink from all registered sources.
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public synchronized void deregisterAll() throws MalformedURLException, RemoteException, NotBoundException {
		for (int i = 0; i < stubs.length; i++) {
			if (registered[i])
				deregister(i);
		}
	}

	/**
	 * Checks whether the sink is registered to the given source.
	 * @param index - The source index.
	 * @return true if the sink is registered.
	 */
	public boolean isRegistered(int index) {
		return registered[index];
	}

	/**
	 * Returns all the activities from the source it is registered to.
	 * @param index - The source index.
	 * @return a list of activities.
	 */
	public ArrayList<Activity> getActivities(int index) {
		return sourceActivities.get(index);
	}

	/**
	 * Saves the activity created to the specified source.
	 * @param srcIndex - The index of the source.
	 * @param activity - The Activity object to save.
	 * @return the Activity object created.
	 * @throws RemoteException 
	 */
	public void saveActivity(int srcIndex, Activity activity) throws RemoteException {
		stubs[srcIndex].createActivity(activity);
		lookupRemoteObject(hostAddress + activity.getID());
	}

	/**
	 * Finds and returns the remote object representing the activity.
	 * @param id - The activity identification name. 
	 * @return the activity from the notification object found.
	 */
	private Activity lookupRemoteObject(String id) {
		Activity activity = null;
		try {
			activity = ((NotificationInterface) Naming.lookup(id)).getActivity();
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return activity;
	}

	/**
	 * Updates the sink with new activities from notifications and displays it to the user.
	 * @throws RemoteException 
	 */
	public synchronized NotificationInterface refresh() throws RemoteException {
		NotificationInterface notification = null;
		for (int i = 0; i < stubs.length; i++) {
			if (registered[i]) {
				if (stubs[i] != null) {
					notification = stubs[i].updateMe(sinkID);
				}
			}
		}
		return notification;
	}

}

