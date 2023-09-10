package com.modloader.events.exec;

import java.lang.reflect.Field;

import com.modloader.Main;

public class OnGameLoadExec {
	private static boolean exsists =false;
	Field keyBaordLockedField;
	Object keyBaord;
	public OnGameLoadExec(Field keyBaordLockedField, Object keyBaord){
		if(!exsists) {
			exsists = true;
		}else {
			throw new RuntimeException("attmpted to create too many instanced of "+this.getClass());
		}
		this.keyBaordLockedField = keyBaordLockedField;
		this.keyBaord = keyBaord;
	}
	
	public void run() {
		if(Main.onGameLoadObjects.size()==0) {
			System.out.println("no game load events found.");
			return;
		}
		
		/*try {
			keyBaordLockedField.setAccessible(true);
			while(!keyBaordLockedField.getBoolean(keyBaord)) {//wait for the keyboard to be locked
				Thread.sleep(2);
				keyBaordLockedField.setAccessible(true);
			}
			keyBaordLockedField.setAccessible(true);
			while(keyBaordLockedField.getBoolean(keyBaord)) {//wait for the keyboard to be unlocked
				Thread.sleep(2);
				keyBaordLockedField.setAccessible(true);
			}
		} catch (IllegalArgumentException | IllegalAccessException | InterruptedException e) {
			e.printStackTrace();
		}*/
		
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
