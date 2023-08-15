package com.modloader;

public class InitializerCallback {
	ModInitializer mod;
	
	public void set(ModInitializer mod) {
		this.mod=mod;
	}
	
	public ModInitializer get() {
		return mod;
	}
}
