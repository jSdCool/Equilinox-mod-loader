package com.modloader;

import java.lang.reflect.Field;

public class ErrorManagerFixer extends Thread{
	ClassLoader cl;
	ErrorManagerFixer(ClassLoader cl){
		super("error manager text replacemnt thread");
		this.cl=cl;
		start();
	}
	
	public void run() {
		replaceMessage();
	}
	void replaceMessage() {
		try {
			Class<?> em = cl.loadClass("errors.ErrorManager");
			Field messageField = em.getDeclaredField("defaultMessage");
			String message = (String) messageField.get(null);
			while(!message.startsWith("An error has caused the program to crash,")) {
				Thread.sleep(2);
			}
			messageField.set(null, "An error has occurred! This was most likely caused by a mod. You may wish to contact the relevant mod developer, if you do copy-paste the error message below:");
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
