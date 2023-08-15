package com.modloader;

import com.modloader.events.AsyncLooping;

/**this class is the entry point for your mod
 * 
 * @author jSdCool
 *
 */
public abstract class ModInitializer {
	/**this is the first method of your mode the game will call
	 * 
	 */
	public abstract void initMod();
	
	/**registers and event listener of the given type
	 * 
	 * @param e the event to register
	 */
	public final void registerEventListener(AsyncLooping e) {
		if(!Main.asyncLoopingObjects.contains(e))
			Main.asyncLoopingObjects.add(e);
	}

}
