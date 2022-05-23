package updater.credosc.com;

public class RuntimeLogger implements Logger {

	@Override
	public void write(String m) {
		System.out.println("["+DateAndTime.now()+"/Runtime] "+m);
	}

}
