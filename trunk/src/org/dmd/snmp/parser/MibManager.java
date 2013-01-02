package org.dmd.snmp.parser;

import java.util.Iterator;
import java.util.TreeMap;

import org.dmd.util.exceptions.DebugInfo;
import org.dmd.util.exceptions.ResultException;

/**
 * The MibManager class manages definitions read from MIB files and
 * organizes them for convenient access.
 */
public class MibManager {
	
	final static String SNMPv2_SMI_NAME = "SNMPv2-SMI";
	
	// The ISO root OID is never explicitly defined, so we create it.
	static MibOID isoOID = new MibOID();
	
	static MibOID nullOID = new MibOID(0);
	
	TreeMap<String,MibOID>		oids;
	
	TreeMap<String,MibOID>		oidsByNumber;
	
	TreeMap<String,MibOID>		oidsByName;
	
	TreeMap<String,MibModule>	modules;
	
	// These are the statically defined base types
	TreeMap<String,MibType>		baseTypes;
	
	public MibManager(){
		oids 	= new TreeMap<String, MibOID>();
		oids.put(isoOID.getName(), isoOID);
		oids.put(nullOID.getName(), nullOID);
		
		oidsByNumber 	= new TreeMap<String, MibOID>();
		
		oidsByName 	= new TreeMap<String, MibOID>();
		
		modules = new TreeMap<String, MibModule>();
	}
	
	/**
	 * @return the modules available in this manager.
	 */
	public Iterator<MibModule>	getModules(){
		return(modules.values().iterator());
	}
	
	void initBasetypes(){
		MibType type = null;
		baseTypes = new TreeMap<String, MibType>();
		
		// These are the IMPLICIT ASN.1 types
		type = new MibType(new MibDefinitionName("INTEGER"),true);
		type.setLine(142);
		baseTypes.put(type.getDefinitionName().getName(), type);

		type = new MibType(new MibDefinitionName("OCTET STRING"),true);
		type.setLine(147);
		baseTypes.put(type.getDefinitionName().getName(), type);

		type = new MibType(new MibDefinitionName("OBJECT IDENTIFIER"),true);
		type.setLine(150);
		baseTypes.put(type.getDefinitionName().getName(), type);

	}
	
	/**
	 * Used to add the base types to the SNMPv2-SMI module when it has been read.
	 * @param module
	 */
	void addBaseTypes(MibModule module){
		
	}
	
	public void addIdentifier(MibOID moi){
		MibOID existing = oids.get(moi.getName());
		
		if (existing != null){
			
		}
	}
	
	public void addModule(MibModule mm){
		modules.put(mm.getName(), mm);
		
		if (mm.getName().equals(SNMPv2_SMI_NAME))
			addBaseTypes(mm);
	}
	
	public boolean hasModule(String mn){
		String fileName = mn.replace('\\', '/');
		int lastSlash = fileName.lastIndexOf("/");
		String moduleName = mn;
		
		if (lastSlash != -1){
			moduleName = fileName.substring(lastSlash+1);
		}
		
		if (modules.get(moduleName) == null)
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
		
//		for(MibOID oid: oids.values()){
//			System.out.println(oid.toString() + "\n" + oid.getFullStringID() + "\n");
//			
//			oidsByName.put(oid.getFullStringID(), oid);
//			oidsByNumber.put(oid.getFullID(), oid);
//		}
//		
//		System.out.println("\n\n");
//		
//		for(MibOID oid: oidsByName.values()){
//			if (oid.getDefinition() == null)
//				System.out.println(oid.getFullStringID() );
//			else
//				System.out.println(oid.getFullStringID() + "  from: " + oid.getDefinition().getModule().getName());
//		}
//		
//		System.out.println("\n\n");
//		
//		for(MibOID oid: oidsByNumber.values())
//			System.out.println(oid.getFullID());
		
		
	}
	
	public void dumpSummary(){
		for(MibOID oid: oids.values()){
			System.out.println(oid.toString() + "\n" + oid.getFullStringID() + "\n");
			
			oidsByName.put(oid.getFullStringID(), oid);
			oidsByNumber.put(oid.getFullID(), oid);
		}
	
		System.out.println("\n\n");
	
		for(MibOID oid: oidsByName.values()){
			if (oid.getDefinition() == null)
				System.out.println(oid.getFullStringID() );
			else
				System.out.println(oid.getFullStringID() + "  from: " + oid.getDefinition().getModule().getName());
		}
	
		System.out.println("\n\n");
	
		for(MibOID oid: oidsByNumber.values())
			System.out.println(oid.getFullID());
		
	}
	
	void resolveOID(MibOID oid) throws ResultException{
		if (oid.getParentOID() == null){
			if (oid == isoOID)
				return;
			
			// This is okay for the isoOID and nullOID
			if (oid.getParentName() == null)
				return;
			
			MibOID parent = oids.get(oid.getParentName());
			
			if (parent == null){
				DebugInfo.debug("Couldn't find parent for OID: " + oid.toString());
//				throw(new ResultException("Couldn't find parent for OID: " + oid.toString()));
			}
			
			oid.setParentOID(parent);
		}
	}
}
