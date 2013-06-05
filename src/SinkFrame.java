import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * The SinkFrame class for creating a new sink GUI window.
 */
public class SinkFrame extends JFrame {

	private SinkPanel sinkPanel;

	/**
	 * Constructs a new JFrame for the specified sink.
	 * @param sink - The sink the frame represents.
	 * @param title - The title of the JFrame
	 */
	public SinkFrame(final NotificationSink sink, String title) {
		super(title);

		// get look and feel of OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to load system LookAndFeel.");
		}

		// get dimensions
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = 860;
		int frameHeight = 500;

		// add panel
		sinkPanel = new SinkPanel(sink, new Dimension(frameWidth, frameHeight));
		setContentPane(sinkPanel);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					sink.deregisterAll();
				} catch (MalformedURLException | RemoteException | NotBoundException e1) {
					// do nothing
				}
				dispose();
			}
		});

		// align window
		setBounds((int) (dim.getWidth() - frameWidth) / 2,
				(int) (dim.getHeight() - frameHeight) / 2,
				frameWidth, frameHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}

}
