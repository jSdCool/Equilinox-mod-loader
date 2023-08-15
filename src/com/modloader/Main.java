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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.json.JSONObject;

import com.modloader.events.AsyncLooping;

public class Main {
	static final String tempLibPath  = System.getenv("tmp")+"/eml/bin";
	static boolean gameRunning=true;
	static ArrayList<AsyncLooping> asyncLoopingObjects = new ArrayList<>();
	
	public static void main(String[] args) {
		String gameJarPath="EquilinoxWindows_game.jar";
		AsyncLoopExec asyncLoopting = new AsyncLoopExec();
		
		// Get the game JAR file 
        File jarFile = new File(gameJarPath);

        try {//extract any native code that will need to be loaded
        	System.out.println("extratcing native libs from game");
			extractNativeLibraries(jarFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
        modsFolder.mkdirs();//create the mods folder in the game files
        
        try {
        	System.out.println("attempting to url the jar");
	        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()},Main.class.getClassLoader());
	        
	        System.out.println("loading clazz");
	        // Load the main class from the game
	        Class<?> clazz = classLoader.loadClass("main.MainApp");
	        
	        System.out.println("instacing object");
	        // Create an instance of the main class
	        Object instance = clazz.newInstance();
	        
	        System.out.println("acuiring main method");
	        // get the main method of the game 
	        Method method = clazz.getMethod("main", String[].class);
	        
	        //load mods here
	        ArrayList<ModInfo> modInfo = findMods(modsFolder);//find all the mods in the mods folder
	        
	        ArrayList<URL> modJars=new ArrayList<>();
	        ArrayList<ModInitializer> modClasses = new ArrayList<>();
	        
	        for(int i=0;i<modInfo.size();i++) {//make the path of the mod jars into a URL array so the computer knows what to load
	        	modJars.add(new File("mods/"+modInfo.get(i).getJar()).toURI().toURL());
	        }
	        
	        URLClassLoader modClassLoader = new URLClassLoader(modJars.toArray(new URL[]{}),classLoader);//load the jars
	        
	        for(int j=0;j<6;j++) {
		        for(int i=0;i<modInfo.size();i++) {//load the main classes of each mod and execute it's main method
		        	if(modInfo.get(i).getPriority()==j) {//if this mod is of the current priority
			        	System.out.println("loading mod: "+modInfo.get(i).getModName());
			        	Class<?> modClass = modClassLoader.loadClass(modInfo.get(i).getMainClass());
			        	Object c = modClass.newInstance();
			        	((ModInitializer)c).initMod();
			        	modClasses.add((ModInitializer)c);
		        	}
		        }
	        }
	        
	        //auto register event listeners 
	        for(int i=0;i<modClasses.size();i++) {
	        	//AsyncLooping event 
	        	if(modClasses.get(i) instanceof AsyncLooping) {
	        		//if the event was not already registered
	        		if(!asyncLoopingObjects.contains((AsyncLooping)modClasses.get(i)))
	        			asyncLoopingObjects.add((AsyncLooping)modClasses.get(i));
	        	}
	        }
	        
	        asyncLoopting.start();
	        //run the game
	        System.out.println("executing main medthod");
	        method.invoke(instance, (Object)args);
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
        gameRunning = false;
        
	}
	
	/**extracts all native libs from a jar file
	 * 
	 * @param jarFile the jar file to extract from
	 * @throws IOException
	 */
	private static void extractNativeLibraries(File jarFile) throws IOException {
		
		new File(tempLibPath ).mkdirs();//create the tmp dir
	    JarFile jar = new JarFile(jarFile);
	    Enumeration<JarEntry> entries = jar.entries();

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
	
	/**finds the mods
	 * 
	 * @param modsFolder the folder the mods are in
	 * @return
	 */
	static ArrayList<ModInfo> findMods(File modsFolder){
		String jsonPos ="mod.json";
		
		ArrayList<ModInfo> mods = new ArrayList<>();
		String[] modFiles = modsFolder.list();//get a list of all files/folders in the mods folder
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
	                    byte priority = (byte)json.getInt("priority");
	                    mods.add(new ModInfo(modName, mainClass,modFiles[i],priority));
		            	
		            }
				} catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}
		
		return mods;
		
	}

}
