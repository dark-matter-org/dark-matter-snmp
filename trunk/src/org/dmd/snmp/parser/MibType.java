package org.dmd.snmp.parser;

/**
 * The MibType class is used to represent types that can be referred to in a SYNTAX statement.
 * Types are defined via the assignment operator ::= and are also defined as TEXTUAL-CONVENTIONs
 * (which are derived from this class).
 */
public class MibType extends MibDefinition {
	
	// NOTE: this isn't a real SNMP concept, it just makes sense to indicate
	// the type of something
	final static String defTypeName = "TYPE";
	
	// Used to flag one of the implicit types, INTEGER, OCTET STRING of OBJECT IDENTIFIER
	boolean implicitType;
	
	// The name of the base type
	String baseTypeName;
	
	// Types can be based on other types, except for the implict types. This
	// will only be set is we've resolved the base type name
	MibType baseType;

	public MibType(MibDefinitionName mdn, boolean f) {
		super(mdn);
		if (f == false)
			throw(new IllegalStateException("This constructor is only for implicit types!"));
		implicitType 	= f;
		baseTypeName	= null;
		baseType 		= null;
	}
	
	public MibType(MibDefinitionName mdn) {
		super(mdn);
		implicitType	= false;
		baseTypeName	= null;
		baseType		= null;
	}
	
	public String getBaseTypeName(){
		return(baseTypeName);
	}
	
	public void setBaseTypeName(String btn){
		if (baseTypeName != null)
			throw(new IllegalStateException("The base type name can only be set once!"));
		baseTypeName = btn;
	}
	
	public MibType getBaseType(){
		return(baseType);
	}

	public void setBaseType(MibType mt){
		if (baseType != null)
			throw(new IllegalStateException("The base type can only be set once!"));
		baseType = mt;
	}

	public boolean isImplicitType(){
		return(implicitType);
	}

	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}
	

}
