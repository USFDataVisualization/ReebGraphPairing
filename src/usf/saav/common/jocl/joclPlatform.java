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

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_PLATFORM_NAME;
import static org.jocl.CL.clGetDeviceIDs;

import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

public class joclPlatform {

	joclDevice [] devices;
    final long deviceType = CL_DEVICE_TYPE_ALL;
	private cl_platform_id platform_id;

	public joclPlatform(cl_platform_id platform_id, boolean profile) {
        
        this.platform_id = platform_id;

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform_id);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform_id, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        this.devices = new joclDevice[numDevices];
        
        // Obtain a device ID 
        cl_device_id _devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform_id, deviceType, numDevices, _devices, null);
        for(int i = 0; i < devices.length; i++){
        	devices[i] = new joclDevice( contextProperties, _devices[i], profile );
        }

	}

	public joclDevice getDevice(int i) {
		return devices[i];
	}
	
	public int getDeviceCount(){
		return devices.length;
	}
	
	
	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder( );
		
        String platformName = joclController.getString(platform_id, CL_PLATFORM_NAME);

        ret.append("Number of devices in platform "+platformName+": "+devices.length +"\n\n");

        for( joclDevice d : devices ){
        	ret.append( d.toString() );
        }
        return ret.toString();
        
	}
	
	public joclDevice selectAccelerator(){
		for(int d = 0; d < getDeviceCount(); d++){
			joclDevice device = getDevice(d);
			if( device == null ) continue;
			if( device.isAccelerator() ) return device;
		}
		return null;
	}

	public joclDevice selectGPU(){
		for(int d = 0; d < getDeviceCount(); d++){
			joclDevice device = getDevice(d);
			if( device == null ) continue;
			if( device.isGPU() ) return device;
		}
		return null;
	}
	
	public joclDevice selectCPU(){
		for(int d = 0; d < getDeviceCount(); d++){
			joclDevice device = getDevice(d);
			if( device == null ) continue;
			if( device.isCPU() ) return device;
		}
		return null;
	}
	
	public joclDevice selectBestDevice(){
		joclDevice device = selectAccelerator();
		if( device == null ) device = selectGPU();
		if( device == null ) device = selectCPU();
		return device;
	}

}
