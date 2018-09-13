/*
 *     jPSimp - Persistence calculation and simplification of scalar fields.
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
package usf.saav.topology.merge;

import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Vector;

import usf.saav.common.EventTimer;
import usf.saav.common.algorithm.ArrayDisjointSet;
import usf.saav.common.jocl.joclDevice;
import usf.saav.common.jocl.joclEvent;
import usf.saav.common.jocl.joclException;
import usf.saav.common.jocl.joclKernel;
import usf.saav.common.jocl.joclMemory;
import usf.saav.common.jocl.joclResourceLoader;
import usf.saav.scalarfield.ScalarField2D;
import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoTreeNode;

public abstract class PAugmentedMergeTree  extends AbstractAugmentedMergeTree {

	private joclDevice   device;

	//private joclKernel kernel_djs_init;
	//private joclKernel kernel_djs_simplify;
	private joclKernel kernel_djs;
	private joclKernel kernel_cps_extract;
	private joclKernel kernel_cps_bin;
	private joclKernel kernel_cps_bin_sort;
	private joclKernel kernel_cps_propagate;

	private joclMemory d_field;
	private joclMemory d_djs;
	private joclMemory d_cps;
	private joclMemory d_scratch;
	private joclMemory d_histogram;
	
	ScalarField2D sf = null;
	
	Vector<EventTimer> events = new Vector<EventTimer>();

	int histogramBinN = 128;

	boolean verbose = false;
	
	boolean operationComplete = false;
	
	
	public PAugmentedMergeTree( joclDevice _device ){
		this(_device,false);
	}

	public PAugmentedMergeTree( joclDevice _device, boolean _verbose ){
		//super(verbose);
		
		verbose = _verbose;
		
		this.device = _device;

		String dir = "/usf/saav/topology/join/parallel";
		
		try {
				
			kernel_djs   		= device.buildProgram( new joclResourceLoader(dir,"kernel_djs.cl"), 		    "kernel_djs"     );
			kernel_cps_bin		= device.buildProgram( new joclResourceLoader(dir,"kernel_cps_bucket_sort.cl"),	"kernel_cps_bucket" );
			kernel_cps_bin_sort	= device.buildProgram( new joclResourceLoader(dir,"kernel_cps_bucket_sort.cl"),	"kernel_cps_bucket_sort" );
			
			if( device.getMaxWorkGroupSize() == 256 ){
				kernel_cps_extract		= device.buildProgram( new joclResourceLoader(dir,"kernel_cps_extract.cl"),		"kernel_cps_extract_256" );
				kernel_cps_propagate	= device.buildProgram( new joclResourceLoader(dir,"kernel_cps_propagate.cl"),	"kernel_cps_propagate_256" );
			}
			else if( device.getMaxWorkGroupSize() == 1024 ){
				kernel_cps_extract		= device.buildProgram( new joclResourceLoader(dir,"kernel_cps_extract.cl"),		"kernel_cps_extract_1024" );
				kernel_cps_propagate	= device.buildProgram( new joclResourceLoader(dir,"kernel_cps_propagate.cl"),	"kernel_cps_propagate_1024" );
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void release( ){
		 
		if( sf != null ){
			d_field.release();
			d_djs.release();
			d_cps.release();
			d_scratch.release();
			d_histogram.release();
		}		
		sf = null;
		
	}
		

	public void writeDJSInfo( PrintStream ps, boolean showIdx ){
		try {
			ByteBuffer tmp_djs = ByteBuffer.allocate( (int)sf.getSize()*4 );
			tmp_djs.order( ByteOrder.LITTLE_ENDIAN );
			d_djs.enqueueReadBuffer( true, tmp_djs );
			ps.println( sf.getWidth() + " " + sf.getHeight() );
			for( int i = 0; i < sf.getSize(); i++ ){
				if( showIdx ) ps.print( "["+i+"]"+tmp_djs.getInt() + " " );
				else ps.print( tmp_djs.getInt() + " " );
				if( (i%sf.getWidth()) == (sf.getWidth()-1) ) ps.println();
			}
		} catch (joclException e) {
			e.printStackTrace();
		}
	}
	
	class HashDJSRecord {
		int key;
		int value;
		float data;
		int next;
	}

	public void writeDJSHashInfo( PrintStream ps ){
		try {
			ByteBuffer tmp_djs = ByteBuffer.allocate( (int)d_djs.size() );
			tmp_djs.order( ByteOrder.LITTLE_ENDIAN );
			d_djs.enqueueReadBuffer( true, tmp_djs );
			
			int heapp = tmp_djs.getInt();
			System.out.println("heap: " + heapp);
	
			int [] pntr = new int[2048];
			for(int i = 0; i < pntr.length; i++){
				pntr[i] = tmp_djs.getInt();
			}
			
			HashDJSRecord [] rec = new HashDJSRecord[heapp]; 
			for(int i = 0; i < rec.length; i++){
				rec[i] = new HashDJSRecord();
				rec[i].key = tmp_djs.getInt();
				rec[i].value = tmp_djs.getInt();
				rec[i].data = tmp_djs.getFloat();
				rec[i].next = tmp_djs.getInt();
			}

			for(int i = 0; i < pntr.length; i++ ){
				int cur = pntr[i];
				if( cur != -1 ){
					System.out.print("  [" + i + "] ");
					while(cur != -1){
						System.out.print( "[" + rec[cur].key + "->" + rec[cur].value + " (" + rec[cur].data + ")] " );
						cur = rec[cur].next;
					}
					System.out.println();
				}
			}

		} catch (joclException e) {
			e.printStackTrace();
		}
	}

	public void writeCPInfo( PrintStream ps ){
		
		ByteBuffer tmp_cps = ByteBuffer.allocate( (int)d_cps.size() );
		tmp_cps.order( ByteOrder.LITTLE_ENDIAN );
		
		try {
			d_cps.enqueueReadBuffer(true, tmp_cps);
		} catch (joclException e) {
			e.printStackTrace();
		}

		CPSTransfer currCP = new CPSTransfer( );

		int hpointer = tmp_cps.getInt();
		int cpsN = (hpointer-1)/CPSTransfer.size();
	
		int disp = 0;
		ps.println("heap pointer: " + hpointer + " (" + cpsN + " cps)");
		for(int i = 0; i < sf.getSize() && i < cpsN*2; i++){
			currCP.loadNext(tmp_cps);
			//if( currCP.ref > 1 ){
			System.out.print("("+disp+")");
			currCP.print( ps );
			disp++;
			//if(disp>50) break;
			//}
		}
		ps.println();
		
	}
	
	class HashRecord {
		int value;
		int data;
		int next;
	}
	
	public void writeScratchInfo( PrintStream ps, boolean showLocks ){
		ByteBuffer tmp_scratch = ByteBuffer.allocate( (int)d_scratch.size() );
		tmp_scratch.order( ByteOrder.LITTLE_ENDIAN );

		try {
			d_scratch.enqueueReadBuffer(true, tmp_scratch);
		} catch (joclException e) {
			e.printStackTrace();
		}

		for( int loop = 0; loop < 2; loop++ ){
			tmp_scratch.position(loop*2048*4);
			int heapp = tmp_scratch.getInt();
			System.out.println("heap: " + heapp);
	
			int [] pntr = new int[64];
			for(int i = 0; i < pntr.length; i++){
				pntr[i] = tmp_scratch.getInt();
			}
			
			HashRecord [] rec = new HashRecord[heapp]; 
			for(int i = 0; i < rec.length; i++){
				rec[i] = new HashRecord();
				rec[i].value = tmp_scratch.getInt();
				rec[i].data = tmp_scratch.getInt();
				rec[i].next = tmp_scratch.getInt();
			}
			for(int i = 0; i < pntr.length; i++ ){
				System.out.print("  [" + i + "] ");
				int cur = pntr[i];
				while(cur != -1){
					//System.out.print( cur + "  ");
					System.out.print( "(" + rec[cur].value + "->" + rec[cur].data + ") " );
					cur = rec[cur].next;
				}
				
				System.out.println();
			}
		}		
		
		/*
			for(int i = 0; i < sf.getSize(); i++){
				int val = tmp_scratch.getInt();
				if( showLocks ){
					if( val >= sf.getSize() ) ;// ps.print("["+i+"]-- ");
					else ps.print( "["+i+"]" + val + " " );
					//if( (i%sf.getWidth()) == (sf.getWidth()-1) ){
					//	ps.println();
					//}
				}
			}
			ps.println();
			*/
			/*
			int count = tmp_scratch.getInt();
			ps.println( count );
			int iter = tmp_scratch.getInt();
			ps.println( iter );
			
		
			for( int i = sf.getSize()+2, j =0; i < d_scratch.size()/4 && j < iter; i++, j++ ){
				int off = tmp_scratch.getInt();
				int cnt = tmp_scratch.getInt();
				ps.print( "["+j+"]" + off + "/" + cnt + " " );
				if( (j%10) == 9 ) ps.println();
			}
			ps.println();
			*/
	}
	

	public void writeStats( PrintStream ps ){
		for( EventTimer e : events){
			ps.println("\t" + e.getName() + " execution time in milliseconds = " + e.getElapsedTimeMilliseconds() + " ms" );
		}
		ps.println("\tsaddles processed by kernel: " + processed );
		ps.println("\tsaddles invaldiated by kernel: " + invalid );
		ps.println("\tsaddles unprocessed by kernel: " + unprocessed );
		ps.println("\tnodes in tree: " + total );
	}

	public void writeStatsCSVHeader( PrintStream ps ){
		for( EventTimer e : events){
			ps.print( e.getName() + ", " );
		}
		ps.print("saddles_processed, ");
		ps.print("saddles_invaldiated, ");
		ps.print("saddles_unprocessed, ");
		ps.println("tree_size");
	}
	
	public void writeStatsCSV( PrintStream ps ){
		for( EventTimer e : events){
			ps.print(e.getElapsedTimeMilliseconds() + ", ");
		}
		ps.print( processed + ", " );
		ps.print( invalid + ", " );
		ps.print( unprocessed + ", " );
		ps.println( total );
	}

  public abstract void calculate( ScalarField2D _sf ) ;
  

	protected void calculate( ScalarField2D _sf, boolean invert ){

		events.clear();
		
		if( sf != null && sf.getSize() < _sf.getSize() ){
			d_field.release();
			d_djs.release();
			d_cps.release();
			d_scratch.release();
			d_histogram.release();
			sf = null;
		}
		
		if( sf == null ){
			d_field     = device.createBuffer( "field",     CL_MEM_READ_ONLY,  4*_sf.getSize() );
			d_djs       = device.createBuffer( "djs",       CL_MEM_READ_WRITE, Math.max(4*_sf.getSize(), 4*(1+2048+2048*4) ) );
			d_cps       = device.createBuffer( "cps",       CL_MEM_READ_WRITE, 4*_sf.getSize()*CPSTransfer.size() );
			d_scratch   = device.createBuffer( "scratch",   CL_MEM_READ_WRITE, 4*_sf.getSize() );
			d_histogram = device.createBuffer( "histogram", CL_MEM_READ_WRITE, 4* (1 + 2 + histogramBinN + histogramBinN) ); // 1 for bin count, 2 for min/max, N for bins, N for bin offsets
		}
		
		sf = _sf;

		EventTimer.Default complete_time = new EventTimer.Default("start_to_finish");
		events.add(complete_time);
		complete_time.start();
		
		try {
			
			float [] data = new float[sf.getSize()];
			float minV =  Float.MAX_VALUE;
			float maxV = -Float.MAX_VALUE;
			if( invert ){
				for(int i = 0; i < data.length; i++){
					data[i] = -sf.getValue(i);
					minV = Math.min(minV, data[i]);
					maxV = Math.max(maxV, data[i]);
				}
			}
			else{
				for(int i = 0; i < data.length; i++){
					data[i] = sf.getValue(i);
					minV = Math.min(minV, data[i]);
					maxV = Math.max(maxV, data[i]);
				}
			}
			joclEvent event_field_write   = d_field.enqueueWriteBuffer(false, data);
			events.add(event_field_write);
			
			joclEvent event_djs_write     = d_djs.enqueueFillBuffer( new byte[]{0} );
			events.add(event_djs_write);
			
			joclEvent event_cps_write     = d_cps.enqueueFillBuffer( new byte[]{0} );
			events.add(event_cps_write);

			joclEvent event_scratch_write = d_scratch.enqueueFillBuffer( new byte[]{0} );
			events.add(event_scratch_write);

			

			ByteBuffer histogram = ByteBuffer.allocate(4*(1+2+histogramBinN*2));
			histogram.order( ByteOrder.LITTLE_ENDIAN );
			histogram.putInt( histogramBinN );
			histogram.putFloat( minV );
			histogram.putFloat( maxV );
			for(int i = 0; i < histogramBinN; i++ ){
				histogram.putInt( 0 );
				histogram.putInt( 0 );
			}
			joclEvent event_histogram_write = d_histogram.enqueueWriteBuffer( false, histogram );
			events.add(event_histogram_write);

			
			int arg = 0;
			
			/*
			arg = 0;
			kernel_djs_init.setKernelArg( arg++, sf.getWidth() ); 
			kernel_djs_init.setKernelArg( arg++, sf.getHeight() );
			kernel_djs_init.setKernelArg( arg++, d_field );
			kernel_djs_init.setKernelArg( arg++, d_djs );
			joclEvent event_init_djs = kernel_djs_init.enqueueNDRangeKernel( new long[]{sf.getWidth(),sf.getHeight()}, event_histogram_write.event, event_field_write.event, event_djs_write.event );
			events.add(event_init_djs);
			
			
			arg = 0;
			kernel_djs_simplify.setKernelArg( arg++, sf.getWidth() );
			kernel_djs_simplify.setKernelArg( arg++, sf.getHeight() );
			kernel_djs_simplify.setKernelArg( arg++, d_djs );
			joclEvent event_simplify_djs = kernel_djs_simplify.enqueueNDRangeKernel( new long[]{sf.getWidth(),sf.getHeight()}, event_init_djs.event );
			events.add(event_simplify_djs);
			 */

			kernel_djs.setKernelArg( 0, sf.getWidth() ); 
			kernel_djs.setKernelArg( 1, sf.getHeight() );
			kernel_djs.setKernelArg( 2, d_field );
			kernel_djs.setKernelArg( 3, d_djs );
			kernel_djs.setKernelArg( 4, (int)1 );
			joclEvent event_init_djs = kernel_djs.enqueueNDRangeKernel( new long[]{sf.getWidth(),sf.getHeight()}, event_histogram_write.event, event_field_write.event, event_djs_write.event );
			events.add(event_init_djs);

			kernel_djs.setKernelArg( 4, (int)2 );
			joclEvent event_simplify_djs = kernel_djs.enqueueNDRangeKernel( new long[]{sf.getWidth(),sf.getHeight()}, event_init_djs.event );
			events.add(event_simplify_djs);

			arg = 0;
			kernel_cps_extract.setKernelArg( arg++, sf.getWidth() );
			kernel_cps_extract.setKernelArg( arg++, sf.getHeight() );
			kernel_cps_extract.setKernelArg( arg++, d_field );
			kernel_cps_extract.setKernelArg( arg++, d_djs );
			kernel_cps_extract.setKernelArg( arg++, d_cps );
			kernel_cps_extract.setKernelArg( arg++, d_histogram );
			joclEvent event_extract_cps = kernel_cps_extract.enqueueNDRangeKernel( new long[]{sf.getWidth(),sf.getHeight()}, event_cps_write.event, event_simplify_djs.event );
			events.add(event_extract_cps);
			
			
						arg = 0;
			kernel_cps_bin.setKernelArg( arg++, d_cps );
			kernel_cps_bin.setKernelArg( arg++, d_histogram );
			joclEvent event_cps_bucket = kernel_cps_bin.enqueueNDRangeKernel( new long[]{sf.getWidth()*sf.getHeight()}, event_extract_cps.event );
			events.add(event_cps_bucket);

			
			arg = 0;
			kernel_cps_bin_sort.setKernelArg( arg++, d_cps );
			kernel_cps_bin_sort.setKernelArg( arg++, d_histogram );
			joclEvent event_cps_bucket_sort = kernel_cps_bin_sort.enqueueNDRangeKernel( new long[]{device.getMaxWorkGroupSize()*histogramBinN}, new long[]{device.getMaxWorkGroupSize()}, event_cps_bucket.event );
			events.add(event_cps_bucket_sort);



			ByteBuffer tmp_cpsN = ByteBuffer.allocate( 4 );
			tmp_cpsN.order( ByteOrder.LITTLE_ENDIAN );
			joclEvent event_cpsN_read = d_cps.enqueueReadBuffer(true, tmp_cpsN, event_cps_bucket_sort.event );
			events.add(event_cpsN_read);

			int hpointer = tmp_cpsN.getInt();
			int cpsN = (hpointer-1)/CPSTransfer.size();

			System.out.println( "CPS=" + cpsN );
			kernel_cps_propagate.setKernelArg( 0, d_cps );
			kernel_cps_propagate.setKernelArg( 1, d_djs );
			kernel_cps_propagate.setKernelArg( 3, d_scratch );
			kernel_cps_propagate.setKernelArg( 4, (int)0 );
			
			long wgs = device.getMaxWorkGroupSize();
			long cpsWork = (cpsN+wgs-1) - ( (cpsN+wgs-1)%wgs ); 
			joclEvent lastEvent = event_cpsN_read;
			

			joclEvent event_cps_propagate_clear_djs = d_djs.enqueueFillBuffer( Integer.MAX_VALUE, lastEvent.event );
			events.add(event_cps_propagate_clear_djs);
			lastEvent = event_cps_propagate_clear_djs;

			joclEvent event_cps_propagate_clear_scratch = d_scratch.enqueueFillBuffer( new int[]{0,Integer.MAX_VALUE}, lastEvent.event  );
			events.add(event_cps_propagate_clear_scratch);
			lastEvent = event_cps_propagate_clear_scratch;

			int [] res = {0,0};
			int cpsOffset = 0;
			int phase;
			EventTimer.CombinedEvents prop_event = new EventTimer.CombinedEvents( "event_cps_propagate" );
			events.add(prop_event);
			for( phase = 1; phase <= 40; phase++ ){
				kernel_cps_propagate.setKernelArg( 2, phase );
				kernel_cps_propagate.setKernelArg( 4, cpsOffset );

				joclEvent event_cps_propagate_phase_X = kernel_cps_propagate.enqueueNDRangeKernel( new long[]{cpsWork-cpsOffset}, lastEvent.event );
				prop_event.add(event_cps_propagate_phase_X);
				lastEvent = event_cps_propagate_phase_X;
				
				if( phase > 1 ){
					d_scratch.enqueueReadBuffer(true, (phase-1)*8, res, lastEvent.event );
					cpsOffset = (int) (res[1]-(res[1]%wgs));
					System.out.println( "Phase:" + (phase-1) + " {modified:" + res[0] + ", minimium:" + res[1] + "} Offset:" + cpsOffset + " Work Remaining:" + (cpsWork-cpsOffset) );
					if( res[1] > cpsN ) break;
				}
			}
			
			
			
			//ByteBuffer tmp_cps = ByteBuffer.allocate( (int) d_cps.size() );
			ByteBuffer tmp_cps = ByteBuffer.allocate( hpointer*4 );
			tmp_cps.order( ByteOrder.LITTLE_ENDIAN );
			joclEvent event_cps_read = d_cps.enqueueReadBuffer(true, tmp_cps, lastEvent.event );
			events.add(event_cps_read);
			
			EventTimer.Default proc_cps = new EventTimer.Default("processCPS");
			proc_cps.start();
			//processCPS( tmp_cps );
			fastProcessCPS( tmp_cps );
			proc_cps.stop();
			events.add(proc_cps);
			
		} catch (joclException e) {
			e.printStackTrace();
		}
		
		complete_time.stop();
		operationComplete = true;
		
	}


	
	static private class CPSTransfer {
		int read = 0;
		int id;
		float val;
		int ref;
		int [] setID = new int[8];
				
		static int size(){ return 11; }
		
		public boolean loadNext( ByteBuffer stream ){
			read++;
			id  = stream.getInt();
			val = stream.getFloat();
			ref = stream.getInt();
			for(int j = 0; j < 8; j++){
				setID[j] = stream.getInt();
			}
			return true;
		}
		
		public void print( PrintStream to ){
			to.print( "[" + read + "] " + id + " (value: " + val + ") (ref: " + ref + ") -- ");
			for(int j = 0; j < 8; j++){
				to.print(setID[j] + ", ");
			}
			to.println();
		}
	}

	int processed   = 0;
	int invalid     = 0;
	int unprocessed = 0;
	int total		= 0;
	
	private void fastProcessCPS( ByteBuffer tmp_cps ){
		
		processed   = 0;
		invalid     = 0;
		unprocessed = 0;
		total = 0;

		HashMap<Integer,PAugmentedMergeTreeNode> cp_map = new HashMap<Integer,PAugmentedMergeTreeNode>( );
		CPSTransfer currCP = new CPSTransfer( );
		
		int hpointer = tmp_cps.getInt();
		int cpsN     = (hpointer-1)/CPSTransfer.size();
		for(int i = 0; i < cpsN; i++ ){
			currCP.loadNext( tmp_cps );

			if( currCP.ref <= -2 ) processed++;
			else if( currCP.ref <= 1 ) invalid++;
			else unprocessed++;
			
			if( currCP.ref >= 2 ){
				
				PAugmentedMergeTreeNode currPJTN = createTreeNode( currCP.id );
				grid.add(currPJTN);
				total++;
				//System.out.println( currCP.id );
				
				for(int j = 0; j < currCP.ref; j++){
					PAugmentedMergeTreeNode chldPJTN = cp_map.get( currCP.setID[j] );
					if( chldPJTN == null ){
						chldPJTN = createTreeNode( currCP.setID[j] );
						grid.add(chldPJTN);
						total++;
					}
					currPJTN.addChild( chldPJTN );
					
					cp_map.put( currCP.setID[j], currPJTN );
				}
				this.head = currPJTN;
			}
		}
		//total = cp_map.size();
	}
	
	
	private void processCPS(ByteBuffer tmp_cps) {
		
		processed   = 0;
		invalid     = 0;
		unprocessed = 0;

		HashMap<Integer,PAugmentedMergeTreeNode> cp_map = new HashMap<Integer,PAugmentedMergeTreeNode>( );
		ArrayDisjointSet djs = new ArrayDisjointSet( sf.getSize() );
		
		CPSTransfer currCP = new CPSTransfer( );
		
		int hpointer = tmp_cps.getInt();
		int cpsN = (hpointer-1)/CPSTransfer.size();
		System.out.println( "Heap Pointer: " + hpointer + " cps:" + cpsN);
		for(int i = 0; i < cpsN; i++ ){
			currCP.loadNext( tmp_cps );

			if( currCP.ref <= -2 ) processed++;
			else if( currCP.ref <= 1 ) invalid++;
			else unprocessed++;
			
			if( currCP.ref <= -2 ){
				
				PAugmentedMergeTreeNode currPJTN = cp_map.get( currCP.id );
				if( currPJTN == null ){
					currPJTN = createTreeNode( currCP.id );
					cp_map.put( currCP.id, currPJTN );
				}
				
				for(int j = 0; j < 8; j++){
					if( currCP.setID[j] >= 0 ){
						PAugmentedMergeTreeNode chldPJTN = cp_map.get( currCP.setID[j] );
						if( chldPJTN == null ){
							chldPJTN = createTreeNode( currCP.setID[j] );
							cp_map.put( currCP.setID[j],  chldPJTN );
						}
						
						currPJTN.addChild( chldPJTN );
					}
				}
				this.head = cp_map.get(currCP.id);
			}
			else if( currCP.ref >= 2 ){
				int setN = 0;
				for(int j = 0; j < 8; j++){
					if( currCP.setID[j] >= 0 ){
						currCP.setID[j] = djs.find(currCP.setID[j]);
						for( int h = 0; h < j; h++ ){
							if( currCP.setID[h] == currCP.setID[j] ) currCP.setID[j] = -1;
						}
						if( currCP.setID[j] != -1 ){
							currCP.setID[setN++] = currCP.setID[j];
						}
					}
				}
				if( setN >= 2 ){
					for(int j = 0; j < setN; j++){
						djs.union( currCP.id, currCP.setID[j] );
					}
					if( !cp_map.containsKey(currCP. id ) ) cp_map.put( currCP.id,  createTreeNode( currCP.id ) );
					for(int j = 0; j < setN; j++){
						if( !cp_map.containsKey( currCP.setID[j] ) ) cp_map.put( currCP.setID[j],  createTreeNode( currCP.setID[j] ) );
						cp_map.get( currCP.id ).addChild(cp_map.get( currCP.setID[j] ));
					}
					this.head = cp_map.get(currCP.id);
				}
			}
		}
		total = cp_map.size();
	}


	
		
	
	protected abstract PAugmentedMergeTreeNode createTreeNode( int sf_node );
	
	public abstract class PAugmentedMergeTreeNode extends MergeTreeNode implements TopoTreeNode {
		
		int idx;
		
		protected PAugmentedMergeTreeNode( Integer _idx ) {
			super(_idx);
			idx = _idx;
		}
		
		protected PAugmentedMergeTreeNode( int _u,int _v ){
			super( new Integer(_v*sf.getWidth()+_u) );
			idx = _v*sf.getWidth()+_u;
			
		}
		
		@Override public float   getValue(){ return sf.getValue(idx); }
		//@Override public int     getPosition(){ return idx; }
		
		@Override public void addChild( MergeTreeNode c ){
			if( this.children.size() < 2 ){
				super.addChild(c);
			}
			else{
				// this code makes splits up multiway saddle cases
				MergeTreeNode c1 = children.get(1);
				children.remove(1);
				PAugmentedMergeTreeNode newc1 = createTreeNode( idx );
				children.add(newc1);
				newc1.addChild(c);
				newc1.addChild(c1);
			}
		}
		
	}

	
	

}
