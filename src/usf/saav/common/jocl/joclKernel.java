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

import static org.jocl.CL.CL_KERNEL_ARG_ACCESS_QUALIFIER;
import static org.jocl.CL.CL_KERNEL_ARG_ADDRESS_QUALIFIER;
import static org.jocl.CL.CL_KERNEL_ARG_NAME;
import static org.jocl.CL.CL_KERNEL_ARG_TYPE_NAME;
import static org.jocl.CL.CL_KERNEL_ARG_TYPE_QUALIFIER;
import static org.jocl.CL.CL_KERNEL_NUM_ARGS;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clGetKernelArgInfo;
import static org.jocl.CL.clGetKernelInfo;
import static org.jocl.CL.clSetKernelArg;

import java.util.Arrays;

import org.jocl.CL;
import org.jocl.CLException;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_event;
import org.jocl.cl_kernel;
import org.jocl.cl_program;
import org.jocl.cl_sampler;

public class joclKernel {

	private cl_kernel  kernel = null;
	private joclDevice device = null;
	private String     name;


	joclKernel( joclDevice device, String programSource, String kernel_main ) {
		init( device, programSource, kernel_main );
	}

	joclKernel( joclDevice device, joclLoader program, String kernel_main ){
		init( device, program.toString(), kernel_main );
	}

	
	joclKernel( joclDevice device, String [] programSource, String kernel_main ){
		StringBuilder newProgram = new StringBuilder();
		for(String s : programSource){
			newProgram.append( s + "\n" );
		}
		init( device, newProgram.toString(), kernel_main );
	}
		
	private void init(joclDevice device, String programSource, String kernel_main){
		//System.out.print(programSource);
		this.name = kernel_main;

		cl_program program = clCreateProgramWithSource(device.context, 1, new String[]{programSource}, null, null);
        
        // Build the program
		try{ 
			clBuildProgram(program, 0, null, null, null, null);
		} catch ( CLException e ){
			System.err.println("Compile error: " + kernel_main);
			e.printStackTrace();
			System.exit(0);
		}
        
        // Create the kernel
        this.device = device;
        this.kernel = clCreateKernel(program, kernel_main, null);
	}
	
	
	
	public void setKernelArg( int arg_index, float ... arg ) throws joclException{
		if( clSetKernelArg(kernel, arg_index, arg.length*4, Pointer.to(arg) ) != CL.CL_SUCCESS )
			throw new joclException( );
	}

	public void setKernelArg( int arg_index, int ... arg ) throws joclException{
		if( clSetKernelArg(kernel, arg_index, arg.length*4, Pointer.to(arg) ) != CL.CL_SUCCESS )
			throw new joclException( );
	}

	public void setKernelArg(int arg_index, cl_sampler sampler) throws joclException {
		if( clSetKernelArg(kernel, arg_index, Sizeof.cl_sampler, Pointer.to(sampler) ) != CL.CL_SUCCESS )
			throw new joclException( );
	}
	
	public void setKernelArg(int arg_index, joclMemory mem ) throws joclException {
		if( clSetKernelArg(kernel, arg_index, Sizeof.cl_mem, Pointer.to(mem.memory) ) != CL.CL_SUCCESS )
			throw new joclException( );
	}
	
	public void setKernelArg(int arg_index, joclImage img ) throws joclException {
		if( clSetKernelArg(kernel, arg_index, Sizeof.cl_mem, Pointer.to(img.memory) ) != CL.CL_SUCCESS )
			throw new joclException( );
	}
	
	public void setKernelArgs( KernelArg...args ) throws joclException {
		for( int i = 0; i < args.length; i++ ){
			if( clSetKernelArg(kernel, i, args[i].size, args[i].pntr ) != CL.CL_SUCCESS )
				throw new joclException( );
		}
	}
	
	public class KernelArg {
		Pointer pntr;
		long size;
		public KernelArg( float ... arg ) {
			size = arg.length*4;
			pntr = Pointer.to(arg);
		}

		public KernelArg( int ... arg ) {
			size = arg.length*4;
			pntr = Pointer.to(arg);
		}

		public KernelArg( cl_sampler sampler ) {
			size = Sizeof.cl_sampler;
			pntr = Pointer.to(sampler);
		}
		
		public KernelArg( joclMemory mem ) {
			size = Sizeof.cl_mem;
			pntr = Pointer.to(mem.memory);
		}
		
		public KernelArg( joclImage img ) {
			size = Sizeof.cl_mem;
			pntr = Pointer.to(img.memory);
		}
	}
	
	
	
	public joclEvent enqueueNDRangeKernel( long [] global_work_size, cl_event...waitEvents ) throws joclException{
		return enqueueNDRangeKernel( global_work_size, Arrays.copyOf(global_work_size, global_work_size.length), waitEvents );
	}

