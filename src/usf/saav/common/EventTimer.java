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
package usf.saav.common;

import java.util.Vector;

public interface EventTimer {
	
	public String getName();
	
	public long   getStartTime();
	public long   getEndTime();
	public long   getElapsedTime();
	public double getElapsedTimeMilliseconds();
	
	public class Default implements EventTimer {
		long proc_start;
		long proc_end;
		String name;

		public Default( String name ){
			this.name = name;
		}

		public void start(){ proc_start = System.nanoTime(); }
		public void stop(){  proc_end   = System.nanoTime(); }

		@Override public String getName() { return name; }
		
		@Override public long   getStartTime() { 	return proc_start; 	}
		@Override public long   getEndTime() {		return proc_end; 	}
		@Override public long   getElapsedTime() {	return getEndTime()-getStartTime();	}
		@Override public double getElapsedTimeMilliseconds() {	return (double)getElapsedTime()*1.0e-6;	}

	}
	
	public class CombinedEvents implements EventTimer {
		String name;
		Vector<EventTimer> events = new Vector<EventTimer>();
		
		public CombinedEvents( String name ){
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
		
		public void add( EventTimer e ){
			events.add(e);
		}

		@Override
		public long getStartTime() {
			long start = Long.MAX_VALUE;
			for( EventTimer e : events ){
				start = Math.min( start, e.getStartTime() );
			}
			return start;
		}

		@Override
		public long getEndTime() {
			long end = 0;
			for( EventTimer e : events ){
				end = Math.max( end, e.getEndTime() );
			}
			return end;
		}

		@Override
		public long getElapsedTime() {
			long total = 0;
			for( EventTimer e : events ){
				total += e.getElapsedTime();
			}
			return total;
		}

		@Override public double getElapsedTimeMilliseconds() {	return (double)getElapsedTime()*1.0e-6;	}
		
	}
}
