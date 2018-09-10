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

import static org.jocl.CL.CL_DEVICE_ADDRESS_BITS;
import static org.jocl.CL.CL_DEVICE_ERROR_CORRECTION_SUPPORT;
import static org.jocl.CL.CL_DEVICE_GLOBAL_MEM_SIZE;
import static org.jocl.CL.CL_DEVICE_IMAGE2D_MAX_HEIGHT;
import static org.jocl.CL.CL_DEVICE_IMAGE2D_MAX_WIDTH;
import static org.jocl.CL.CL_DEVICE_IMAGE3D_MAX_DEPTH;
import static org.jocl.CL.CL_DEVICE_IMAGE3D_MAX_HEIGHT;
import static org.jocl.CL.CL_DEVICE_IMAGE3D_MAX_WIDTH;
import static org.jocl.CL.CL_DEVICE_IMAGE_SUPPORT;
import static org.jocl.CL.CL_DEVICE_LOCAL_MEM_SIZE;
import static org.jocl.CL.CL_DEVICE_LOCAL_MEM_TYPE;
import static org.jocl.CL.CL_DEVICE_MAX_CLOCK_FREQUENCY;
import static org.jocl.CL.CL_DEVICE_MAX_COMPUTE_UNITS;
import static org.jocl.CL.CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_MEM_ALLOC_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_READ_IMAGE_ARGS;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_GROUP_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_ITEM_SIZES;
import static org.jocl.CL.CL_DEVICE_MAX_WRITE_IMAGE_ARGS;
import static org.jocl.CL.CL_DEVICE_NAME;
import static org.jocl.CL.CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR;
import static org.jocl.CL.CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE;
import static org.jocl.CL.CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT;
import static org.jocl.CL.CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT;
import static org.jocl.CL.CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG;
import static org.jocl.CL.CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT;
import static org.jocl.CL.CL_DEVICE_QUEUE_PROPERTIES;
import static org.jocl.CL.CL_DEVICE_SINGLE_FP_CONFIG;
import static org.jocl.CL.CL_DEVICE_TYPE;
import static org.jocl.CL.CL_DEVICE_TYPE_ACCELERATOR;
import static org.jocl.CL.CL_DEVICE_TYPE_CPU;
import static org.jocl.CL.CL_DEVICE_TYPE_DEFAULT;
import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.CL_DEVICE_VENDOR;
import static org.jocl.CL.CL_DRIVER_VERSION;
import static org.jocl.CL.CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE;
import static org.jocl.CL.CL_QUEUE_PROFILING_ENABLE;
import static org.jocl.CL.CL_SUCCESS;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateSampler;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.stringFor_cl_device_fp_config;

import java.io.IOException;

import org.jocl.Pointer;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_image_format;
import org.jocl.cl_sampler;

public class joclDevice {

	cl_command_queue commandQueue;
	cl_context       context;
	cl_device_id     device_id;

	public joclDevice( cl_context_properties contextProperties, cl_device_id device, boolean profile) {
        this.device_id = device;
        
        // Create a context for the selected device
        context = clCreateContext( contextProperties, 1, new cl_device_id[]{device}, null, null, null);
        
        // Create a command-queue for the selected device
        long properties = 0;
        if( profile ) properties |= CL_QUEUE_PROFILING_ENABLE;
       	commandQueue = clCreateCommandQueue(context, device, properties, null);
		
	}
	
