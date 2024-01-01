package com.modloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

import org.json.JSONObject;

import com.modloader.events.AsyncLooping;
import com.modloader.events.OnGameLoad;
import com.modloader.events.SynchronousLooping;
import com.modloader.events.exec.AsyncLoopExec;
import com.modloader.events.exec.OnGameLoadExec;
import com.modloader.events.exec.SynchronousLoopingExec;

/**main class for the EquilinoxMod loader
 * @author jSdCool
 *
 */
public class Main {
	static final String tempLibPath  = System.getenv("tmp")+"/eml/bin";
	public static boolean gameRunning=true;
	private static ArrayList<AsyncLooping> asyncLoopingObjects = new ArrayList<>();
	private static ArrayList<OnGameLoad> onGameLoadObjects = new ArrayList<>();
	private static ArrayList<SynchronousLooping> syncLoopingObjects = new ArrayList<>();
	public static ArrayList<ModInfo> modInfo;
	private static OnGameLoadExec onGameLoadExec;
	private static SynchronousLoopingExec syncLoopExec;
	private static boolean gameLaoded =false,APIExsists=false;
	
	/**the method that is called by the JVM when the program is launched
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		//check the java version to make sure it is java 8. key features of java used by the mod loader do not work in java 9+
		String javaVersion = System.getProperty("java.version");
		if(!javaVersion.startsWith("1.8")) {
			JOptionPane.showMessageDialog(null, "Java 1.8 is required to use this mod loader,\ncurrently running in java "+javaVersion, "java 8 required!", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			return;
		}
		
		String gameJarPath="EquilinoxWindows_game.jar";
		ArrayList<String> additionalMods = new ArrayList<>();
		//process command line args
		for(int i=0;i<args.length;i++) {
			//if the game jar file is in another location
			//specify the location of the game jar
			if(args[i].equals("--gameLocation")) {
				i++;
				if(i<args.length) {
					gameJarPath = args[i];
				}
				continue;
			}
			//provide locations for additional mods that are not in the mod folder
			//this is mostly intended for mod development. to prevent unnecessary movement of files
			//note the proved vale must be the file path of a jar file
			if(args[i].equals("-mod")){
				i++;
				if(i<args.length) {
					additionalMods.add(args[i]);
				}
				continue;
			}
			
		}
		
		AsyncLoopExec asyncLoopting = new AsyncLoopExec(asyncLoopingObjects);
		
		// Get the game JAR file 
        File jarFile = new File(gameJarPath);

        try {//extract any native code that will need to be loaded
        	System.out.println("extratcing native libs from game");
			extractNativeLibraries(jarFile);
		} catch (IOException e) {
			throw new RuntimeException("Exception occored while attemping to extract native libs",e);
		}
        
        //add the native code dir we created to the lib path
        System.out.println("patching java lib path");
        String libPath=System.getProperty("java.library.path");
        libPath = libPath.substring(0,libPath.length()-1)+tempLibPath+";.";
        System.setProperty("java.library.path",libPath);
        
        try {//make the class loader refresh the system path list
            Field field = ClassLoader.class.getDeclaredField("sys_paths");
            field.setAccessible(true);
            field.set(null, null);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        
        File modsFolder = new File("mods");
        modsFolder.mkdirs();//create the mods folder in the game files if it does not already exist
        
        try {
        	System.out.println("attempting to url the game jar");
        	//create a new class loader that has the game jar on it and has the class loader for this class in its tree
	        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()},Main.class.getClassLoader());
	        
	        System.out.println("loading clazz");
	        // Load the main class from the game
	        Class<?> clazz = classLoader.loadClass("main.MainApp");
	        
	        System.out.println("instacing object");
	        // Create an instance of the main class
	        Object instance = clazz.newInstance();
	        
	        System.out.println("acuiring main method");
	        // get the main method of the game 
	        Method mainMethod = clazz.getMethod("main", String[].class);
	      
	        //load mods here
	        modInfo = findMods(modsFolder,additionalMods);//find all the mods in the mods folder
	        
	        ArrayList<URL> modJars=new ArrayList<>();
	        ArrayList<ModInitializer> modClasses = new ArrayList<>();
	        
	        for(int i=0;i<modInfo.size();i++) {//make the path of the mod jars into a URL array so the computer knows what to load
	        	modJars.add(new File("mods/"+modInfo.get(i).getJar()).toURI().toURL());
	        }
	        
	        URLClassLoader modClassLoader = new URLClassLoader(modJars.toArray(new URL[]{}),classLoader);//load the jars
	        //convert args into a list that can be passed around to all the mods without letting any of the modify it
	        List<String> clArgs = Arrays.asList(args);
	        clArgs = Collections.unmodifiableList(clArgs);
	        
	        for(int j=0;j<6;j++) {//go through all 6 priority  levels
		        for(int i=0;i<modInfo.size();i++) {//load the main classes of each mod and execute it's main method
		        	if(modInfo.get(i).getPriority()==j) {//if this mod is of the current priority
			        	System.out.println("loading mod: "+modInfo.get(i).getModName());
			        	//if the mod is the API then enable the api features
			        	if(modInfo.get(i).getModID().equals("api")) {
			        		APIExsists=true;
			        	}
			        	System.out.println(modInfo.get(i).toString());
			        	Class<?> modClass = modClassLoader.loadClass(modInfo.get(i).getMainClass());//load the main class of the mod
			        	Object c = modClass.newInstance();//create an instance of the main class
			        	((ModInitializer)c).initMod(clArgs);//run the init(main) method of the mod
			        	modClasses.add((ModInitializer)c);//cast it to a ModInitializer so it can be handled natively instead of via reflection. then store it in a arrayList for later use
		        	}
		        }
	        }
	        
	        //auto register event listeners 
	        for(int i=0;i<modClasses.size();i++) {
	        	ModInitializer m = modClasses.get(i);
	        	//AsyncLooping event 
	        	if(m instanceof AsyncLooping) {
	        		//if the event was not already registered
	        		if(!asyncLoopingObjects.contains((AsyncLooping)m))
	        			asyncLoopingObjects.add((AsyncLooping)m);
	        	}
	        	//game loaded event
	        	if(m instanceof OnGameLoad) {
	        		if(!onGameLoadObjects.contains((OnGameLoad)m)) {
	        			onGameLoadObjects.add((OnGameLoad)m);
	        		}
	        	}
	        	//sync looping event
	        	if(m instanceof SynchronousLooping) {
	        		if(!syncLoopingObjects.contains((SynchronousLooping)m)) {
	        			syncLoopingObjects.add((SynchronousLooping)m);
	        		}
	        	}
	        }
	        //warnings if API is not present
	        if(!APIExsists) {
		        if(onGameLoadObjects.size()>0) {
		        	System.out.println("==WARNING== attempted to register game loaded event without CRISPR API present. Any functionality that relys on game loaded event will not work!");
		        }
		        if(syncLoopingObjects.size()>0) {
					System.out.println("==WARNING== attempted to register game loaded event without CRISPR API present. Any functionality that relys on sync looping event will not work!");
		        }
	        }
	        
	        //Create the object for the game load event
			onGameLoadExec = new OnGameLoadExec(onGameLoadObjects);
			//create the object for the sync looping event
			syncLoopExec = new SynchronousLoopingExec(syncLoopingObjects);
			
	        
	        //start the async looping event
	        asyncLoopting.start();
	        //run the game
	        System.out.println("executing main medthod");
	        mainMethod.invoke(instance, (Object)args);
	        modClassLoader.close();
	        classLoader.close();
	        
        }catch(MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException m) {
        	m.printStackTrace();
        } catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        System.out.println("program terminating");
        gameRunning = false;//used to tell any parallel threads that the program has stopped
        
	}
	
	/**extracts all native libs from a jar file
	 * then places them at the temp path
	 * @param jarFile the jar file to extract from
	 * @throws IOException generic IOExceoption
	 */
	private static void extractNativeLibraries(File jarFile) throws IOException {
		
		new File(tempLibPath ).mkdirs();//create the tmp dir if it does not exist
	    JarFile jar = new JarFile(jarFile);
	    Enumeration<JarEntry> entries = jar.entries();
	    //look through all the files in the jar file
	    while (entries.hasMoreElements()) {
	        JarEntry entry = entries.nextElement();//get the next thing in the jar
	        if (entry.getName().endsWith(".dll") || entry.getName().endsWith(".so")) {//if that thing is a .dll or .so
	            File outputFile = new File(tempLibPath , entry.getName());//copy the file out of the jar into the tmp dir
	            try (InputStream inputStream = jar.getInputStream(entry);
	                 FileOutputStream outputStream = new FileOutputStream(outputFile)) {
	                byte[] buffer = new byte[1024];
	                int bytesRead;
	                while ((bytesRead = inputStream.read(buffer)) != -1) {
	                    outputStream.write(buffer, 0, bytesRead);
	                }
	            }
	        }
	    }
	    jar.close();
	}
	
