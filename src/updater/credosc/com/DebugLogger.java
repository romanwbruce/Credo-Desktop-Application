package updater.credosc.com;

public class DebugLogger implements Logger {

	@Override
	public void write(String m) {
		System.out.println("["+DateAndTime.now()+"/debug] "+m);
	}

}
