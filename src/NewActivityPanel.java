import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The class representing the JPanel used to create a new Activity object.
 */
public class NewActivityPanel extends JPanel {

	private static JFrame newActivityFrame;
	private static SinkPanel sinkPanel;
	private static JButton saveBtn;

	/**
	 * Constructs a new panel.
	 * @param panel - The sink panel used to call it.
	 */
	public NewActivityPanel(JFrame frame, SinkPanel panel) {
		super();
		sinkPanel = panel;
		newActivityFrame = frame;
		init();
		frame.pack();
	}

	/**
	 * Initialises the panel's components.
	 */
	private void init() {

		setLayout(new FlowLayout(FlowLayout.LEADING));
		setPreferredSize(new Dimension(400, 200));

		final JTextField titleField = new JTextField(20);

		Integer[] numbers = {2, 3, 4, 5, 6, 7, 8, 9, 10}; 
		final JComboBox<Integer> peopleField = new JComboBox<Integer>(numbers);

		saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveInfo(titleField.getText(), (Integer) peopleField.getSelectedItem());
				newActivityFrame.dispose();
			}
		});

		add(titleField);
		add(peopleField);
		add(saveBtn);

	}

	/**
	 * Creates a new activity object based on the user's entered information.
	 * @param title - The title of the activity.
	 * @param num - The minimum number of people required.
	 */
	private void saveInfo(String title, int num) {
		Activity activity = new Activity(title, num);
		sinkPanel.addActivity(activity);
	}

}
