package com.modloader;

public class ModInfo {
	private final String modName,entryPoint,jarFile,absoluteJarFile,modid;
	private final byte priority;
	ModInfo(String name,String mainClass,String jarPath,byte priority,String absoluteJarPath,String modid){
		modName=name;
		entryPoint=mainClass;
		jarFile=jarPath;
		this.priority = priority;
		absoluteJarFile=absoluteJarPath;
		this.modid=modid;
	}
	
	public String getModName() {
		return modName;
	}
	
	public String getMainClass() {
		return entryPoint;
	}
	public String getJar() {
		return jarFile;
	}
	
	public String getAbsoluteJar() {
		return absoluteJarFile;
	}
	
	public String getModID() {
		return modid;
	}
	
	public byte getPriority() {
		return priority;
	}
	
	public String toString() {
		return modName + " jarFile: " + jarFile + " jarFile Path: " + absoluteJarFile + " entry point: " + entryPoint + " mod id: "+modid;
	}
}
