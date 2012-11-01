package org.dmd.snmp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibLoaderLog;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibTypeSymbol;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.MibLoaderLog.LogEntry;
import net.percederberg.mibble.snmp.SnmpTextualConvention;
import net.percederberg.mibble.type.CompoundConstraint;
import net.percederberg.mibble.type.Constraint;
import net.percederberg.mibble.type.IntegerType;
import net.percederberg.mibble.type.ValueConstraint;
import net.percederberg.mibble.value.NumberValue;
import net.percederberg.mibble.value.ObjectIdentifierValue;

import org.junit.Test;


public class ParserTest {

	@Test
	public void test() throws IOException {
		
        File curr = new File(".");
        String runDir;
		runDir = curr.getCanonicalPath();
		System.out.println("*** Parsing running from: " + runDir);
		
		File loadDir = new File(runDir + "/mibs");
//		File loadFile = new File(runDir + "/mibs/SNMPv2-SMI");
//		File loadFile = new File(runDir + "/mibs/SNMPv2-TC");
//		File loadFile = new File(runDir + "/mibs/INET-ADDRESS-MIB");
//		File loadFile = new File(runDir + "/mibs/ITU-ALARM-MIB");
		File loadFile = new File(runDir + "/mibs/IANA-ITU-ALARM-TC-MIB");

		MibLoader loader = new MibLoader();
		
//		loader.addDir(loadDir);
//		loader.addResourceDir("mibs/ietf");
		
		Mib mib = null;
		
		try {
			mib = loader.load(loadFile);
		} catch (MibLoaderException e) {
			MibLoaderLog log = e.getLog();
			Iterator<?> entries = log.entries();
			while(entries.hasNext()){
				LogEntry entry = (LogEntry) entries.next();
				System.err.println(entry.getFile().getName() + " " + entry.getLineNumber() + " " + entry.getMessage());
			}
			return;
		}
		
	    Iterator<?>   iter = mib.getAllSymbols().iterator();
	   
	    MibSymbol  symbol;
	    MibValue   value;

	    while (iter.hasNext()) {
	        symbol = (MibSymbol) iter.next();
	        System.out.println(symbol.getName());
	        value = extractOid(symbol);
	        if (value != null) {
	            System.out.println(symbol.getName() + " " + value);
	        }
	        else{
	        	if (symbol instanceof MibTypeSymbol){
	        		MibTypeSymbol mts = (MibTypeSymbol) symbol;
	        		MibType mt = mts.getType();
	        		
	        		System.out.println(mt.getClass().getName());
	        		if (mt instanceof SnmpTextualConvention){
	        			SnmpTextualConvention stc = (SnmpTextualConvention) mt;
	        			MibType syntax = stc.getSyntax();
	        			
	        			if (syntax instanceof IntegerType){
	        				IntegerType it = (IntegerType) syntax;
	        				Constraint c = it.getConstraint();
	        				
	        				MibValueSymbol[] symbols = it.getAllSymbols();
	        				if (symbols != null){
	        					for(int i=0; i<symbols.length; i++){
	        						MibValueSymbol mvs = symbols[i];
	        						MibValue st = mvs.getValue();
//	        						System.out.println(mvs.getName() );
	        						if (st instanceof NumberValue){
	        							NumberValue iv = (NumberValue) st;
		        						System.out.println(mvs.getName() + " " + iv.toString());
	        						}
	        					}
	        				}
	        				
//	    	        		System.out.println(c.getClass().getName());
//	    	        		if (c instanceof CompoundConstraint){
//		        				CompoundConstraint cc = (CompoundConstraint) c;
//		        				
//		        				ArrayList<?> ccList = cc.getConstraintList();
//		        				if (ccList != null){
//		        					System.out.println("HERE");
//		        					for(int i=0; i<ccList.size(); i++){
//		        						Object obj = ccList.get(i);
//		        			            System.out.println(obj.getClass().getName());
//		        			            if (obj instanceof ValueConstraint){
//		        			            	ValueConstraint vc = (ValueConstraint) obj;
//		        			            	System.out.println(vc.getValue().getClass().getName());
//		        			            	
//		        			            	if (vc.getValue() instanceof NumberValue){
//		        			            		NumberValue nv = (NumberValue) vc.getValue();
//		        			            		System.out.println(nv.toString());
//		        			            	}
//		        			            }
//		        					}
//		        				}
//	    	        		}
	        				
	        			}
	        		}
	        	}
	        }
	    }		
	}
	
	public ObjectIdentifierValue extractOid(MibSymbol symbol) {
	    MibValue  value;

	    if (symbol instanceof MibValueSymbol) {
	        value = ((MibValueSymbol) symbol).getValue();
	        if (value instanceof ObjectIdentifierValue) {
	            return (ObjectIdentifierValue) value;
	        }
	    }
	    return null;
	}
}
