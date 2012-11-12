package org.dmd.snmp.parser;

/**
 * The MibTextualConvention class is used to store information about textual conventions, strangely enough.
 */
public class MibTextualConvention extends MibDefinition {
	
	static String defTypeName = "TEXTUAL_CONVENTION";

	public MibTextualConvention(MibDefinitionName mdn){
		super(mdn);
	}

	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}

}
