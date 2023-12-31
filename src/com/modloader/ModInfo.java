package com.modloader;

/**represents the relevant info about a mod as obtained from the mod.json file inside of a mod
 * used by the mod loader to identify and properly load mods
 * @author jSdCool
 *
 */
public class ModInfo {
	private final String modName,entryPoint,jarFile,absoluteJarFile,modid;
	private final byte priority;
	private final Version version,loaderVersion;
	private final Dependency[] dependencies;
	private final boolean loaderGte;
	final String[] authors;
	ModInfo(String name,String mainClass,String jarPath,byte priority,String absoluteJarPath,String modid,Version version,Version loaderVersion,boolean loaderGte,Dependency[] dependencies,String[] authors){
		modName=name;
		entryPoint=mainClass;
		jarFile=jarPath;
		this.priority = priority;
		absoluteJarFile=absoluteJarPath;
		this.modid=modid;
		this.version=version;
		this.loaderVersion = loaderVersion;
		this.dependencies = dependencies;
		this.loaderGte=loaderGte;
		this.authors=authors;
	}
	
	/**get the name of the mod
	 * @return the defined name of the mod in the mod.json file
	 */
	public String getModName() {
		return modName;
	}
	
	/**get the main class of the mod
	 * @return the main class as defined in the mod.json file
	 */
	public String getMainClass() {
		return entryPoint;
	}
	
	/**get the path to the jar file
	 * @return the path to the jar file in question
	 */
	public String getJar() {
		return jarFile;
	}
	
	/**get the path to the jar file
	 * @return the absolute file system path to the jar file
	 */
	public String getAbsoluteJar() {
		return absoluteJarFile;
	}
	
	/**get the id of the mod
	 * @return the id of the mod as defined by the mod.json file
	 */
	public String getModID() {
		return modid;
	}
	
	/**get the loading priority of the mod
	 * @return the priority of the mod (0-5) as defined by the mod.json file
	 */
	public byte getPriority() {
		return priority;
	}
	
	/**get the version of the mod
	 * @return the mod's version
	 */
	public Version getVersion() {
		return version;
	}
	
	public String toString() {
		return modName + " jarFile: " + jarFile + " jarFile Path: " + absoluteJarFile + " entry point: " + entryPoint + " mod id: "+modid+" version: "+version;
	}
	
	/**get the required mod loader version for this mod
	 * @return the required mod loader version
	 */
	public Version getLoaderVersion() {
		return loaderVersion;
	}
	
	/**get a list of other mods this mod depends on
	 * @return a list of dependencies
	 */
	public Dependency[] getDependencies() {
		return dependencies;
	}
	
	/**get weather or not the loader version should be greater then or equal to
	 * @return if the loader version id greater then or equal to
	 */
	public boolean getLoaderGte() {
		return loaderGte;
	}
	
	/**get the authors of the mod
	 * @return a string array containing the names of the mod authors
	 */
	public String[] getAuthors() {
		return authors;
	}
}
