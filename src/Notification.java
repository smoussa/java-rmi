import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * The remote object class that allows sinks to access source methods.
 */
public class Notification extends UnicastRemoteObject implements NotificationInterface {
	
	private static final long serialVersionUID = 1L;
	private NotificationSource source;
	private Activity activity;

	/**
	 * Constructor for creating a new source remote control object.
	 * @param source
	 * @throws RemoteException
	 */
	protected Notification(NotificationSource source) throws RemoteException {
		super();
		this.source = source;
	}

	/**
	 * Constructor for creating a new activity notification object.
	 * @param activity - The activity this notification stores.
	 * @throws RemoteException
	 */
	protected Notification(Activity activity) throws RemoteException {
		super();
		this.activity = activity;
	}

	@Override
	public void createActivity(Activity activity) throws RemoteException { 
		source.registerObject(new Notification(activity));
	}

	@Override
	public Activity getActivity() throws RemoteException {
		return activity;
	}

	@Override
	public ArrayList<Activity> getAllActivities() throws RemoteException {
		return source.getActivities();
	}

	@Override
	public synchronized NotificationInterface updateMe(String sinkID) throws RemoteException {
		return source.getNotification(sinkID);
	}

	@Override
	public synchronized void incrementSinkCount() throws RemoteException {
		source.incrementSinkNum();
	}
	
	@Override
	public synchronized void decrementSinkCount() throws RemoteException {
		source.decrementSinkNum();
	}

}
