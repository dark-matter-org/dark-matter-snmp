package org.dmd.snmp.parser.doc.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.dmd.snmp.parser.MibDefinition;
import org.dmd.snmp.parser.MibImport;
import org.dmd.snmp.parser.MibManager;
import org.dmd.snmp.parser.MibModule;
import org.dmd.snmp.parser.MibModuleIdentity;
import org.dmd.snmp.parser.MibOID;

/**
 * The MibPageFormatter formats the information associated with a MIB module
 * as a navigable web page.
 */
public class MibPageFormatter {

	static public void dumpMibPage(String outdir, MibManager mm, MibModule module) throws IOException {
		String ofn = outdir + File.separator + module.getName() + ".html";
		BufferedWriter out = new BufferedWriter( new FileWriter(ofn) );
		
		StandardParts.writePageHeader(out, "The " + module.getName() + " MIB");
		
		StandardParts.writeContentStart(out);

		out.write("<div class=\"mibName\"> MIB: " + module.getName() + "</div>");
		
		// Dump the module identity
		
		dumpImports(out, module);
		
		dumpDefinitionSummary(out, module);
		
		dumpDefinitionDetails(out, module);
		
		StandardParts.writeContentEnd(out);
		
		Summarizer.writeSidebar(out, mm);
		
		StandardParts.writePageFooter(out);
		
		out.close();
	}
	
	static void dumpDefinitionDetails(BufferedWriter out, MibModule module) throws IOException {
		out.write("<div class=\"definitionDetailsSection\">\n\n");
		
		out.write("<h2> Definition Details </h2>\n\n");
		
		Iterator<MibDefinition> defs = module.getDefinitions();
		while(defs.hasNext()){
			MibDefinition def = defs.next();
			
			if (def instanceof MibModuleIdentity)
				continue;
			
			out.write("<table>\n\n");

			out.write("<tr> <td class=\"definitionName\" colspan=\"4\">\n");
			out.write("  <a name=\"" + def.getDefinitionName().getName() + "\"> ");
			out.write(def.getDefinitionName().getName());
			out.write(" </a> " );
			out.write("</td> </tr>");
			
			out.write("</table>\n\n");

		}
		
		out.write("</div>\n\n");

	}
	
	static void dumpDefinitionSummary(BufferedWriter out, MibModule module) throws IOException {
		
		out.write("<div class=\"definitionSummarySection\">\n\n");
		
		out.write("<h2> Definition Summary </h2>\n\n");
		
		out.write("<table>\n\n");
		
		Iterator<MibDefinition> defs = module.getDefinitions();
		while(defs.hasNext()){
			MibDefinition def = defs.next();
			
			if (def instanceof MibModuleIdentity)
				continue;
			
			out.write("<tr>\n");
			
			out.write("<td class=\"mibDefSummaryName\">\n");
			out.write("<a class=\"deflink\" href=\"#" + def.getDefinitionName().getName() + "\"> ");
			out.write(def.getDefinitionName().getName());
			out.write(" </a> " );
			out.write("</td>\n");

			out.write("<td class=\"mibDefTypeSummaryName\">\n");
			out.write(def.getDefinitionTypeName());
			out.write("</td>\n");

			if (def.getDefinitionName() instanceof MibOID){
				out.write("<td class=\"mibDefSummaryID\">\n");
				MibOID oid = (MibOID) def.getDefinitionName();
				out.write(oid.getFullID());
				out.write("</td>\n");	
			}
			
			out.write("</tr>\n");			
		}
		
		out.write("</table>\n\n");

		out.write("</div>\n\n");
	}
	
	static void dumpImports(BufferedWriter out, MibModule module) throws IOException {
		Iterator<MibImport> imports = module.getImports();
		
		if (imports == null)
			return;
		
		out.write("<div class=\"importSection\">\n\n");
		
		out.write("<h2> Imports </h2>\n\n");

		out.write("<table>\n\n");
		
		while(imports.hasNext()){
			MibImport mi = imports.next();
			out.write("<tr>\n");
			
			out.write("<td class=\"mibImportName\">\n");
			out.write("<a class=\"navLink\" href=\"" + mi.getMibName() + ".html\">");
			out.write(mi.getMibName() + "\n");
			out.write("</a>\n");
			out.write("</td>\n");
			
			out.write("<td class=\"mibSymbolName\">\n");
			Iterator<String> symbols = mi.getSymbols();
			while(symbols.hasNext()){
				String symbol = symbols.next();
				out.write("<a class=\"navLink\" href=\"" + mi.getMibName() + ".html#" + symbol + "\">");
				out.write(symbol + " \n");
				out.write("</a>\n");
			}
			out.write("</td>\n");
			
			out.write("</tr>\n");
		}
		
		out.write("</table>\n\n");

		out.write("</div>\n\n");
	}
}
