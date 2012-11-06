package org.dmd.snmp.parser;

public class MibObjectType extends MibDefinition {

	static String defTypeName = "OBJECT-TYPE";

	public MibObjectType(MibOID moi){
		super(moi);
	}
	
	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}

}
