package org.dmd.snmp.parser;

import java.util.ArrayList;
import java.util.Iterator;

public class MibImport {
	
	String mibName;
	
	ArrayList<String>	symbols;
	
	public MibImport(String mn, ArrayList<String> s){
		mibName = mn;
		symbols = s;
	}
	
	public String getMibName(){
		return(mibName);
	}

	public Iterator<String> getSymbols(){
		return(symbols.iterator());
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("Imports from " + mibName + ": ");
		
		for(int i=0; i<symbols.size(); i++)
			sb.append(symbols.get(i) + " ");
		
		return(sb.toString());
	}

}
