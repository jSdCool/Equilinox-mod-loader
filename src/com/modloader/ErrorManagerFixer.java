package com.modloader;

import java.lang.reflect.Field;

/**replaces the default message on the error handler to a custom one for the mod loader
 * this is mostly so that the game developer's email address does not show up when then game crashes, and people don't email him about mod related issues
 * @author jSdCool
 *
 */
public class ErrorManagerFixer extends Thread{
	ClassLoader cl;
	/**
	 * @param cl the class loader that loaded the game
	 */
	ErrorManagerFixer(ClassLoader cl){
		super("error manager text replacemnt thread");
		this.cl=cl;
		start();
	}
	
	/**the start execution point for the thread
	 */
	public void run() {
		replaceMessage();
	}
	
	/**replace the default error message with a custom one
	 */
	private void replaceMessage() {
		try {
			//get a reference to the ErrorManager class since this class can not directly see it
			Class<?> em = cl.loadClass("errors.ErrorManager");
			//get a reference to the message field
			Field messageField = em.getDeclaredField("defaultMessage");
			//get the current message
			String message = (String) messageField.get(null);
			//while the message has not been set by the game wait
			while(!message.startsWith("An error has caused the program to crash,")) {
				Thread.sleep(2);
				message = (String) messageField.get(null);
			}
			//Replace the message
			messageField.set(null, "An error has occurred! This was most likely caused by a mod. You may wish to contact the relevant mod developer, if you do copy-paste the error message below:");
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
