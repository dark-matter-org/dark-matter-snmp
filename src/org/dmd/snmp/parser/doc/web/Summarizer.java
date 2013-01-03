package org.dmd.snmp.parser.doc.web;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import org.dmd.snmp.parser.MibManager;
import org.dmd.snmp.parser.MibModule;
import org.dmd.util.exceptions.DebugInfo;

public class Summarizer {

	static public void writeSidebar(BufferedWriter out, MibManager mm) throws IOException {
		Iterator<MibModule> modules = mm.getModules();
		
		out.write("<!-- " + DebugInfo.getWhereWeAreNow() + " -->\n");
		out.write("    <div id=\"sidebar\">\n");
		
		while(modules.hasNext()){
			MibModule module = modules.next();
			out.write("        <a class=\"navLink\" href=\"" + module.getName() + ".html\"> " + module.getName() + "</a>\n");
		}
		
		out.write("    </div>\n");
	}
}
