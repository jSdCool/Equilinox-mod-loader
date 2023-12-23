package com.modloader.events.exec;

import com.modloader.Main;

/**the class that is reasonable for the operation of the 
 * @author jSdCool
 *
 */
public class OnGameLoadExec {
	private static boolean exsists =false;
	public OnGameLoadExec(){
		if(!exsists) {//allow only 1 instance of this class to exist at once
			exsists = true;
		}else {
			throw new RuntimeException("attmpted to create too many instanced of "+this.getClass());
		}
	
	}
	
	/**run the game load event on all the register listeners
	 */
	public void run() {
		//check if any game load events were register
		if(Main.onGameLoadObjects.size()==0) {
			System.out.println("no game load events found.");
			return;
		}
		
	
		
		//call the methods from the mods
		for(int i=0;i<Main.onGameLoadObjects.size();i++) {
			try {
				Main.onGameLoadObjects.get(i).gameLoaded();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
