package org.dmd.snmp.parser;

/**
 * The MibDefinitionName class provides a common base for identifying things have been
 * defined in a MIB. Although Object Identifiers (OIDs) are the most common way of
 * identifying things defined in a MIB, there are other definitions that don't conform
 * to the hierarchic structure of OIDs e.g. MACROs, SEQUENCEs etc. So MibDefinitionName
 * provides a fall back, string based name for definitions in a MIB.
 */
public class MibDefinitionName {

	// The definition name
	String 			name;
	
	// The definition with which this name is associated
	MibDefinition	definition;
	
	public MibDefinitionName(String n){
		name		= n;
		definition	= null;
	}
	
	/**
	 * @return the name of definition.
	 */
	public String getName(){
		return(name);
	}
	
	/**
	 * Sets the definition with which this name is associated. 
	 * @param mb
	 */
	void setDefintion(MibDefinition mb){
		if (definition != null)
			throw(new IllegalStateException("The definition associated with a MibDefinitionName can only be set once!"));
		
		definition = mb;
	}
	
	public String toString(){
		if (definition == null)
			return("Definition: " + name);
		else
			return(definition.getDefinitionTypeName() + ": " + name);
	}

}
