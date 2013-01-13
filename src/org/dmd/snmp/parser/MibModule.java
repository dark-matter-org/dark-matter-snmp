package org.dmd.snmp.parser;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * The MibModule class is used to organize a collection of MibDefinitions read
 * from a single MIB file.
 */
public class MibModule {

	String name;
	
	TreeMap<String,MibImport>				imports;
	MibModuleIdentity						identity;
	
	TreeMap<String,MibDefinition>			definitions;
	
	// Types include both explicitly defined types and MibTextualConventions
	TreeMap<String,MibType>					types;
	
	TreeMap<String,MibTextualConvention>	textualConventions;
	
	public MibModule(String n){
		name 				= n;
		imports 			= new TreeMap<String, MibImport>();
		definitions 		= new TreeMap<String, MibDefinition>();
		types 				= new TreeMap<String, MibType>();
		textualConventions 	= new TreeMap<String, MibTextualConvention>();
	}
	
	public void addImport(MibImport i){
		imports.put(i.getMibName(), i);
	}
	
	public void setModuleIdentity(MibModuleIdentity mmi){
		identity = mmi;
		
		addDefinition(mmi);
	}
	
	public MibModuleIdentity getIdentity(){
		return(identity);
	}
	
	/**
	 * @return the name of the MIB module file.
	 */
	public String getName(){
		return(name);
	}
	
	public Iterator<MibDefinition> getDefinitions(){
		return(definitions.values().iterator());
	}
	
	public Iterator<MibImport> getImports(){
		return(imports.values().iterator());
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
		
		if (md instanceof MibType)
			types.put(md.getDefinitionName().getName(), (MibType) md);
		
		MibDefinition existing = definitions.get(md.getDefinitionName().getName());
		
		if (existing != null)
			throw(new IllegalStateException("MIB definitions with duplicate names: \n\n" + existing.toString() + "\n\n" + md.toString()));
		
		definitions.put(md.getDefinitionName().getName(), md);
	}
}
