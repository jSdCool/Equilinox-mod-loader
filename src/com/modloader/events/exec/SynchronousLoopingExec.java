package com.modloader.events.exec;

import java.util.ArrayList;

import com.modloader.events.SynchronousLooping;

/** the class that is reasonable for the operation of the synchronous looping event
 * @author jSdCool
 *
 */
public class SynchronousLoopingExec {
	private static boolean exsists =false;
	ArrayList<SynchronousLooping> syncLoopingObjects;
	/**create the object that is responsible for propagating the synchronous looping event
	 * @param syncLoopingObjects the registered listeners
	 */
	public SynchronousLoopingExec(ArrayList<SynchronousLooping> syncLoopingObjects){
		if(!exsists) {//allow only 1 instance of this class to exist at once
			exsists = true;
			this.syncLoopingObjects =syncLoopingObjects;
		}else {
			throw new RuntimeException("attmpted to create too many instanced of "+this.getClass());
		}
	
	}
	
	/**run the synchronous looping event on all the registered listeners
	 */
	public void run() {
		//check if any game load events were register
		if(syncLoopingObjects.size()==0) {
			return;
		}
		
		//call the methods from the mods
		for(int i=0;i<syncLoopingObjects.size();i++) {
			try {
				syncLoopingObjects.get(i).syncLoop();;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
