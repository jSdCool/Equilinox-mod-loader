package com.modloader;

import com.modloader.events.AsyncLooping;
import com.modloader.events.OnGameLoad;

/**this class is the entry point for your mod
 * @author jSdCool
 *
 */
public abstract class ModInitializer {
	/**this is the first method of your mode the game will call
	 */
	public abstract void initMod();
	
	/**registers and event listener of the given type
	 * @param e the event to register
	 */
	public final void registerEventListener(AsyncLooping e) {
		if(!Main.asyncLoopingObjects.contains(e))
			Main.asyncLoopingObjects.add(e);
	}
	
	/**registers and event listener of the given type
	 * @param e the event to register
	 */
	public final void registerEventListener(OnGameLoad e) {
		if(!Main.APIExsists()) {
			System.out.println("==WARNING== attempted to register game loaded event without CRISPR API present. Any functionality that relys on game loaded event will not work!");
		}
		
		if(!Main.onGameLoadObjects.contains(e))
			Main.onGameLoadObjects.add(e);
	}

}
