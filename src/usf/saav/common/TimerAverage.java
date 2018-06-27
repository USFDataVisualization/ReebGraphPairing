package usf.saav.common;

public class TimerAverage implements Timer {

	private Timer baseTimer;
	private double msTotal = 0;
	private double nsTotal = 0;
	private int count = 0;
	
	public TimerAverage( Timer _baseTimer ) {
		baseTimer = _baseTimer;
	}
	@Override
	public void start() {
		baseTimer.start();
	}

	@Override
	public void end() {
		baseTimer.end();
		msTotal += baseTimer.getElapsedMilliseconds();
		nsTotal += baseTimer.getElapsedNanoseconds();
		count++;
	}

	@Override
	public double getElapsedMilliseconds() {
		return msTotal / count;
	}

	@Override
	public double getElapsedNanoseconds() {
		return nsTotal / count;
	}

}
