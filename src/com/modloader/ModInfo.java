package com.modloader;

public class ModInfo {
	String modName,entryPoint,jarFile;
	byte priority;
	ModInfo(String name,String mainClass,String jarPath,byte priority){
		modName=name;
		entryPoint=mainClass;
		jarFile=jarPath;
		this.priority = priority;
	}
	
	String getModName() {
		return modName;
	}
	
	String getMainClass() {
		return entryPoint;
	}
	String getJar() {
		return jarFile;
	}
	
	byte getPriority() {
		return priority;
	}
}
