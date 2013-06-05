import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

/**
 * The main sink interface panel class
 */
public class SinkPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final NotificationSink sink;
	private JComboBox<String> availableGroupsList;
	private DefaultListModel<Object> activityListModel;
	private int chosenSourceIndex;

	/**
	 * Constructs a new panel with the specified size dimension.
	 * @param sink - The sink the panel represents.
	 * @param dimension - The dimension of the window.
	 */
	public SinkPanel(final NotificationSink sink, Dimension dimension) {
		super();
		this.sink = sink;

		final Timer t = new Timer(); // timer to continuously update with new activities
		t.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				NotificationInterface notification;
				try {
					notification = sink.refresh();
					if (notification != null) {
						display(notification.getActivity());
					}
				} catch (RemoteException e) {
					try {
						Thread.sleep(3000);
						notification = sink.refresh();
						if (notification != null) {
							display(notification.getActivity());
						}
					} catch (InterruptedException | RemoteException e1) {
						display("Disconnected from the group.");
					}
					t.cancel();
				}
			}
		}, 1000, 3000);

		initLayout(dimension);
	}

	/**
	 * Initialises the panel's components.
	 * @param dimension - The dimension of the window.
	 */
	private void initLayout(Dimension dimension) {

		// define layout
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		setPreferredSize(dimension);
		setBackground(new Color(250, 250, 250));

		// create colours and fonts
		Color black = new Color(30, 30, 30);
		Font labelFont = new Font(null, Font.PLAIN, 20);
		Font compFont = new Font(null, Font.PLAIN, 14);

		// create drop down components
		String[] availableGroups = { "Running", "Football", "Oceana" };
		availableGroupsList = new JComboBox<String>(availableGroups);
		availableGroupsList.setSelectedIndex(0);
		chosenSourceIndex = 0;

		// create labels
		JLabel availableGroupsLabel = new JLabel("Available groups");
		JLabel viewGroupsLabel = new JLabel("Views registered groups");
		JLabel myActivitiesLabel = new JLabel("My activities");

		// create buttons
		final JButton registerBtn = new JButton("Register");
		final JButton createBtn = new JButton("Create activity");
		final JButton joinBtn = new JButton("Join");

		// create list of activities
		activityListModel = new DefaultListModel<Object>();
		final JList<Object> activityList = new JList<Object>(activityListModel);
		activityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		activityListModel.add(0, "");
		JScrollPane scrollPane = new JScrollPane(activityList);

		// add listeners
		availableGroupsList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chosenSourceIndex = ((JComboBox) e.getSource()).getSelectedIndex();
				if (sink.isRegistered(chosenSourceIndex)) {
					registerBtn.setText("Deregister");
				} else {
					registerBtn.setText("Register");
				}
			}
		});

		registerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String sourceName = "";
				if (!sink.isRegistered(chosenSourceIndex)) {
					try {
						sourceName = sink.register(chosenSourceIndex);
						for (Activity activity : sink.getActivities(chosenSourceIndex)) {
							display(activity);
						}
						display("Available activities: ");
						display("\nYou are now registered to the " + sourceName + " group.");
						registerBtn.setText("Deregister");
					} catch (RemoteException | MalformedURLException | NotBoundException e) {
						display("This group has not been started yet.");
					}
				} else {
					try {
						sourceName = sink.deregister(chosenSourceIndex);
					} catch (MalformedURLException | RemoteException | NotBoundException e) {
						display("Could not deregister");
					}
					display("\nYou have left the " + sourceName + " group.");
					registerBtn.setText("Register");
				}
			}
		});

		createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showActivityForm();
			}
		});

		joinBtn.setVisible(false);
		joinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Activity activity = (Activity) activityList.getSelectedValue();
				activity.incrementJoining();
				display("There are now " + activity.getNumJoined() + " people attending.");
				display("\nYou have joined the " + activity + " activity.");
			}
		});

		activityList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(
					JList list, Object obj, int index, boolean isSelected, boolean cellHasFocus) {
				if (!(obj instanceof Activity)) {
					cellHasFocus = false;
					isSelected = false;
				}
				super.getListCellRendererComponent(list, obj, index, isSelected, cellHasFocus);
				return this;
			}
		});

		activityList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Object element = ((JList) e.getSource()).getSelectedValue();
				if (element instanceof Activity) {
					joinBtn.setVisible(true);
				} else {
					joinBtn.setVisible(false);
				}
			}
		});

		// set styles
		availableGroupsLabel.setFont(labelFont);
		availableGroupsLabel.setForeground(black);
		viewGroupsLabel.setFont(labelFont);
		viewGroupsLabel.setForeground(black);
		myActivitiesLabel.setFont(labelFont);
		myActivitiesLabel.setForeground(black);

		availableGroupsList.setFont(compFont);
		registerBtn.setFont(compFont);
		createBtn.setFont(compFont);
		joinBtn.setFont(compFont);
		availableGroupsList.setBackground(null);
		registerBtn.setBackground(null);
		createBtn.setBackground(null);
		joinBtn.setBackground(null);

		activityList.setFont(compFont);

		// align panel and its components
		layout.putConstraint(SpringLayout.NORTH, availableGroupsLabel, 30, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, availableGroupsLabel, 30, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, availableGroupsList, 15, SpringLayout.SOUTH, availableGroupsLabel);
		layout.putConstraint(SpringLayout.WEST, availableGroupsList, 0, SpringLayout.WEST, availableGroupsLabel);
		layout.putConstraint(SpringLayout.NORTH, registerBtn, 0, SpringLayout.NORTH, availableGroupsList);
		layout.putConstraint(SpringLayout.WEST, registerBtn, 20, SpringLayout.EAST, availableGroupsList);
		layout.putConstraint(SpringLayout.NORTH, createBtn, 0, SpringLayout.NORTH, availableGroupsList);
		layout.putConstraint(SpringLayout.WEST, createBtn, 20, SpringLayout.EAST, registerBtn);
		layout.putConstraint(SpringLayout.NORTH, joinBtn, 0, SpringLayout.NORTH, availableGroupsList);
		layout.putConstraint(SpringLayout.WEST, joinBtn, 50, SpringLayout.EAST, createBtn);

		layout.putConstraint(SpringLayout.NORTH, viewGroupsLabel, 30, SpringLayout.SOUTH, availableGroupsList);
		layout.putConstraint(SpringLayout.WEST, viewGroupsLabel, 0, SpringLayout.WEST, availableGroupsList);
		layout.putConstraint(SpringLayout.NORTH, scrollPane, 30, SpringLayout.SOUTH, viewGroupsLabel);
		layout.putConstraint(SpringLayout.WEST, scrollPane, 30, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, scrollPane, -30, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, scrollPane, -30, SpringLayout.SOUTH, this);

		// add panel and its components
		add(availableGroupsLabel);
		add(availableGroupsList);
		add(registerBtn);
		add(createBtn);
		setLayout(layout);
		add(viewGroupsLabel);
		add(joinBtn);
		add(scrollPane);

	}

	/**
	 * Adds the activity created by the user.
	 * @param activity - The activity to create.
	 */
	public void addActivity(Activity activity) {
		String name = availableGroupsList.getItemAt(chosenSourceIndex);
		try {
			if (activity.toString() != "") {
				sink.saveActivity(chosenSourceIndex, activity);
				display("You have added " + activity + " to the group " + name);
			} else {
				display("Sorry, you need to enter a name for the activity.");
			}
		} catch (RemoteException | NullPointerException e) {
			display("You have not registered to " + name + " yet.");
		}
	}

	/**
	 * Outputs information to the user via the JList.
	 * @param object - The object to display.
	 */
	public void display(Object object) {
		activityListModel.add(0, object);
	}

	/**
	 * Shows the window to create a new activity.
	 */
	private void showActivityForm() {

		JFrame frame = new JFrame("New Activity");

		// get look and feel of OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to load system LookAndFeel.");
		}

		// get dimensions
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = 400;
		int frameHeight = 200;

		// add panel
		frame.add(new NewActivityPanel(frame, this));

		// align window
		frame.setBounds((int) (dim.getWidth() - frameWidth) / 2,
				(int) (dim.getHeight() - frameHeight) / 2,
				frameWidth, frameHeight);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

	}

}
