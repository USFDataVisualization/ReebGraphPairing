package usf.saav.common;

public class TimerNanosecond implements Timer {
	long start,end;
	
	@Override
	public void start() {
		end = start = System.nanoTime();
	}
	
	@Override
	public void end() {
		end = System.nanoTime();
	}

	@Override
	public double getElapsedMilliseconds() {
		return (double)(end-start)/10e6;
	}
	
	@Override
	public double getElapsedNanoseconds() {
		return (double)(end-start);
	}
}
