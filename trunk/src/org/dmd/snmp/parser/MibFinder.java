//	---------------------------------------------------------------------------
//	dark-matter-data
//	Copyright (c) 2010 dark-matter-data committers
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.dmd.snmp.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.dmd.util.exceptions.ResultException;

/**
 * The ConfigFinder utility recursively hunts through the source directories of the
 * current set of Eclipse projects your have on your build path, as well as any JAR 
 * files that end with a specific suffix (e.g. *DMSchema.jar) and finds configuration
 * files that end with a specific suffix e.g. .dms .dmg etc.
 * <P>
 * Add the suffixes you're looking for by calling addSuffix(). You must provide at
 * least one suffix, or the findConfigs() method will fail.
 * <P>
 * The ConfigFinder also understands the convention of versioning your configuration
 * files by storing them in subfolders named v<#>dot<#>. For instance, for a versioned
 * schema you might have schema/v0dot1, schema/v1dot23, schema/v11dot3dot1 which would
 * represent schemas at version 0.1, 1.23 and 11.3.1 respectively.
 * <P>
 * NOTE: as indicated, the finder assumes that you're working in an Eclipse environment
 * where you have a directory structure like: project/bin project/src. The finder will
 * see the project/bin on the java.class.path and then try to find the src directory in
 * the same location.
 * <P>
 * If your development environment doesn't conform to this arrangement, you can derive
 * your own class, or, manually specify the source directories to search by priming the
 * ConfigFinder with calls to addSourceDirectory().
 */
public class MibFinder {

	// The source paths that we're going to search
	ArrayList<String>	sourceDirs;
	
	// The folders that we're going to check for MIB files
	ArrayList<String>	mibFolders;
	
	ArrayList<String>	jarPrefixes;
	
	// The individual configs that we've found
	ArrayList<MibLocation>	configs;
	
	// These are the class paths we searched
	ArrayList<String>	classPaths;
		
	String fsep;
	
	// The preferences file we attempt to read
	String 	prefName;
	boolean	prefsAvailable;
	
	// The length of the longest schema name we found
	int	longest;
	
	boolean	debug;
	
	public MibFinder(){
		init();
//		loadPreferences();
	}
	
	/**
	 * Constructs a new ConfigFinder that will search the specified folders for
	 * configurations.
	 * @param srcdirs source directories that have been specified on the commandline.
	 */
	public MibFinder(Iterator<String> srcdirs){
		init();
		while(srcdirs.hasNext()){
			sourceDirs.add(srcdirs.next());
		}
		prefsAvailable = true;
	}
	
	public void debug(boolean db){
		debug = db;
	}
	
	void debugMessage(String message){
		if (debug)
			System.out.println(message);
	}
	
	void init(){
		debugMessage("ConfigFinder.initializing()");
		
		sourceDirs 		= new ArrayList<String>();
		mibFolders 		= new ArrayList<String>();
		jarPrefixes		= new ArrayList<String>();
		configs			= new ArrayList<MibLocation>();
		fsep 			= File.separator;
		prefsAvailable 	= false;
		classPaths 		= new ArrayList<String>();
	}
	
	/**
	 * @return The name of the file where additional source paths are indicated.
	 */
	public String getPrefName(){
		return(prefName);
	}
	
	/**
	 * Adds a MIB folder to hunt for. Generally collections of MIBs are stored in particular folders.
	 * @param s the suffix to hunt for.
	 */
	public void addMibFolder(String s){
		mibFolders.add(s);
	}
	
	/**
	 * Adds a jar prefix to hunt for. Any jar starting with the specified prefix will be
	 * searched for MIB files.
	 * @param e the JAR prefix to hunt for.
	 */
	public void addJarPrefix(String e){
		jarPrefixes.add(e);
	}
	
	/**
	 * Adds a source directory root to the set of paths that the finder will
	 * traverse in search of schemas.
	 * @param dir The source directory.
	 */
	public void addSourceDirectory(String dir){
		sourceDirs.add(dir);
	}
	
	/**
	 * Scans the class path (and an additional source directories) for files
	 * ending with the suffixes you've specified.
	 * @throws ResultException 
	 * @throws IOException  
	 */
	public void findMIBs() throws ResultException, IOException {
		debugMessage("Finding MIBs:\n\n" + getSearchInfo() + "\n");
		
		if (mibFolders.size() == 0){
			ResultException ex = new ResultException("You must specify at least one MIB folder to hunt for using the addMibFolder() method");
			throw(ex);
		}
		
		for(String d : sourceDirs)
			findConfigsRecursive(new File(d));
		
		findMIBsOnClassPath();
		
		debugMessage("MIB search complete: " + getSearchInfo() + "\n");		
	}
		
	/**
	 * @return An iterator over all the configs we found.
	 */
	public Iterator<MibLocation> getLocations(){
		return(configs.iterator());
	}
	
