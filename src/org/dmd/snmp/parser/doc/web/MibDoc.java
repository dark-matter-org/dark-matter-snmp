package org.dmd.snmp.parser.doc.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.dmd.snmp.parser.MibManager;
import org.dmd.snmp.parser.MibModule;
import org.dmd.util.BooleanVar;
import org.dmd.util.exceptions.DebugInfo;
import org.dmd.util.parsing.CommandLine;

public class MibDoc {

	CommandLine		cl;
	StringBuffer  	help;
	BooleanVar		helpFlag	= new BooleanVar();
	StringBuffer	workspace	= new StringBuffer();
	StringBuffer	docdir		= new StringBuffer();
	
	

	public void dumpDocumentation(String outdir, MibManager mm) throws IOException{
		initDirs(outdir);
		
		URL url = this.getClass().getResource("mibstyle.css");
		DebugInfo.debug("url: " + url.getFile());
		FileUtils.copyURLToFile(url, new File(outdir + File.separator + "mibstyle.css"));
		
		Iterator<MibModule> modules = mm.getModules();
		while(modules.hasNext()){
			MibModule module = modules.next();
			MibPageFormatter.dumpMibPage(outdir, mm, module);
		}
		
	}
	
	void initDirs(String outdir){
		File mobdocdir = new File(outdir);
		if (!mobdocdir.exists())
			mobdocdir.mkdir();
	}

}
