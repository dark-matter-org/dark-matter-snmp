package org.dmd.snmp.parser;

import java.util.TreeMap;

public class MibSyntax {
	
	String name;
	
	Boolean	isSequence;
	
	// If the syntax has an associated size, this is true
	Boolean isSized;
	
	// If the syntax has a SIZE or a range, these are the start and end values
	Integer start;
	Integer end;
	
	// If the syntax has an associated range of values, this is it
	Integer range;
	
	TreeMap<Integer,String>	enumValues;

	
	public MibSyntax(String n){
		name		= n;
		isSequence 	= null;
		isSized		= null;
		start		= null;
		end			= null;
	}
	
	public void isSized(boolean f){
		isSized = f;
	}
	
	public boolean isSized(){
		if (isSized == null)
			return(false);
		return(isSized);
	}
	
	public boolean isEnumerated(){
		if (enumValues == null)
			return(false);
		return(true);
	}
	
	public void addEnumValue(String name, Integer value){
		if (enumValues == null)
			enumValues = new TreeMap<Integer, String>();
		
		enumValues.put(value, name);
	}
	
	public TreeMap<Integer,String> getEnumValues(){
		return(enumValues);
	}
	
	public void setStart(int s){
		start = s;
	}
	
	public void setEnd(int e){
		end = e;
	}
	
	public void isSequence(boolean f){
		if (isSequence != null)
			throw(new IllegalStateException("Can only set sequence flag once."));
		
		isSequence = f;
	}
	
	public boolean isSquence(){
		if (isSequence == null)
			return(false);
		return(isSequence);
	}
	
	/**
	 * 
	 * SYNTAX RowStatus
	 * SYNTAX INTEGER {
	 * SYNTAX INTEGER { notInUse(1), inUse(2) }
	 * SYNTAX OBJECT IDENTIFIER
	 * SYNTAX INTEGER (0..127) 
	 * SYNTAX DisplayString (SIZE (0..12))
	 * SYNTAX OCTET STRING (SIZE (0..100))
	 * SYNTAX SEQUENCE OF IfEntry

	 */
	public String toString(){
		StringBuffer
		sb = new StringBuffer();
		
		sb.append("MIB SYNTAX ");
		
		if (isSquence())
			sb.append("SEQUENCE OF ");
		
		sb.append(name + " ");
		
		if ( (isSized != null) && isSized){
			if (end == null)
				sb.append("(SIZE (" + start + "))");
			else
				sb.append("(SIZE (" + start + ".." + end + "))");
		}
		else{
			if (start != null)
				sb.append("(" + start + ".." + end + ")");
		}
		
		if (enumValues != null){
			sb.append("{ ");
			boolean first = true;
			for(Integer value: enumValues.keySet()){
				if (!first)
					sb.append(", ");
				
				sb.append(enumValues.get(value) + "(" + value + ")");
				first = false;
			}
			sb.append(" }");
		}
		
		return(sb.toString());
	}
	

}
