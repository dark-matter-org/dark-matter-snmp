package org.dmd.snmp.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class MibImport {
	
	String mibName;
	
	TreeSet<String>	symbolsSorted;
	ArrayList<String> symbols;
	
	public MibImport(String mn, ArrayList<String> s){
		mibName = mn;
		symbolsSorted = new TreeSet<String>();
		for(String symbol: s){
			symbolsSorted.add(symbol);
		}
		symbols = s;
	}
	
	public String getMibName(){
		return(mibName);
	}

	public Iterator<String> getSymbols(){
		return(symbolsSorted.iterator());
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("Imports from " + mibName + ": ");
		
		for(int i=0; i<symbols.size(); i++)
			sb.append(symbols.get(i) + " ");
		
		return(sb.toString());
	}

}
