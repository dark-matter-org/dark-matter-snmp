package org.dmd.snmp.parser;

import java.util.TreeMap;

/**
 * The MibModule class is used to organize a collection of MibDefinitions read
 * from a single MIB file.
 */
public class MibModule {

	String name;
	
	TreeMap<String,MibImport>	imports;
	MibModuleIdentity			identity;
	
	public MibModule(String n){
		name = n;
		imports = new TreeMap<String, MibImport>();
	}
	
	public void addImport(MibImport i){
		imports.put(i.getMibName(), i);
	}
	
	public void setModuleIdentity(MibModuleIdentity mmi){
		identity = mmi;
	}
	
	/**
	 * @return the name of the MIB module file.
	 */
	public String getName(){
		return(name);
	}
	
	/**
	 * Adds the definition to this module and sets this module as the source of
	 * the definition.
	 * @param md the definition to be added.
	 */
	public void addDefinition(MibDefinition md){
		md.setModule(this);
		
		if (md instanceof MibModuleIdentity)
			identity = (MibModuleIdentity) md;
	}
}
