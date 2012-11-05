package org.dmd.snmp.parser;

/**
 * The MibOID class is used to represent the hierarchical name of a MibDefinition,
 * otherwise known as an OID.
 */
public class MibOID {
	
	// The definition with which this OID is associated
	MibDefinition			definition;
	
	public static int 		isoID = 1;
	public static String 	isoName = "iso";

	// The string name of the parent OID
	String 					parentName;
	MibOID					parentOID;
	
	// The name of the identifier at this level
	String 					name;
	
	// The ID at this level
	Integer 				id;
	
	// The entire OID string of dot separated integers. This is only available
	// if this identifier has been resolved.
	String 					fullID;
	
	/**
	 * Constructs the root ISO OID.
	 */
	public MibOID(){
		definition	= null;
		parentName	= null;
		parentOID	= null;
		name 		= isoName;
		id			= isoID;
		fullID		= "" + isoID;
	}
	
	public MibOID(String p, String n, int i){
		definition	= null;
		parentName 	= p;
		parentOID	= null;
		name 		= n;
		id			= i;
		fullID		= null;
	}
	
	/**
	 * Sets the definition with which this identifier is associated. 
	 * @param mb
	 */
	void setDefintion(MibDefinition mb){
		if (definition != null)
			throw(new IllegalStateException("The definition associated with an OID can only be set once!"));
		
		definition = mb;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		String def = "Object";
		if (definition != null)
			def = definition.getDefinitionName();
		
		if (fullID == null){
			sb.append(def + ": " + name + "(" + id + ")" + "   Parent: " + parentName);
		}
		else{
			sb.append(def + ": " + name + "(" + fullID + ")" + "   Parent: " + parentName);
		}
		
		return(sb.toString());
	}
	
	/**
	 * @return the name of definition represented by this object identifier.
	 */
	public String getName(){
		return(name);
	}
	
	
}
