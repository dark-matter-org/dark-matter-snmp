package org.dmd.snmp.parser;

/**
 * The MibObjectIdenity class is used to store information associated with the
 * 
 * Example:
 *         someSystem OBJECT-IDENTITY
            STATUS current
            DESCRIPTION 
                "Sub-tree for some system MIB definitions."
            ::= { someSystemParent 1 }

 */
public class MibObjectIdentity extends MibDefinition {

	static String defTypeName = "OBJECT-IDENTITY";

	public MibObjectIdentity(MibOID moi){
		super(moi);
	}
	
	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}

}
