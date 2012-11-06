package org.dmd.snmp.parser;

import java.util.TreeMap;

/**
 * The MibManager class manages definitions read from MIB files and
 * organizes them for convenient access.
 */
public class MibManager {
	
	// The ISO root OID is never explicitly defined, so we create it.
	static MibOID isoOID = new MibOID();
	
	TreeMap<String,MibOID>	oids;
	
	TreeMap<String,MibModule>			modules;
	
	public MibManager(){
		oids 	= new TreeMap<String, MibOID>();
		oids.put(isoOID.getName(), isoOID);
		
		modules = new TreeMap<String, MibModule>();
	}

	public void addIdentifier(MibOID moi){
		MibOID existing = oids.get(moi.getName());
		
		if (existing != null){
			
		}
	}
	
	public void addModule(MibModule mm){
		modules.put(mm.getName(), mm);
	}
	
	public boolean hasModule(String mn){
		if (modules.get(mn) == null)
			return(false);
		return(true);
	}
	
	
}
