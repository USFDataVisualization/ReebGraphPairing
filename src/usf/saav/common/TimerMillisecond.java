package usf.saav.common;

public class TimerMillisecond implements Timer {

	long start,end;
	
	@Override
	public void start() {
		end = start = System.currentTimeMillis();	
	}
	
	@Override
	public void end() {
		end = System.currentTimeMillis();
		//return (end-start);
	}

	@Override
	public double getElapsedMilliseconds() {
		return (double)(end-start);
	}
	@Override
	public double getElapsedNanoseconds() {
		return (double)(end-start)*10e6;
	}

}
