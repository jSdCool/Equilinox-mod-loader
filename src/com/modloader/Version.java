package com.modloader;

/**represents a software version in the format release.update.patch
 * @author jSdCool
 *
 */
public class Version {
	private final int release;
	private final int update;
	private final int patch;
	private final boolean anyPatch;
	
	/**create a version from a string
	 * @param v the string form of a version
	 */
	Version(String v){
		String[] parts = v.split(".");
		if(parts.length>3) {
			throw new VersionFormatException("too many version parts");
		}
		if(parts.length<3) {
			throw new VersionFormatException("not enough version parts");
		}
		try {
			release = Integer.parseInt(parts[0]);
		}catch(Exception e) {
			throw new VersionFormatException("invalid release value",e);
		}
		try {
			update = Integer.parseInt(parts[1]);
		}catch(Exception e) {
			throw new VersionFormatException("invalid update value",e);
		}
		try {
			if(parts[2].equals("x") || parts[2].equals("X")) {
				anyPatch=true;
				patch = -1;
			}else {
				patch = Integer.parseInt(parts[2]);
				anyPatch=false;
			}
		}catch(Exception e) {
			throw new VersionFormatException("invalid patch value",e);
		}
		if(release < 0 || update < 0 || patch < 0) {
			throw new VersionFormatException("negitive values are not allowed");
		}
		
	}
	
	/**create a version from integer values
	 * @param r release value
	 * @param u update value
	 * @param p patch value
	 */
	Version(int r,int u,int p){
		release = r;
		update = u;
		patch = p;
		anyPatch=false;
		if(release < 0 || update < 0 || patch < 0) {
			throw new VersionFormatException("negitive values are not allowed");
		}
	}
	
	/**get the version as a String
	 */
	public String toString() {
		if(anyPatch) {
			return release+"."+update+".X";
		}
		return release+"."+update+"."+patch;
	}
	
	/**weather or not the other is considered the same as the current one
	 * if either of them has the patch set to wild card then only the rel;ease and update will be checked
	 */
	public boolean equals(Object o) {
		if(!(o instanceof Version)) {
			return false;
		}
		Version outher = (Version)o;
		
		if(anyPatch || outher.anyPatch) {
			return release == outher.release && update == outher.update;
		}else {
			return release == outher.release && update == outher.update && patch == outher.patch;
		}
		
	}
	
	/**weather or not comparisons will accept any patch
	 */
	public boolean accesptsAnyPatch() {
		return anyPatch;
	}
	
	/**if the current version is greater then or equal to the supplied version
	 */
	public boolean greaterThanOrEqualTo(Version outher) {
		if(equals(outher)) {
			return true;
		}
		
		return release >= outher.release && update >= outher.update && patch >= outher.patch;
	}
	
	/**exception that gets thrown if a provided version is in an invalid format
	 * @author jSdCool
	 *
	 */
	static class VersionFormatException extends RuntimeException{
		private static final long serialVersionUID = 7152763374354364626L;
		public VersionFormatException(){}
		public VersionFormatException(String message){
			super(message);
		}
		public VersionFormatException(String message,Exception cause){
			super(message,cause);
		}
		
	}
}
