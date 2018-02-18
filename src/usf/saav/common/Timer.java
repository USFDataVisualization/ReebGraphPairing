package usf.saav.common;

public class Timer {

	long start,end;
	
	public void start() {
		end = start = System.currentTimeMillis();	
	}
	public long end() {
		end = System.currentTimeMillis();
		return (end-start);
	}
	public long getElapsed() {
		return (end-start);
	}
}