	public void release( ){
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);		
	}


	
	public joclKernel buildProgram( String [] program, String main_func ){
		return new joclKernel( this, program, main_func );
	}
	
	public joclKernel buildProgram( joclLoader program, String main_func ) throws IOException {
		return new joclKernel( this, program, main_func );
	}
	
	

	public joclMemory createBuffer( String name, long flags, long size, Pointer host_data, int [] err_codes ){
		return new joclMemory( this, name, flags, size, host_data, err_codes );
	}

	public joclMemory createBuffer( String name, long flags, long size ){
		return new joclMemory( this, name, flags, size, null, null );
	}
	
	public joclMemory createBuffer( String name, long flags, float[] host_data ){
		return new joclMemory( this, name, flags, host_data.length*4, Pointer.to(host_data), null );
	}

	public joclMemory createBuffer( String name, long flags, byte[] host_data ) {
		return new joclMemory( this, name, flags, host_data.length, Pointer.to(host_data), null );
	}
	
	public joclImage createImage3D(long flags, cl_image_format img_format, int w, int h, int d, float[] data ) throws joclException{
		return new joclImage( this, flags, img_format, w, h, d, Pointer.to(data) );
	}

	public joclImage createImage2D(long flags, cl_image_format img_format, int w, int h, float [] data ) throws joclException{
		return new joclImage( this, flags, img_format, w, h, Pointer.to(data) );
	}
	
	public cl_sampler createSampler( boolean normalized_coords, int addressing_mode, int filter_mode ) throws joclException {
		int [] ciErrNum = {CL_SUCCESS};
		cl_sampler ret = clCreateSampler( context, normalized_coords, addressing_mode, filter_mode, ciErrNum );
		if( ciErrNum[0] != CL_SUCCESS ) throw new joclException();
		return ret;
	}

	
	
	public boolean isCPU(){
        long deviceType = joclController.getLong(device_id, CL_DEVICE_TYPE);
        return ( (deviceType & CL_DEVICE_TYPE_CPU) != 0);
	}
	public boolean isGPU(){
        long deviceType = joclController.getLong(device_id, CL_DEVICE_TYPE);
        return ( (deviceType & CL_DEVICE_TYPE_GPU) != 0);
	}
	
	public boolean isAccelerator(){
        long deviceType = joclController.getLong(device_id, CL_DEVICE_TYPE);
        return ( (deviceType & CL_DEVICE_TYPE_ACCELERATOR) != 0);
	}
	
	
	
	public long [] getMaxWorkDimensionSizes() {
        return joclController.getSizes(device_id, CL_DEVICE_MAX_WORK_ITEM_SIZES, 3);
	}

	public long getMaxWorkSize() {
		return joclController.getSize(device_id, CL_DEVICE_MAX_WORK_GROUP_SIZE);
	}

	public long getMaxWorkGroupSize() {
		return joclController.getSize(device_id, CL_DEVICE_MAX_WORK_GROUP_SIZE);
	}



	@Override
	public String toString(){
		
		StringBuilder ret = new StringBuilder();
	
        // CL_DEVICE_NAME
        String deviceName = joclController.getString(device_id, CL_DEVICE_NAME);
        ret.append("--- Info for device "+deviceName+": ---\n");
        ret.append("CL_DEVICE_NAME: \t\t\t"+deviceName+"\n");

        // CL_DEVICE_VENDOR
        String deviceVendor = joclController.getString(device_id, CL_DEVICE_VENDOR);
        ret.append("CL_DEVICE_VENDOR: \t\t\t"+deviceVendor+"\n");

        // CL_DRIVER_VERSION
        String driverVersion = joclController.getString(device_id, CL_DRIVER_VERSION);
        ret.append("CL_DRIVER_VERSION: \t\t\t"+driverVersion+"\n");

        // CL_DEVICE_TYPE
        long deviceType = joclController.getLong(device_id, CL_DEVICE_TYPE);
        if( (deviceType & CL_DEVICE_TYPE_CPU) != 0)
            ret.append("CL_DEVICE_TYPE:\t\t\t\t"+"CL_DEVICE_TYPE_CPU"+"\n");
        if( (deviceType & CL_DEVICE_TYPE_GPU) != 0)
            ret.append("CL_DEVICE_TYPE:\t\t\t\t"+"CL_DEVICE_TYPE_GPU"+"\n");
        if( (deviceType & CL_DEVICE_TYPE_ACCELERATOR) != 0)
            ret.append("CL_DEVICE_TYPE:\t\t\t\t"+"CL_DEVICE_TYPE_ACCELERATOR"+"\n");
        if( (deviceType & CL_DEVICE_TYPE_DEFAULT) != 0)
            ret.append("CL_DEVICE_TYPE:\t\t\t\t"+"CL_DEVICE_TYPE_DEFAULT"+"\n");

        // CL_DEVICE_MAX_COMPUTE_UNITS
        int maxComputeUnits = joclController.getInt(device_id, CL_DEVICE_MAX_COMPUTE_UNITS);
        ret.append("CL_DEVICE_MAX_COMPUTE_UNITS:\t\t"+maxComputeUnits+"\n");

        // CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS
        long maxWorkItemDimensions = joclController.getLong(device_id, CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
        ret.append("CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS:\t"+maxWorkItemDimensions+"\n");

        // CL_DEVICE_MAX_WORK_ITEM_SIZES
        long maxWorkItemSizes[] = joclController.getSizes(device_id, CL_DEVICE_MAX_WORK_ITEM_SIZES, 3);
        ret.append("CL_DEVICE_MAX_WORK_ITEM_SIZES:\t\t"+maxWorkItemSizes[0]+" / "+maxWorkItemSizes[1]+" / "+maxWorkItemSizes[2]+" \n");

        // CL_DEVICE_MAX_WORK_GROUP_SIZE
        long maxWorkGroupSize = joclController.getSize(device_id, CL_DEVICE_MAX_WORK_GROUP_SIZE);
        ret.append("CL_DEVICE_MAX_WORK_GROUP_SIZE:\t\t"+maxWorkGroupSize+"\n");

        // CL_DEVICE_MAX_CLOCK_FREQUENCY
        long maxClockFrequency = joclController.getLong(device_id, CL_DEVICE_MAX_CLOCK_FREQUENCY);
        ret.append("CL_DEVICE_MAX_CLOCK_FREQUENCY:\t\t"+maxClockFrequency+" MHz\n");

        // CL_DEVICE_ADDRESS_BITS
        int addressBits = joclController.getInt(device_id, CL_DEVICE_ADDRESS_BITS);
        ret.append("CL_DEVICE_ADDRESS_BITS:\t\t\t"+addressBits+"\n");

        // CL_DEVICE_MAX_MEM_ALLOC_SIZE
        long maxMemAllocSize = joclController.getLong(device_id, CL_DEVICE_MAX_MEM_ALLOC_SIZE);
        ret.append("CL_DEVICE_MAX_MEM_ALLOC_SIZE:\t\t"+(int)(maxMemAllocSize / (1024 * 1024))+" MByte\n");

        // CL_DEVICE_GLOBAL_MEM_SIZE
        long globalMemSize = joclController.getLong(device_id, CL_DEVICE_GLOBAL_MEM_SIZE);
        ret.append("CL_DEVICE_GLOBAL_MEM_SIZE:\t\t"+(int)(globalMemSize / (1024 * 1024))+" MByte\n");

        // CL_DEVICE_ERROR_CORRECTION_SUPPORT
        int errorCorrectionSupport = joclController.getInt(device_id, CL_DEVICE_ERROR_CORRECTION_SUPPORT);
        ret.append("CL_DEVICE_ERROR_CORRECTION_SUPPORT:\t"+(errorCorrectionSupport != 0 ? "yes" : "no")+"\n");

        // CL_DEVICE_LOCAL_MEM_TYPE
        int localMemType = joclController.getInt(device_id, CL_DEVICE_LOCAL_MEM_TYPE);
        ret.append("CL_DEVICE_LOCAL_MEM_TYPE:\t\t"+(localMemType == 1 ? "local" : "global")+"\n");

        // CL_DEVICE_LOCAL_MEM_SIZE
        long localMemSize = joclController.getLong(device_id, CL_DEVICE_LOCAL_MEM_SIZE);
        ret.append("CL_DEVICE_LOCAL_MEM_SIZE:\t\t"+(int)(localMemSize / 1024)+" KByte\n");

        // CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE
        long maxConstantBufferSize = joclController.getLong(device_id, CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE);
        ret.append("CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE:\t"+(int)(maxConstantBufferSize / 1024)+" KByte\n");

        // CL_DEVICE_QUEUE_PROPERTIES
        long queueProperties = joclController.getLong(device_id, CL_DEVICE_QUEUE_PROPERTIES);
        if(( queueProperties & CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE ) != 0)
            ret.append("CL_DEVICE_QUEUE_PROPERTIES:\t\t"+"CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE"+"\n");
        if(( queueProperties & CL_QUEUE_PROFILING_ENABLE ) != 0)
            ret.append("CL_DEVICE_QUEUE_PROPERTIES:\t\t"+"CL_QUEUE_PROFILING_ENABLE"+"\n");

        // CL_DEVICE_IMAGE_SUPPORT
        int imageSupport = joclController.getInt(device_id, CL_DEVICE_IMAGE_SUPPORT);
        ret.append("CL_DEVICE_IMAGE_SUPPORT:\t\t"+imageSupport+"\n");

        // CL_DEVICE_MAX_READ_IMAGE_ARGS
        int maxReadImageArgs = joclController.getInt(device_id, CL_DEVICE_MAX_READ_IMAGE_ARGS);
        ret.append("CL_DEVICE_MAX_READ_IMAGE_ARGS:\t\t"+maxReadImageArgs+"\n");

        // CL_DEVICE_MAX_WRITE_IMAGE_ARGS
        int maxWriteImageArgs = joclController.getInt(device_id, CL_DEVICE_MAX_WRITE_IMAGE_ARGS);
        ret.append("CL_DEVICE_MAX_WRITE_IMAGE_ARGS:\t\t"+maxWriteImageArgs+"\n");

        // CL_DEVICE_SINGLE_FP_CONFIG
        long singleFpConfig = joclController.getLong(device_id, CL_DEVICE_SINGLE_FP_CONFIG);
        ret.append("CL_DEVICE_SINGLE_FP_CONFIG:\t\t"+stringFor_cl_device_fp_config(singleFpConfig)+"\n");

        // CL_DEVICE_IMAGE2D_MAX_WIDTH
        long image2dMaxWidth = joclController.getSize(device_id, CL_DEVICE_IMAGE2D_MAX_WIDTH);
        ret.append("CL_DEVICE_2D_MAX_WIDTH\t\t\t"+image2dMaxWidth+"\n");

        // CL_DEVICE_IMAGE2D_MAX_HEIGHT
        long image2dMaxHeight = joclController.getSize(device_id, CL_DEVICE_IMAGE2D_MAX_HEIGHT);
        ret.append("CL_DEVICE_2D_MAX_HEIGHT\t\t\t"+image2dMaxHeight+"\n");

        // CL_DEVICE_IMAGE3D_MAX_WIDTH
        long image3dMaxWidth = joclController.getSize(device_id, CL_DEVICE_IMAGE3D_MAX_WIDTH);
        ret.append("CL_DEVICE_3D_MAX_WIDTH\t\t\t"+image3dMaxWidth+"\n");

        // CL_DEVICE_IMAGE3D_MAX_HEIGHT
        long image3dMaxHeight = joclController.getSize(device_id, CL_DEVICE_IMAGE3D_MAX_HEIGHT);
        ret.append("CL_DEVICE_3D_MAX_HEIGHT\t\t\t"+image3dMaxHeight+"\n");

        // CL_DEVICE_IMAGE3D_MAX_DEPTH
        long image3dMaxDepth = joclController.getSize(device_id, CL_DEVICE_IMAGE3D_MAX_DEPTH);
        ret.append("CL_DEVICE_3D_MAX_DEPTH\t\t\t"+image3dMaxDepth+"\n");

        // CL_DEVICE_PREFERRED_VECTOR_WIDTH_<type>
        ret.append("CL_DEVICE_PREFERRED_VECTOR_WIDTH_<t>\t");
        int preferredVectorWidthChar = joclController.getInt(device_id, CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR);
        int preferredVectorWidthShort = joclController.getInt(device_id, CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT);
        int preferredVectorWidthInt = joclController.getInt(device_id, CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT);
        int preferredVectorWidthLong = joclController.getInt(device_id, CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG);
        int preferredVectorWidthFloat = joclController.getInt(device_id, CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT);
        int preferredVectorWidthDouble = joclController.getInt(device_id, CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE);
        ret.append("CHAR "+preferredVectorWidthChar+", SHORT "+preferredVectorWidthShort+", INT "+preferredVectorWidthInt+
        		", LONG "+preferredVectorWidthLong+", FLOAT "+preferredVectorWidthFloat+", DOUBLE "+preferredVectorWidthDouble+"\n\n\n");
        
        return ret.toString();
	}


	

}
