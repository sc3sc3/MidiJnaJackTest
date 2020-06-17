package midi;

import java.util.EnumSet;

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

	public JnaJackClient() {
		try {
			EnumSet<JackOptions> options = EnumSet.of(JackOptions.JackNoStartServer);
			EnumSet<JackStatus> status = EnumSet.noneOf(JackStatus.class);
			this.jack = Jack.getInstance();
			this.jackClient = this.jack.openClient("TestJackMidi", options, status);
			EnumSet<JackPortFlags> flags = EnumSet.of(JackPortFlags.JackPortIsOutput);
			this.outputPort = this.jackClient.registerPort("MIDI_out", JackPortType.MIDI, flags);
			this.jackClient.setProcessCallback(this);
			this.jackClient.activate();
		} catch (JackException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean process(JackClient client, int frame) {
		System.out.println("process frame: " + frame);
		return true;
	}

	public void processMidiMessage(ShortMessage shortMessage) {
		System.out.println("processMidiMessage: " + shortMessage + ", on port: " + this.outputPort.getName());
		try {
			JackMidi.clearBuffer(this.outputPort);
		} catch (JackException e) {
			e.printStackTrace();
		}
		try {
			JackMidi.eventWrite(this.outputPort, 300, shortMessage.getMessage(), shortMessage.getLength());
		} catch (JackException e) {
			e.printStackTrace();
		}
	}
}
