package org.dmd.snmp.parser;

/**
 * The MibNotificationType class is used to store notification definitions i.e. trap definitions.
 * @author peter
 *
 */
public class MibNotificationType extends MibDefinition {

	static String defTypeName = "NOTIFICATION-TYPE";
	
	public MibNotificationType(MibOID moi){
		super(moi);
	}

	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}

}
