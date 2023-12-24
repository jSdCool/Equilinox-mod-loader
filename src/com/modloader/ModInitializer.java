package com.modloader;

import java.util.List;

import com.modloader.events.AsyncLooping;
import com.modloader.events.OnGameLoad;
import com.modloader.events.SynchronousLooping;

/**this class is the entry point for your mod
 * @author jSdCool
 *
 */
public abstract class ModInitializer {
	/**this is the first method of your mode the game will call
	 * @param args the arguments passed in on the command line
	 */
	public abstract void initMod(List<String> args);
	
	/**registers and event listener of the given type
	 * @param e the event to register
	 */
	public final void registerEventListener(AsyncLooping e) {
		Main.registerEventListener(e);
	}
	
	/**registers and event listener of the given type
	 * @param e the event to register
	 */
	public final void registerEventListener(OnGameLoad e) {
		Main.registerEventListener(e);
	}
	
	/**registers and event listener of the given type
	 * @param e the event to register
	 */
	public final void registerEventListener(SynchronousLooping e) {
		Main.registerEventListener(e);
	}

}
