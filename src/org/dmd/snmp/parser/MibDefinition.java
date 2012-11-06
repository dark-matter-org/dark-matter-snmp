package org.dmd.snmp.parser;

/**
 * The MibDefinition class is an abstract class that provides a common base for
 * all definitions read from a MIB definition file.
 */
abstract public class MibDefinition {

	// All definitions have an identifier. The identifier will have a reference
	// back to its definition.
	MibDefinitionName		dname;
	
	// The module where this definition came from
	MibModule	module;
	
//	protected MibDefinition(){
//		dname = null;
//	}
	
	protected MibDefinition(MibOID moi){
		dname = moi;
		moi.setDefintion(this);
	}
	
	protected MibDefinition(MibDefinitionName mdn){
		dname = mdn;
		mdn.setDefintion(this);
	}
	
	void setModule(MibModule mm){
		if (module != null)
			throw(new IllegalStateException("The module associated with a MibDefinition should only be set once!"));
		module = mm;
	}
	
	abstract public String getDefinitionTypeName();
	
}
