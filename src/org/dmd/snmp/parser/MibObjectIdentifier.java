package org.dmd.snmp.parser;

/**
 * The MibObjectidentifier class is used to represent object identifier definitions
 * <p/>
 * Example: alarmObjects OBJECT IDENTIFIER ::= { alarmMIB 1 }
 */
public class MibObjectIdentifier extends MibDefinition {

	static String defName = "OBJECT IDENTIFIER";

	public MibObjectIdentifier(MibOID moi){
		super(moi);
		moi.setDefintion(this);
	}
	
	@Override
	public String getDefinitionName() {
		return(defName);
	}

}