	/**
	 * @return the length of the longest config name.
	 */
	public int getLongestName(){
		return(longest);
	}
	
	/**
	 * Returns a description of where we searched for your config files.
	 * @return A string indicating the source paths and class paths searched as well as the
	 * suffixes and JAR endings we used.
	 */
	public String getSearchInfo(){
		StringBuffer sb = new StringBuffer();
		
		if (prefName == null)
			sb.append("Source directory preferences from -srcdir option:\n");
		else
			sb.append("Source directory preferences: " + prefName + "\n");
		
		if (prefsAvailable){
			for(String f : sourceDirs){
				sb.append("    " + f + "\n");
			}
		}
		else
			sb.append("No preferences specified");
		sb.append("\n");
		
		sb.append("Checked the following locations on your class path:\n");
		
		for(String c : classPaths){
			sb.append("    " + c + "\n");
		}
		
		sb.append("\n");
		
		if (jarPrefixes.size() > 0){
			sb.append("    Checked JARs with the following prefixs:\n");
			for(String j : jarPrefixes){
				sb.append("    " + j + "\n");
			}
			sb.append("\n");
		}
		
		sb.append("For MIB files in folders containing these substrings:\n");
		for(String s : mibFolders){
			sb.append("    " + s + "\n");
		}
		
		return(sb.toString());
	}
	
//	/**
//	 * This method will check to see if the user has created a sourcedirs.txt
//	 * in user_home/.darkmatter
//	 */
//	void loadPreferences(){
//		
//		String userHome = System.getProperty("user.home");
//		File darkMatterFolder = new File(userHome + fsep + ".darkmatter");
//		
//		debugMessage("loadPreferences() - " + userHome + fsep + ".darkmatter");
//		
//		// Create the preferences folder if it doesn't exist
//		if (!darkMatterFolder.exists()){
//			darkMatterFolder.mkdir();
//		}
//		
//		prefName = userHome + fsep + ".darkmatter" + fsep + "sourcedirs.txt";
//		File prefFile = new File(prefName);
//		
//		if (prefFile.exists()){
//			prefsAvailable = true;
//            try {
//            	LineNumberReader in = new LineNumberReader(new FileReader(prefName));
//                String str;
//                while ((str = in.readLine()) != null) {
//                	String line = str.trim();
//                	
//                	if (line.startsWith("//"))
//                		continue;
//                	
////                	if (line.endsWith(".jar"))
////                		jarPrefixes.add(line);
////                	else
//                		sourceDirs.add(line);
//                }
//                
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
	
	/**
	 * Recursively descends through the directory structure looking for files
	 * that end with any of the suffixes that have been specified.
	 * @param d The directory to search.
	 * @throws ResultException
	 * @throws IOException 
	 */
	void findConfigsRecursive(File dir) throws ResultException, IOException {
		if (dir.exists()){
			String[] files = dir.list();
			
			for(String f : files){
				for (String suffix : mibFolders){
//					DebugInfo.debug("Checking suffix: " + suffix + " against " + f);					
					if (f.endsWith(suffix)){
						if (f.startsWith("meta"))
							continue;
						
						MibLocation newLocation = new MibLocation(f, dir.getCanonicalPath(), suffix);
						
						addConfig(newLocation);
						
						if (newLocation.getConfigName().length() > longest)
							longest = newLocation.getConfigName().length();
					}
					else{
						String fullname = dir.getAbsolutePath() + File.separator + f;
						File curr = new File(fullname);
						if (curr.isDirectory())
							findConfigsRecursive(curr);
					}
				}
			}
		}
		else{
			ResultException ex = new ResultException();
			ex.addError("Specified source directory doesn't exist: " + dir.getCanonicalPath());
			throw(ex);
		}
	}
	
	/**
	 * Attempts to add the new location. If the version clashes with an existing version,
	 * we pitch an exception.
	 * @param cl The new location.
	 * @throws ResultException
	 */
	void addConfig(MibLocation cl) throws ResultException {
//		DebugInfo.debug("*** Adding config: " + cl.getConfigName());
		
//		ConfigVersion cv = versions.get(cl.getConfigName());
//		
//		if (cv == null){
//			cv = new ConfigVersion();
//			versions.put(cl.getConfigName(), cv);
//		}
//		else{
//			ConfigLocation existing = cv.getLatestVersion();
//			
//			if (!cl.getConfigParentDirectory().equals(existing.getConfigParentDirectory())){
//				System.out.println("\nClashing config names: " + cl.configName);
//				System.out.println("    " + existing.getConfigParentDirectory());
//				System.out.println("    " + cl.getConfigParentDirectory() + "\n");
//			}
//		}
//		
//		cv.addVersion(cl);
		
		// Just add that puppy
//		DebugInfo.debug("Found config\n\n" + cl.toString());
		debugMessage("found possible MIB file: " + cl.toString());
		configs.add(cl);
	}
	
//	/**
//	 * @return A listing of the schemas we've found.
//	 */
//	public String getSchemaListing(){
//		StringBuffer sb = new StringBuffer();
//		
//		for(ConfigLocation dsl : configs.values()){
//			sb.append(dsl.getConfigName() + " -- " + dsl.getDirectory() + "\n");
//		}
//		return(sb.toString());
//	}
	
