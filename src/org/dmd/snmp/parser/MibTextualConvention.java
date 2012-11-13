package org.dmd.snmp.parser;

import java.util.TreeMap;

/**
 * The MibTextualConvention class is used to store information about textual conventions, strangely enough.
 */
public class MibTextualConvention extends MibType {
	
	static String defTypeName = "TEXTUAL_CONVENTION";
	
	// If the convention has associated enumerated values, we store them here
	TreeMap<Integer,String>	enumValues;

	public MibTextualConvention(MibDefinitionName mdn){
		super(mdn);
	}

	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}
	
	public void addEnumValue(String name, Integer value){
		if (enumValues == null)
			enumValues = new TreeMap<Integer, String>();
		
		enumValues.put(value, name);
	}

}
