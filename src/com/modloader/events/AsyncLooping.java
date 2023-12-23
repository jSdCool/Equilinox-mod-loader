package com.modloader.events;

/**event that is called repeatedly by the mod loader completely async from the game
 * @author jSdCool
 *
 */
public interface AsyncLooping {
	/**called by the mod loader as frequently as possible
	 */
	public void asyncLoop();
}
