package remote;

import jugglinglab.core.Animator;
import jugglinglab.core.AnimatorPrefs;
import jugglinglab.jml.JMLPattern;
import jugglinglab.notation.Notation;
import jugglinglab.util.JuggleExceptionInternal;
import jugglinglab.util.JuggleExceptionUser;

public class RemoteControl extends Thread {

	int patCounter = 0;
	String pattern = "";

	boolean keepRunning = true;
	UDPSender sender = new UDPSender();
	UDPReceiver receiver = new UDPReceiver();
	final private Animator animator;

	public RemoteControl(Animator a) {
		this.animator = a;
	}

	public void setPattern(String p) {
		pattern = p;
		patCounter = 0;
	}

	public void countCatch() {
		if (patCounter < pattern.length()) {
			char c = pattern.charAt(patCounter);
			sender.send("b " + c);
		}

		if (++patCounter >= pattern.length()) {
			patCounter = 0;
		}
	}

	public void updateAnimation(int i, int x, int y) {
		String s = "xy " + i + " " + x + " " + y;
		sender.send(s);
	}

	public void run() {
		while (keepRunning) {
			String s = receiver.receive();
			System.out.println("RECEIVED: " + s);

			if (s != null) {
				String[] a = s.split(" ");
				if (a.length >= 2) {
					if (a[0].equals("pat")) {
						String pat = a[1];
						System.out.println("new pattern: " + pat);

						try {
							Notation not = Notation
									.getNotation(Notation.builtinNotations[0]);
							JMLPattern p = not.getJMLPattern("pattern=" + pat
									+ ";prop=ball");
							this.animator.restartJuggle(p, new AnimatorPrefs());
						} catch (JuggleExceptionUser e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JuggleExceptionInternal e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
