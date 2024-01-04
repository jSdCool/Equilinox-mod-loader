package com.modloader.events.exec;

import java.util.ArrayList;

import com.modloader.Main;
import com.modloader.events.AsyncLooping;

/**the class that is reasonable for the operation of the async looping event
 * @author jSdCool
 *
 */
public final class AsyncLoopExec extends Thread{
	private static boolean exsists =false;
	private ArrayList<AsyncLooping> asyncLoopingObjects;
	/**create the object that is responsible for propagating the async looping event
	 * @param asyncLoopingObjects the registered listeners
	 */
	public AsyncLoopExec(ArrayList<AsyncLooping> asyncLoopingObjects){
		//allow only 1 instance of this class to exsist
		if(!exsists) {
			exsists = true;
			this.asyncLoopingObjects=asyncLoopingObjects;
			return;
		}else {
			throw new RuntimeException("attmpted to create too many instanced of "+this.getClass());
		}
		
	}
	
	/**Initialized by the mod loader
	 * Continuously loops over all register events and executes them async from the game
	 */
	public void run() {
		//stop of no async looping events were registered
		if(asyncLoopingObjects.size()==0) {
			System.out.println("no async loops found. terminated async loop thread");
			return;
		}
		//while the game is running
		while(Main.gameRunning) {
			//go through all resisted events and execute them
			for(int i=0;i<asyncLoopingObjects.size();i++) {
				try {
					asyncLoopingObjects.get(i).asyncLoop();
				} catch (Exception e) {
					System.err.println("An async loop ran into an error: ");
					e.printStackTrace();
				}
			}
		}
	}
}
