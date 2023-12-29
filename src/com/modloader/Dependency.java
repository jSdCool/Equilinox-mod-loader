package com.modloader;

/**Represents a mod dependency
 * this class is meant to be used internally in the mod loader
 * @author jSdCool
 */
public class Dependency {
	private final String id;
	private final Version version;
	private final boolean gte;
	
	Dependency(String id,Version version,boolean gte){
		this.id=id;
		this.version=version;
		this.gte=gte;
	}
	
	public String getId() {
		return id;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public boolean getGte(){
		return gte;
	}

}