	/**
	 * This method checks the current class path for /bin directories (that, in Eclipse,
	 * give us a hint as to where the /src directories are) and JAR files whose names end
	 * with a JAR ending you've specified. Such JARs are assumed to contain files with .xxx
	 * file extensions. This mechanism allows you to easily import configs defined elsewhere,
	 * in other projects you have open or exported in JARs from other sources.
	 * @throws IOException  
	 * @throws ResultException 
	 */
	void findMIBsOnClassPath() throws IOException, ResultException {
		
//		String[] paths = System.getProperty("java.class.path").split(";");
		String[] paths = System.getProperty("java.class.path").split(File.pathSeparator);
		
		debugMessage("findConfigsOnClassPath()");
		for(String f : paths){
			
			debugMessage("    checking: " + f);
			if ((jarPrefixes.size() > 0) && f.endsWith(".jar")){
				debugMessage("\n    we have a jar - adding to classPaths");
				classPaths.add(f);
				
				for(String jarPrefix : jarPrefixes){
					debugMessage("    checking against prefix: " + jarPrefix);
					String jarName = "";
					
					int lastSlash = f.lastIndexOf(File.separator);
					if (lastSlash != -1)
						jarName = f.substring(lastSlash+1);
					debugMessage("    jar name: " + jarName);
					
					if (jarName.startsWith(jarPrefix)){
						debugMessage("findMIBsOnClassPath() - jar starts with prefix " + f);
						// We have a JAR of interest - an example might look like:
						// file:F:\AASoftDev\workspace\dark-matter-data\extjars\exampleDMSchema.jar
						JarFile jar = new JarFile(f);	        
				        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();)
				        {
				            String jarEntry = ((JarEntry)entries.nextElement()).getName();
				            
				            for(String folder : mibFolders){
				            	if (jarEntry.contains(folder)){
				            		
				            		if (jarEntry.indexOf(".") != -1){
										debugMessage("    skipping file with a file extension: " + jarEntry);
				            			
				            			continue;
				            		}
					            	// The jarEntry might appear as follows: /com/example/schema/example.dms
					            	// AND NOTE THAT THE FILE SEPERATORS ARE FORWARD SLASHES, NOT SYSTEM DEPENDENT!!!
					            	lastSlash = jarEntry.lastIndexOf("/");
					            	String schemaName = jarEntry.substring(lastSlash+1);
					            	String path = jarEntry.substring(0,lastSlash);
				            		
									debugMessage("    jarEntry contains MIB folder name: " + folder + "  -  " + jarEntry);
									
						            MibLocation newLocation = new MibLocation(f, schemaName, path);
									
									addConfig(newLocation);
									
									if (newLocation.getConfigName().length() > longest)
										longest = newLocation.getConfigName().length();
				            	}
				            	
//					            if (jarEntry.endsWith(folder)){
//					            	// The jarEntry might appear as follows: /com/example/schema/example.dms
//					            	// AND NOTE THAT THE FILE SEPERATORS ARE FORWARD SLASHES, NOT SYSTEM DEPENDENT!!!
//					            	lastSlash = jarEntry.lastIndexOf("/");
//					            	String schemaName = jarEntry.substring(lastSlash+1);
//					            	String path = jarEntry.substring(0,lastSlash);
//					            	
//									debugMessage("    jarEntry ends with suffix " + jarEntry);
////									DebugInfo.debug(f);
////						            DebugInfo.debug(jarEntry);
//
//						            MibLocation newLocation = new MibLocation(f, schemaName, path, folder);
//									
//									addConfig(newLocation);
//									
//									if (newLocation.getConfigName().length() > longest)
//										longest = newLocation.getConfigName().length();
//					            }
				            }
				        }
					}
				}
			}
			else if (f.endsWith(File.separator + "bin")){
				// NOTE: we no longer add this stuff because we're generally going to run as a tool out of a jar
				// and need to be explicitly told where to look for config files. Leaving this stuff in could
				// cause problems with clashing configs.
				
//				// We may have a project's bin directory here, which would mean that
//				// the src should be in a parallel directory
//				int lastSlash = f.lastIndexOf(File.separatorChar);
//				String prefix = f.substring(0,lastSlash);
//				
//				File src = new File(prefix + File.separator + "src");
//				
//				if (src.exists()){
//					findConfigsRecursive(src);
//				}
			}
			
		}
		
	}
}
