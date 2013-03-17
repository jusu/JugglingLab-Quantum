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
	JMLPattern pat;

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

	public void countCatch(int path, int direction) {
		if (patCounter < pattern.length()) {
			char c = pattern.charAt(patCounter);
			sender.send(String.format("b %d %d", path, direction));
		}

		if (++patCounter >= pattern.length()) {
			patCounter = 0;
		}
	}

	public void updateAnimation(int pathnum, int height, int x, int y) {
		String s = String.format("xy %d %d %d %d", pathnum, height, x, y);
		// String s = "xy " + pathnum + " " + height + " " + x + " " + y;
		sender.send(s);
	}

	public void run() {
		while (keepRunning) {
			String s = receiver.receive();

			if (s != null) {
				String[] a = s.split(" ");
				if (a.length >= 2) {
					if (a[0].equals("pat")) {
						String pat = a[1];

						try {
							Notation not = Notation
									.getNotation(Notation.builtinNotations[0]);
							JMLPattern p = not.getJMLPattern("pattern=" + pat
									+ ";prop=ball");

							if (this.animator.parentView != null) {
								this.animator.parentView.restartView(p,
										new AnimatorPrefs());
							}
						} catch (JuggleExceptionUser e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JuggleExceptionInternal e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (a[0].equals("speed")) {
						String sp = a[1];
						double speed = Double.valueOf(sp);
						if (speed > 0.0 && this.animator.parentView != null) {
							AnimatorPrefs prefs = new AnimatorPrefs();
							AnimatorPrefs.slowdown_def = speed;
							prefs.slowdown = speed;
							try {
								double curTime = this.animator.getTime();

								Notation not = Notation
										.getNotation(Notation.builtinNotations[0]);
								JMLPattern p = not.getJMLPattern("pattern="
										+ pattern + ";prop=ball");

								System.out.println("speed=" + speed);
								this.animator.parentView.restartView(p, prefs);

								this.animator.setTime(curTime);
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
}
