//	---------------------------------------------------------------------------
//	dark-matter-data
//	Copyright (c) 2012 dark-matter-data committers
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


/**
 * The MibLocation simply indicates the name of a MIB file and its location on the file system
 * or in a JAR.
 */
public class MibLocation {
	
	// The name of the MIB 
	String mibName;
	
	// The full name of the directory where the MIB file exists for example:
	// mibs/ietf
	String directory;
	
	// The full name of the directory where the MIB folder resides
	String mibParentDirectory;
	
	// The full name of the MIB file, for example:
	// /mibs/ietf/VDSL-LINE-EXT-MCM-MIB
	String fileName;
		
	// FOR JAR LOCATIONS
	
	// If the MIB was found in a JAR, this is the name of the JAR, for example:
	// /Users/joe/softdev/dark-matter-snmp/extjars/mibble-mibs-2.9.3.jar
	String jarFileName;
	
	// The directory in the JAR file where the .xxx file exists, for example:
	// /com/example/schema. NOTE: the file separators are ALWAYS forward slash "/"
	String jarDirectory;
	
	// Just the jar file name. If jarFileName is: file:F:\AASoftDev\workspace\dark-matter-data\extjars\exampleDMSchema.jar
	// This will be just: exampleDMSchema.jar
	String justJarName;
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("MibLocation\n");
		sb.append("             MIB name: " + mibName + "\n");
		sb.append("            directory: " + directory + "\n");
		sb.append("configParentDirectory: " + mibParentDirectory + "\n");
		sb.append("             fileName: " + fileName + "\n");
		sb.append("          jarFileName: " + jarFileName + "\n");
		sb.append("         jarDirectory: " + jarDirectory + "\n");
		sb.append("          justJarName: " + justJarName + "\n\n");
		return(sb.toString());
	}
	
	/**
	 * Constructs a new DmsSchemaLocation.
	 * @param n   The name of the MIB file 
	 * @param dir The directory where this file was found.
	 */
	public MibLocation(String n, String dir){
		int lastSlash = -1;
		
		mibName 	= n;
		directory	= dir;
		fileName 	= directory + File.separatorChar + n;
		
		lastSlash = directory.lastIndexOf(File.separatorChar);
		
		mibParentDirectory = directory.substring(0,lastSlash);
		
		// Not used in this case
		jarFileName 	= null;
		jarDirectory	= null;
		justJarName		= null;
	}
	
	/**
	 * @return true if the config was found in a jar and false otherwise.
	 */
	public boolean isFromJAR(){
		if (jarFileName == null)
			return(false);
		return(true);
	}
	
	/**
	 * Constructs a new schema location that's located in a JAR file.
	 * @param j The JAR file name.
	 * @param n The name of the possible MIB file.
	 * @param dir The sub directory in the JAR where the schema is found.
	 */
	public MibLocation(String j, String n, String dir){
		int lastSlash = -1;

		mibName 	= n;
		directory 	= dir;
		
		lastSlash = directory.lastIndexOf("/");
		
		mibParentDirectory = directory.substring(0,lastSlash);

		jarFileName 	= j;
		jarDirectory	= dir;
		
		String 	tmp = "/" + jarDirectory + "/" + mibName;
		fileName = tmp.replace('\\', '/');
		
		lastSlash = jarFileName.lastIndexOf("/");
		justJarName = jarFileName.substring(lastSlash+1);
	}
	
	/**
	 * @return The name of the schema (i.e. the name of the .dms file without the .dms extension).
	 */
	public String getConfigName(){
		return(mibName);
	}
	
	/**
	 * @return The name of the directory where the config file resides.
	 */
	public String getDirectory(){
		return(directory);
	}
	
	/**
	 * The complete file name of this config file OR When the file is found in a JAR file,
	 * it's the URL of the file, for example <BR>
	 * "jar:file:" + jarFileName + "!/" + jarDirectory + "/" + configName + ".xxx" <BR>
	 * URL url = new URL(fileName);
	 * @return The file name.
	 */
	public String getFileName(){
		return(fileName);
	}
	
	/**
	 * @return The parent directory where the config subfolder lives.
	 */
	public String getConfigParentDirectory(){
		return(mibParentDirectory);
	}
	
	/**
	 * If the schema was in a JAR, this is the fully qualified name of the JAR.
	 * @return The JAR file name.
	 */
	public String getJarFilename(){
		return(jarFileName);
	}
	
	/**
	 * If the schema was in a JAR, this is the name of the directory within the
	 * JAR where it was found.
	 * @return The directory path segment - the file separators in this are forward slashes.
	 */
	public String getJarDirectory(){
		return(jarDirectory);
	}
	
	/**
	 * If the schema was in a JAR, this is just the name of the JAR.
	 * @return The JAR file name.
	 */
	public String getJustJarFilename(){
		return(justJarName);
	}
	
}
