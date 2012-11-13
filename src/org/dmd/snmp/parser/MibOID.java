package org.dmd.snmp.parser;

/**
 * The MibOID class is used to represent the hierarchical name of a MibDefinition,
 * otherwise known as an OID.
 */
public class MibOID extends MibDefinitionName {
	
	public final static int 	isoID 	= 1;
	public final static String 	isoName = "iso";

	// The string name of the parent OID
	String 					parentName;
	
	// The parent's OID - this will only be set if we've resolved it via the MibManager
	MibOID					parentOID;
	
	// The ID at this level
	Integer 				id;
	
	// The entire OID string of dot separated integers. This is only available
	// if this identifier has been resolved.
	String 					fullID;
	
	// The entire OID string of dot separated strings. This is only available
	// if this identifier has been resolved.
	String					fullStringID;
	
	/**
	 * Constructs the root ISO OID.
	 */
	public MibOID(){
		super(isoName);
		parentName		= null;
		parentOID		= null;
		id				= isoID;
		fullID			= "." + isoID;
		fullStringID	= ".iso";
	}
	
	/**
	 * Constructs the NULL OID.
	 * @param n
	 * @param i
	 */
	public MibOID(int i){
		super("0");
		parentName		= null;
		parentOID		= null;
		id				= i;
		fullID			= "." + id;
		fullStringID	= ".0";
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
		
		String moduleName = "META";
		
		if (definition != null)
			moduleName = definition.getModule().getName();
		
		if (fullID == null){
			sb.append(def + ": " + name + "(" + id + ")" + " defined in: " + moduleName + "   Parent: " + parentName);
		}
		else{
			sb.append(def + ": " + name + "(" + fullID + ")" + " defined in: " + moduleName + "   Parent: " + parentName);
		}
		
		
		return(sb.toString());
	}
	
	public MibOID getParentOID(){
		return(parentOID);
	}
	
	public void setParentOID(MibOID p){
		if (parentOID != null)
			throw(new IllegalStateException("The parent OID of a MibOID can only be set once!"));
		parentOID = p;
	}
	
	/**
	 * @return the name of definition represented by this object identifier.
	 */
	public String getName(){
		return(name);
	}
	
	public String getParentName(){
		return(parentName);
	}
	
	public String getFullID(){
		if (fullID == null){
			if (parentOID == null)
				fullID = "" + id;
			else
				fullID = parentOID.getFullID() + "." + id;
		}
		return(fullID);
	}
	
	public String getFullStringID(){
		if (fullStringID == null){
			if (parentOID == null)
				fullStringID = "" + name;
			else
				fullStringID = parentOID.getFullStringID() + "." + name;
		}
		return(fullStringID);
	}
	
}
