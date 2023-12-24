package com.modloader.events;

/**event that is called repeatedly by the API ar some point during the main game tick loop. on the game's main thread
 * NOTE: the CRISPR API is required for this event to function 
 * @author jSdCool
 *
 */
public interface SynchronousLooping {
	/**called by the API on the game's main thread
	 */
	public void syncLoop();
}
