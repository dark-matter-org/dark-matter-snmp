package org.dmd.snmp.parser;

public class MibObjectType extends MibDefinition {

	static String defTypeName = "OBJECT-TYPE";
	
	MibSyntax		syntax;
	MaxAccessEnum	maxAccess;

	public MibObjectType(MibOID moi){
		super(moi);
	}
	
	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}
	
	public void setSyntax(MibSyntax s){
		syntax = s;
	}

	public void setMaxAccess(MaxAccessEnum ma){
		maxAccess = ma;
	}
}
