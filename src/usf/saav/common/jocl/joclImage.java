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

import static org.jocl.CL.CL_MEM_OBJECT_IMAGE2D;
import static org.jocl.CL.CL_MEM_OBJECT_IMAGE3D;
import static org.jocl.CL.CL_SUCCESS;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.cl_image_desc;
import org.jocl.cl_image_format;
import org.jocl.cl_mem;

public class joclImage {

	private joclDevice device;
	cl_mem     memory;
	cl_image_format img_format;
	cl_image_desc img_desc;

	public joclImage(joclDevice device, long flags, cl_image_format img_format, int w, int h, Pointer data) throws joclException {
		this( device, flags, img_format, w, h, 1, data);
	}	
	public joclImage(joclDevice device, long flags, cl_image_format img_format, int w, int h, int d, Pointer data) throws joclException {
		
		this.device = device;

		this.img_format = img_format;

		this.img_desc = new cl_image_desc();
		if( d == 1 )
			this.img_desc.image_type   = CL_MEM_OBJECT_IMAGE2D;
		else
			this.img_desc.image_type   = CL_MEM_OBJECT_IMAGE3D;
		this.img_desc.image_width  = w;
		this.img_desc.image_height = h;
		this.img_desc.image_depth  = d;
		//img_desc.image_row_pitch   = 4*w;
		//img_desc.image_slice_pitch = 4*w*h;
		
		int[] ciErrNum = {CL_SUCCESS};
		this.memory = CL.clCreateImage( device.context, flags, img_format, img_desc, data, ciErrNum );
        if( ciErrNum[0] != CL_SUCCESS ) throw new joclException();
	}

	public void release( ){
		CL.clReleaseMemObject(memory);
		memory = null;
	}

	public void EnqueueWriteImage(boolean blocking, float[] img) throws joclException {
		if( CL.clEnqueueWriteImage( device.commandQueue, 
								memory,
				                blocking,
				                new long[]{0,0,0},
				                new long[]{img_desc.image_width,img_desc.image_height,img_desc.image_depth},
				                img_desc.image_row_pitch,
				                img_desc.image_slice_pitch,
				                Pointer.to(img),
				                0, null, null ) != CL.CL_SUCCESS ){
			throw new joclException();
		}
		
	}
	


}
