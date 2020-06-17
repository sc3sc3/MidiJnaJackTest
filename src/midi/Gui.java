package midi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Gui extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int MIDI_CHANNEL = 0;
	private static final int VELOCITY = 60;
	private JnaJackClient jackClient;
	private Random random;

	public Gui() {
		super();
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton button = new JButton("Midi");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int note = random.nextInt(50) + 40;
				ShortMessage msg = new ShortMessage();
				try {
					msg.setMessage(ShortMessage.NOTE_ON, MIDI_CHANNEL, note, VELOCITY);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
				jackClient.processMidiMessage(msg);

				try {
					// play not for half a second
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				msg = new ShortMessage();
				try {
					msg.setMessage(ShortMessage.NOTE_OFF, MIDI_CHANNEL, note, VELOCITY);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
				jackClient.processMidiMessage(msg);
			}
		});
		add(button);
		this.jackClient = new JnaJackClient();
		this.random = new Random();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Gui gui = new Gui();
				gui.setVisible(true);

			}
		});
	}
}
