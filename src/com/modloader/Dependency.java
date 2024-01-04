package com.modloader;

/**Represents a mod dependency
 * this class is meant to be used internally in the mod loader
 * @author jSdCool
 */
public class Dependency {
	private final String id;
	private final Version version;
	private final boolean gte;
	
	/**
	 * @param id the ID of the required mod
	 * @param version the requested version of the required mod
	 * @param gte if the version is greater then or equal to
	 */
	Dependency(String id,Version version,boolean gte){
		this.id=id;
		this.version=version;
		this.gte=gte;
	}
	
	/**
	 * @return the id of the required mod
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return the version of the required mod 
	 */
	public Version getVersion() {
		return version;
	}
	
	/**
	 * @return weather the requested version is greater than or equal to
	 */
	public boolean getGte(){
		return gte;
	}

}