	/**finds all mods(jar files) in the mods folder
	 *then attempts to acquire  basic information0 about the mod
	 * @param modsFolder the folder the mods are in
	 * @return a list of information about all the mods found
	 */
	static ArrayList<ModInfo> findMods(File modsFolder,ArrayList<String> additionaMods){
		String jsonPos ="mod.json";
		
		ArrayList<ModInfo> mods = new ArrayList<>();
		String[] modFiles = modsFolder.list();//get a list of all files/folders in the mods folder
		for(String s:modFiles) {
			additionaMods.add(s);
		}
		modFiles = additionaMods.toArray(modFiles);
		for(int i=0;i<modFiles.length;i++) {
			if(modFiles[i].endsWith(".jar")) {//only attempt to check jar files
				
				try (JarFile jarFile = new JarFile("mods/"+modFiles[i])) {
		            JarEntry entry = jarFile.getJarEntry(jsonPos);//get the json file in the jar file
		            if (entry != null) {//if the json file exists
		            	InputStream inputStream = jarFile.getInputStream(entry);
		            	//extract the raw contents
		            	StringBuilder jsonContent = new StringBuilder();
	                    int bytesRead;
	                    byte[] buffer = new byte[1024];
	                    while ((bytesRead = inputStream.read(buffer)) != -1) {
	                        jsonContent.append(new String(buffer, 0, bytesRead));
	                    }

	                    JSONObject json = new JSONObject(jsonContent.toString());
	                    
	                    //retrieve the mod info from the file
	                    String modName = json.getString("name");
	                    String mainClass = json.getString("entrypoint");
	                    String id = json.getString("modid");
	                    byte priority = (byte)json.getInt("priority");
	                    for(int j=0;j<mods.size();j++) {
	                    	if(mods.get(j).getModID().equals(id)) {
	                    		throw new RuntimeException("attempted to load 2 mods with the same ID");
	                    	}
	                    }
	                    mods.add(new ModInfo(modName, mainClass,modFiles[i],priority,new File("mods/"+modFiles[i]).getAbsolutePath(),id));
	                    
		            }
				} catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}
		
		return mods;
		
	}
	
	/**called by the API when the game is loaded
	 * runs the game loaded event
	 */
	public static void gameLoaded() {
		if(!gameLaoded) {
			gameLaoded=true;
			onGameLoadExec.run();
		}
	}
	
	/**called by the API on every game tick
	 * runs the sync loop event
	 */
	public static void gameTick() {
		syncLoopExec.run();
	}
	
	/**check weather the API is present
	 * @return weather the API is currently loaded
	 */
	public static boolean APIExsists() {
		return APIExsists;
	}
	
	public static final void registerEventListener(AsyncLooping e) {
		if(!asyncLoopingObjects.contains(e))
			asyncLoopingObjects.add(e);
	}
	
	/**registers and event listener of the given type
	 * @param e the event to register
	 */
	public static final void registerEventListener(OnGameLoad e) {
		if(!APIExsists()) {
			System.out.println("==WARNING== attempted to register game loaded event without CRISPR API present. Any functionality that relys on game loaded event will not work!");
		}
		
		if(!onGameLoadObjects.contains(e))
			onGameLoadObjects.add(e);
	}
	
	/**registers and event listener of the given type
	 * @param e the event to register
	 */
	public static final void registerEventListener(SynchronousLooping e) {
		if(!APIExsists()) {
			System.out.println("==WARNING== attempted to register game loaded event without CRISPR API present. Any functionality that relys on sync looping event will not work!");
		}
		
		if(!syncLoopingObjects.contains(e))
			syncLoopingObjects.add(e);
	}

}
