package org.dmd.snmp.parser;

/**
 * The MibObjectidentifier class is used to represent object identifier definitions
 * <p/>
 * Example: alarmObjects OBJECT IDENTIFIER ::= { alarmMIB 1 }
 */
public class MibObjectIdentifier extends MibDefinition {

	static String defTypeName = "OBJECT IDENTIFIER";

	public MibObjectIdentifier(MibOID moi){
		super(moi);
	}
	
	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}

}
