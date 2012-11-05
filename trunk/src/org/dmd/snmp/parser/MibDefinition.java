package org.dmd.snmp.parser;

/**
 * The MibDefinition class is an abstract class that provides a common base for
 * all definitions read from a MIB definition file.
 */
abstract public class MibDefinition {

	// Most definitions have an identifier. The identifier will have a reference
	// back to its definition.
	MibOID		oid;
	
	// The module where this definition came from
	MibModule	module;
	
	protected MibDefinition(){
		oid = null;
	}
	
	protected MibDefinition(MibOID moi){
		oid = moi;
	}
	
	void setModule(MibModule mm){
		if (module != null)
			throw(new IllegalStateException("The module associated with a MibDefinition should only be set once!"));
		module = mm;
	}
	
	abstract public String getDefinitionName();
	
}
