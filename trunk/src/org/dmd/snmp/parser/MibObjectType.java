package org.dmd.snmp.parser;

public class MibObjectType extends MibDefinition {

	static String defName = "OBJECT-TYPE";

	public MibObjectType(MibOID moi){
		super(moi);
		moi.setDefintion(this);
	}
	
	@Override
	public String getDefinitionName() {
		return(defName);
	}

}
