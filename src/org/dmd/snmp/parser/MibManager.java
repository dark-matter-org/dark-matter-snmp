package org.dmd.snmp.parser;

import java.util.Iterator;
import java.util.TreeMap;

import org.dmd.util.exceptions.ResultException;

/**
 * The MibManager class manages definitions read from MIB files and
 * organizes them for convenient access.
 */
public class MibManager {
	
	// The ISO root OID is never explicitly defined, so we create it.
	static MibOID isoOID = new MibOID();
	
	static MibOID nullOID = new MibOID(0);
	
	TreeMap<String,MibOID>		oids;
	
	TreeMap<String,MibModule>	modules;
	
	public MibManager(){
		oids 	= new TreeMap<String, MibOID>();
		oids.put(isoOID.getName(), isoOID);
		oids.put(nullOID.getName(), nullOID);
		
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
	
	public void resolveDefinitions() throws ResultException {
		ResultException rc = null;
		// Load all of the OIDs from the modules we have
		for(MibModule mib: modules.values()){
			Iterator<MibDefinition> it = mib.getDefinitions();
			while(it.hasNext()){
				MibDefinition md = it.next();
				if (md.getDefinitionName() instanceof MibOID){
					MibOID oid = (MibOID) md.getDefinitionName();
					oids.put(oid.getName(), oid);
				}
			}
		}
		
		// Try to recursively resolve the OID hierachy
		for(MibOID oid: oids.values()){
			try {
				resolveOID(oid);
			} catch (ResultException e) {
				if (rc == null)
					rc = e;
				else
					rc.populate(e.result);
			}
		}
		
		if (rc != null)
			throw(rc);
	}
	
	void resolveOID(MibOID oid) throws ResultException{
		if (oid.getParentOID() == null){
			if (oid == isoOID)
				return;
			
			// This is okay for the isoOID and nullOID
			if (oid.getParentName() == null)
				return;
			
			MibOID parent = oids.get(oid.getParentName());
			
			if (parent == null)
				throw(new ResultException("Couldn't find parent: " + oid.getParentName() + " for OID: " + oid.getName()));
			
			oid.setParentOID(parent);
		}
	}
}
