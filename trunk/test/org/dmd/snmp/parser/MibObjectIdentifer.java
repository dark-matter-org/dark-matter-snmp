package org.dmd.snmp.parser;

public class MibObjectIdentifer {
	
	public static int isoID = 1;
	public static String isoName = "iso";

	// The string name of the parent OID
	String 				parentName;
	MibObjectIdentifer	parentOID;
	
	String name;
	
	// The ID at this level
	Integer id;
	
	/**
	 * Constructs the root ISO OID.
	 */
	public MibObjectIdentifer(){
		name 	= isoName;
		id		= isoID;
	}
	
	public MibObjectIdentifer(String n, int i){
		name 	= n;
		id		= i;
	}
	
}
