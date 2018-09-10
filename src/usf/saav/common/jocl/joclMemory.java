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

import static org.jocl.CL.clEnqueueReadBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.cl_event;
import org.jocl.cl_mem;

public class joclMemory {

	private joclDevice device;
	private long	   size;
	private String	   name;

	cl_mem memory;

	
	joclMemory(joclDevice device, String name, long flags, long size, Pointer host_data, int[] err_codes){
		this.device = device;
		this.size   = size;
		this.memory = CL.clCreateBuffer( device.context, flags, size, host_data, err_codes );
		this.name   = name;
	}
	
	public void release( ){
		CL.clReleaseMemObject(memory);
		memory = null;
	}

	public long size() {
		return size;
	}

	

	public joclEvent enqueueWriteBuffer( boolean blocking_write, long offset, long cb, Pointer ptr, int num_events_in_wait_list, cl_event[] event_wait_list ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( CL.clEnqueueWriteBuffer( device.commandQueue, memory, blocking_write, offset, cb, ptr, num_events_in_wait_list, event_wait_list, event) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueWriteBuffer", event);
	}

	public joclEvent enqueueWriteBuffer( boolean blocking_write, float [] data, cl_event...wait_events ) throws joclException {
		if( memory == null ) return null;
		if( size < data.length*4 ){
			System.err.println("joclMemory: buffer too small for data");
			return null;
		}
		cl_event event = new cl_event();
		if( CL.clEnqueueWriteBuffer( device.commandQueue, memory, blocking_write, 0, data.length*4, Pointer.to(data), wait_events.length, wait_events, event) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueWriteBuffer", event);
	}
	
	public joclEvent enqueueWriteBuffer( boolean blocking_write, float [] data ) throws joclException {
		if( memory == null ) return null;
		if( size < data.length*4 ){
			System.err.println("joclMemory: buffer too small for data");
		}
		cl_event event = new cl_event();
		if( CL.clEnqueueWriteBuffer( device.commandQueue, memory, blocking_write, 0, data.length*4, Pointer.to(data), 0, null, event ) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueWriteBuffer", event);
	}

	public joclEvent enqueueWriteBuffer( boolean blocking_write, int [] data ) throws joclException {
		if( memory == null ) return null;
		if( size < data.length*4 ){
			System.err.println("joclMemory: buffer too small for data");
		}
		cl_event event = new cl_event();
		if( CL.clEnqueueWriteBuffer( device.commandQueue, memory, blocking_write, 0, data.length*4, Pointer.to(data), 0, null, event ) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueWriteBuffer", event);
	}

	public joclEvent enqueueWriteBuffer( boolean blocking_write, ByteBuffer src ) throws joclException {
		src.rewind();
		if( memory == null ) return null;
		if( size < src.capacity() ){
			System.err.println("joclMemory: buffer too small for data");
		}
		cl_event event = new cl_event();
		if( CL.clEnqueueWriteBuffer( device.commandQueue, memory, blocking_write, 0, src.capacity(), Pointer.to(src), 0, null, event ) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueWriteBuffer", event);
	}

	public joclEvent enqueueReadBuffer( boolean blocking, float [] dst ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, 0, 4 * dst.length, Pointer.to(dst), 0, null, event) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueWriteBuffer", event);
	}

	public joclEvent enqueueReadBuffer( boolean blocking, float [] dst, cl_event...wait_events ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( wait_events.length == 0 ){
			if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, 0, 4 * dst.length, Pointer.to(dst), 0, null, event) != CL.CL_SUCCESS ){
				throw new joclException();
			}
		}
		else{
			if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, 0, 4 * dst.length, Pointer.to(dst), wait_events.length, wait_events, event) != CL.CL_SUCCESS ){
				throw new joclException();
			}
		}
		return new joclEvent(name+".EnqueueWriteBuffer", event);
	}

	
	public joclEvent enqueueReadBuffer( boolean blocking, ByteBuffer dst, cl_event...wait_events ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( wait_events.length == 0 ){
			if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, 0, dst.capacity(), Pointer.to(dst), 0, null, event) != CL.CL_SUCCESS ){
				throw new joclException();
			}
		}
		else{
			if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, 0, dst.capacity(), Pointer.to(dst), wait_events.length, wait_events, event) != CL.CL_SUCCESS ){
				throw new joclException();
			}
		}
		return new joclEvent(name+".EnqueueReadBuffer", event);
	}

	public joclEvent enqueueReadBuffer( boolean blocking, int [] dst ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, 0, 4 * dst.length, Pointer.to(dst), 0, null, event) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueReadBuffer", event);
	}

	public joclEvent enqueueReadBuffer( boolean blocking, int byte_offset, int [] dst ) throws joclException {
		return __enqueueReadBuffer( blocking, byte_offset, Pointer.to(dst), dst.length*4 );
	}
	public joclEvent enqueueReadBuffer( boolean blocking, int byte_offset, int [] dst, cl_event...wait_events ) throws joclException {
		return __enqueueReadBuffer( blocking, byte_offset, Pointer.to(dst), dst.length*4, wait_events );
	}

	
	private joclEvent __enqueueReadBuffer( boolean blocking, int byte_offset, Pointer dst, long dst_bytes, cl_event...wait_events ) throws joclException{
		if( memory == null ) return null;
		cl_event event = new cl_event();
		int wait_len = (wait_events==null)?0:wait_events.length;
		if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, byte_offset, dst_bytes, dst, wait_len, wait_events, event) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueReadBuffer", event);
	}

	
	
	public joclEvent enqueueReadBuffer( boolean blocking, byte [] dst ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( clEnqueueReadBuffer( device.commandQueue, memory, blocking, 0, dst.length, Pointer.to(dst), 0, null, event) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueReadBuffer", event);
	}

	public joclEvent enqueueFillBuffer( byte[] val ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( CL.clEnqueueFillBuffer(device.commandQueue, memory, Pointer.to(val), val.length, 0, size, 0, null, event ) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueFillBuffer", event);
	}
	
	public joclEvent enqueueFillBuffer( byte[] val, cl_event ... event_list ) throws joclException {
		if( memory == null ) return null;
		cl_event event = new cl_event();
		if( CL.clEnqueueFillBuffer(device.commandQueue, memory, Pointer.to(val), val.length, 0, size, event_list.length, event_list, event ) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		return new joclEvent(name+".EnqueueFillBuffer", event);
	}
	
	public joclEvent enqueueFillBuffer(int val ) throws joclException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order( ByteOrder.LITTLE_ENDIAN );
		bb.putInt( val );
		bb.rewind();
		return this.enqueueFillBuffer( bb.array() );
	}

	public joclEvent enqueueFillBuffer(int val, cl_event ... event_list ) throws joclException {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order( ByteOrder.LITTLE_ENDIAN );
		bb.putInt( val );
		bb.rewind();
		return this.enqueueFillBuffer( bb.array(), event_list );
	}

	public joclEvent enqueueFillBuffer(int [] val, cl_event ... event_list ) throws joclException {
		ByteBuffer bb = ByteBuffer.allocate(4*val.length);
		bb.order( ByteOrder.LITTLE_ENDIAN );
		for( int v : val ){
			bb.putInt( v );
		}
		bb.rewind();
		return this.enqueueFillBuffer( bb.array(), event_list );
	}



	
}
