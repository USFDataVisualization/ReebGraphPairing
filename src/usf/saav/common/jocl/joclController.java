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

import static org.jocl.CL.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jocl.*;

public class joclController {

	joclPlatform [] platforms;

	public joclController( boolean profile ){

		// Enable exceptions
		CL.setExceptionsEnabled(true);

		int numPlatformsArray[] = new int[1];
		clGetPlatformIDs(0, null, numPlatformsArray);
		int numPlatforms = numPlatformsArray[0];

		platforms = new joclPlatform[numPlatforms];
		cl_platform_id _platforms[] = new cl_platform_id[numPlatforms];
		clGetPlatformIDs(_platforms.length, _platforms, null);
		for(int i = 0; i < numPlatforms; i++){
			platforms[i] = new joclPlatform( _platforms[i], profile );
		}
		
	}

	public joclPlatform getPlatform(int i) {
		return platforms[i];
	}
	
	public int getPlatformCount(){
		return platforms.length;
	}

	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder();

		ret.append("Number of platforms: "+platforms.length+"\n");

		for( joclPlatform p : platforms ){
			ret.append(p.toString());
		}
		return ret.toString();

	}




	/**
	 * Returns the value of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @return The value
	 */
	public static int getInt(cl_device_id device, int paramName)
	{
		return getInts(device, paramName, 1)[0];
	}

	/**
	 * Returns the values of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @param numValues The number of values
	 * @return The value
	 */
	public static int[] getInts(cl_device_id device, int paramName, int numValues)
	{
		int values[] = new int[numValues];
		clGetDeviceInfo(device, paramName, Sizeof.cl_int * numValues, Pointer.to(values), null);
		return values;
	}

	/**
	 * Returns the value of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @return The value
	 */
	public static long getLong(cl_device_id device, int paramName)
	{
		return getLongs(device, paramName, 1)[0];
	}

	/**
	 * Returns the values of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @param numValues The number of values
	 * @return The value
	 */
	public static long[] getLongs(cl_device_id device, int paramName, int numValues)
	{
		long values[] = new long[numValues];
		clGetDeviceInfo(device, paramName, Sizeof.cl_long * numValues, Pointer.to(values), null);
		return values;
	}

	/**
	 * Returns the value of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @return The value
	 */
	public static String getString(cl_device_id device, int paramName)
	{
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		clGetDeviceInfo(device, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int)size[0]];
		clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length-1);
	}

	/**
	 * Returns the value of the platform info parameter with the given name
	 *
	 * @param platform The platform
	 * @param paramName The parameter name
	 * @return The value
	 */
	public static String getString(cl_platform_id platform, int paramName)
	{
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		clGetPlatformInfo(platform, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int)size[0]];
		clGetPlatformInfo(platform, paramName, buffer.length, Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length-1);
	}

	/**
	 * Returns the value of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @return The value
	 */
	public static long getSize(cl_device_id device, int paramName)
	{
		return getSizes(device, paramName, 1)[0];
	}

	/**
	 * Returns the values of the device info parameter with the given name
	 *
	 * @param device The device
	 * @param paramName The parameter name
	 * @param numValues The number of values
	 * @return The value
	 */
	public static long[] getSizes(cl_device_id device, int paramName, int numValues)
	{
		// The size of the returned data has to depend on 
		// the size of a size_t, which is handled here
		ByteBuffer buffer = ByteBuffer.allocate(
				numValues * Sizeof.size_t).order(ByteOrder.nativeOrder());
		clGetDeviceInfo(device, paramName, Sizeof.size_t * numValues, 
				Pointer.to(buffer), null);
		long values[] = new long[numValues];
		if (Sizeof.size_t == 4)
		{
			for (int i=0; i<numValues; i++)
			{
				values[i] = buffer.getInt(i * Sizeof.size_t);
			}
		}
		else
		{
			for (int i=0; i<numValues; i++)
			{
				values[i] = buffer.getLong(i * Sizeof.size_t);
			}
		}
		return values;
	}

	

	public static void main(String args[])
	{
		System.out.println(new joclController(true).toString());

	}

	
}


