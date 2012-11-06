package org.dmd.snmp.parser;

/**
 * The MibOID class is used to represent the hierarchical name of a MibDefinition,
 * otherwise known as an OID.
 */
public class MibOID extends MibDefinitionName {
	
	public static int 		isoID = 1;
	public static String 	isoName = "iso";

	// The string name of the parent OID
	String 					parentName;
	MibOID					parentOID;
	
	// The ID at this level
	Integer 				id;
	
	// The entire OID string of dot separated integers. This is only available
	// if this identifier has been resolved.
	String 					fullID;
	
	/**
	 * Constructs the root ISO OID.
	 */
	public MibOID(){
		super(isoName);
		parentName	= null;
		parentOID	= null;
		id			= isoID;
		fullID		= "" + isoID;
	}
	
	public MibOID(String p, String n, int i){
		super(n);
		parentName 	= p;
		parentOID	= null;
		id			= i;
		fullID		= null;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		String def = "Object";
		if (definition != null)
			def = definition.getDefinitionTypeName();
		
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
