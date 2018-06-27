package usf.saav.common;

public interface Timer {

	public void start();
	public void end();
	public double getElapsedMilliseconds();
	public double getElapsedNanoseconds();
}
