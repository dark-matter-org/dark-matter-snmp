package org.dmd.snmp.parser;

public class MibMacro extends MibDefinition {
	
	static String defTypeName = "MACRO";

	public MibMacro(MibDefinitionName mdn) {
		super(mdn);
	}

	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}

}