	public joclEvent enqueueNDRangeKernel( long [] global_work_size, long [] _local_work_size, cl_event...waitEvents ) throws joclException{
		long [] local_work_size = Arrays.copyOf(_local_work_size, _local_work_size.length);
		long [] dim_sizes = device.getMaxWorkDimensionSizes();
		long    max_size  = device.getMaxWorkSize();

		long total_size = 1;
		for( int i = 0; i < local_work_size.length; i++ ){
			local_work_size[i] = Math.min( local_work_size[i], dim_sizes[i]);
			total_size *= local_work_size[i];
		}

		int red_dim = 0;
		while( total_size > max_size ){
			local_work_size[ red_dim ] = Math.max( local_work_size[ red_dim ]/2, 1);
			red_dim = (red_dim+1)%local_work_size.length;
			total_size = 1;
			for( int i = 0; i < local_work_size.length; i++ ){
				total_size *= local_work_size[i];
			}
		}
		
		//System.out.println( "Global: " + Arrays.toString(global_work_size) + "  Local: " + Arrays.toString( local_work_size ) );
		cl_event event = new cl_event();
		if( waitEvents.length == 0 ){
	        if( clEnqueueNDRangeKernel( device.commandQueue, kernel, global_work_size.length, null, global_work_size, local_work_size, 0, null, event) != CL.CL_SUCCESS){
	        	throw new joclException();
	        }
		}
		else {
			if( clEnqueueNDRangeKernel( device.commandQueue, kernel, global_work_size.length, null, global_work_size, local_work_size, waitEvents.length, waitEvents, event) != CL.CL_SUCCESS){
	        	throw new joclException();
	        }
		}
		return new joclEvent(name,event);
	}


	
	@Override
	public String toString( ){
		StringBuilder ret = new StringBuilder();
		// Arrays that will store the parameter values
        int paramValueInt[] = { 0 };
        long paramValueLong[] = { 0 };
        long sizeArray[] = { 0 };
        byte paramValueCharArray[] = new byte[1024];
        
        // Obtain the number of arguments that the kernel has
        clGetKernelInfo(kernel, CL_KERNEL_NUM_ARGS,
            Sizeof.cl_uint, Pointer.to(paramValueInt), null);
        int numArgs = paramValueInt[0];

        // Obtain information about each argument
        for (int a=0; a<numArgs; a++)
        {
            // The argument name
            clGetKernelArgInfo(kernel, a, CL_KERNEL_ARG_NAME, 0, null, sizeArray);
            clGetKernelArgInfo(kernel, a, CL_KERNEL_ARG_NAME, sizeArray[0], Pointer.to(paramValueCharArray), null);
            String argName = new String(paramValueCharArray, 0, (int)sizeArray[0]-1);

            // The address qualifier (global/local/constant/private)
            clGetKernelArgInfo(kernel, a, CL_KERNEL_ARG_ADDRESS_QUALIFIER, Sizeof.cl_int, Pointer.to(paramValueInt), null);
            int addressQualifier = paramValueInt[0];

            // The access qualifier (readOnly/writeOnly/readWrite/none)
            clGetKernelArgInfo(kernel, a, CL_KERNEL_ARG_ACCESS_QUALIFIER, Sizeof.cl_int, Pointer.to(paramValueInt), null);
            int accessQualifier = paramValueInt[0];

            // The type qualifier bitfield (const/restrict/volatile/none)
            clGetKernelArgInfo(kernel, a, CL_KERNEL_ARG_TYPE_QUALIFIER, Sizeof.cl_long, Pointer.to(paramValueLong), null);
            long typeQualifier = paramValueLong[0];

            // The type name
            clGetKernelArgInfo(kernel, a, CL_KERNEL_ARG_TYPE_NAME, 0, null, sizeArray);
            clGetKernelArgInfo(kernel, a, CL_KERNEL_ARG_TYPE_NAME, sizeArray[0], Pointer.to(paramValueCharArray), null);
            String typeName = new String(paramValueCharArray, 0, (int)sizeArray[0]-1);

            // Print the results:
            ret.append("Argument "+a+":");
            ret.append("    Name: "+argName);
            ret.append("    Address qualifier: " + CL.stringFor_cl_kernel_arg_address_qualifier(addressQualifier));
            ret.append("    Access qualifier : " + CL.stringFor_cl_kernel_arg_access_qualifier(accessQualifier));
            ret.append("    Type qualifier   : " + CL.stringFor_cl_kernel_arg_type_qualifer(typeQualifier));
            ret.append("    Type name        : " + typeName);
        }
        
        return ret.toString();
	}



	
	
}
