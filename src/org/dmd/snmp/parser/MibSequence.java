package org.dmd.snmp.parser;

public class MibSequence extends MibDefinition {
	
	static String defTypeName = "SEQUENCE";

	public MibSequence(MibDefinitionName mdn) {
		super(mdn);
	}

	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}

}
