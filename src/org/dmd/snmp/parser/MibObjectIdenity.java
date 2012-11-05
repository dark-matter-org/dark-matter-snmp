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
public class MibObjectIdenity extends MibDefinition {

	static String defName = "OBJECT-IDENTITY";

	public MibObjectIdenity(MibOID moi){
		super(moi);
		moi.setDefintion(this);
	}
	
	@Override
	public String getDefinitionName() {
		return(defName);
	}

}