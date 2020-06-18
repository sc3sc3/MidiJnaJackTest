package midi;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.midi.ShortMessage;

import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.JackMidi;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPort;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackProcessCallback;
import org.jaudiolibs.jnajack.JackStatus;

public class JnaJackClient implements JackProcessCallback {
	private Jack jack;
	private JackClient jackClient;
	private JackPort outputPort;
	private ConcurrentLinkedQueue<ShortMessage> messageQueue;

	public JnaJackClient() {
		messageQueue = new ConcurrentLinkedQueue<>();
		try {
			EnumSet<JackOptions> options = EnumSet.of(JackOptions.JackNoStartServer);
			EnumSet<JackStatus> status = EnumSet.noneOf(JackStatus.class);
			this.jack = Jack.getInstance();
			this.jackClient = this.jack.openClient("TestJackMidi", options, status);

			EnumSet<JackPortFlags> outputFlags = EnumSet.of(JackPortFlags.JackPortIsOutput);
			this.outputPort = this.jackClient.registerPort("MIDI_out", JackPortType.MIDI, outputFlags);

			this.jackClient.setProcessCallback(this);
			this.jackClient.activate();
		} catch (JackException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean process(JackClient client, int frame) {
		try {
			JackMidi.clearBuffer(this.outputPort);
		} catch (JackException e) {
			e.printStackTrace();
		}
		while (messageQueue.size() > 0) {
			ShortMessage msg = messageQueue.remove();
			try {
				JackMidi.eventWrite(this.outputPort, 0, msg.getMessage(), msg.getLength());
			} catch (JackException e) {
				e.printStackTrace();
				continue;
			}
		}
		return true;
	}

	public void processMidiMessage(ShortMessage shortMessage) {
		messageQueue.add(shortMessage);
	}
}
