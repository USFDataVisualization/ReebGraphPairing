/*
 *     saav-core - A (very boring) software development support library.
 *     Copyright (C) 2016 PAUL ROSEN
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the Paul Rosen at <prosen@usf.edu>.
 */
package usf.saav.common.jocl;

import org.jocl.CL;
import org.jocl.CLException;
import org.jocl.Pointer;
import org.jocl.cl_event;

import usf.saav.common.EventTimer;

public class joclEvent implements EventTimer {

	public  cl_event event;
	private String   name;
	
	public joclEvent( String name, cl_event event ){
		this.event = event;
		this.name  = name;
	}
	
	@Override 
	public long getStartTime(){
		long []time_start = {0};
		Pointer ptime_start = Pointer.to( time_start );
		try{
			CL.clGetEventProfilingInfo(event, CL.CL_PROFILING_COMMAND_START, 1*8, ptime_start, null);
		}
		catch( CLException e ){
			//e.printStackTrace();
			System.err.println("Start Time Profile Data Not Available");
			return Long.MIN_VALUE;
		}
		return time_start[0];
	}
	
	@Override 
	public long getEndTime(){
		long []time_end = {0};
		Pointer ptime_end   = Pointer.to( time_end );
		try{
			CL.clGetEventProfilingInfo(event, CL.CL_PROFILING_COMMAND_END, 1*8, ptime_end, null);
		}
		catch( CLException e ){
			//e.printStackTrace();
			System.err.println("End Time Profile Data Not Available");
			return Long.MAX_VALUE;
		}
		return time_end[0];
	}
	
	@Override 
	public long getElapsedTime(){
		return getEndTime() - getStartTime();
	}
	
	@Override 
	public double getElapsedTimeMilliseconds(){
		return ((double)getElapsedTime() / 1000000.0);
	}

	@Override public String getName() { return name; }
	
}
