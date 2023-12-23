package com.modloader.events;

/**event that is called by the API at some point during the game loading process
 * NOTE: the CRISPR API is required for this event to function 
 * @author jSdCool
 *
 */
public interface OnGameLoad {
	/**called by the API during game load
	 */
	public void gameLoaded();
}
