package org.dmd.snmp.parser;

import java.io.File;
import java.io.IOException;

import org.dmd.snmp.parser.doc.web.MibDoc;
import org.dmd.util.exceptions.DebugInfo;
import org.dmd.util.exceptions.ResultException;
import org.junit.Before;
import org.junit.Test;


public class MibParserTest {
	
	static String rundir;
	
	static String workSpace;
	
	@Before
	public void initialize() throws IOException {
        File curr = new File(".");
        rundir = curr.getCanonicalPath();
		System.out.println("*** Parsing running from: " + rundir);
		
		int lastSlash = rundir.lastIndexOf(File.separator);
		workSpace = rundir.substring(0,lastSlash);
		
		System.out.println("*** Workspace = " + workSpace);
	}

	@Test
	public void parse1() throws ResultException, IOException {
		// Parse a MIB based on specification of a file location
		
		DebugInfo.debug("\n\nUNIT TEST - Parse a complete MIB including imports");

		MibParser parser = new MibParser();
		
//		parser.parseMib(rundir + "/mibs/ALARM-MIB");
		
		parser.parseMib(rundir + "/mibs/BELAIR-NBI");
		
//		parser.dumpSummary();
		
		MibDoc mibdoc = new MibDoc();
		
		mibdoc.dumpDocumentation(workSpace + "/dark-matter-snmp/test/mibdocs", parser.getMibManager());
		
//		parser.parseMib(rundir + "/mibs/DOCS-IF-MIB");
	}
	
//	@Test
//	public void parse2() throws ResultException, IOException{
//		// Parse a standard MIB from the jar
//		
//		DebugInfo.debug("\n\nUNIT TEST - Parse a MIB from JAR");
//		
//		MibParser parser = new MibParser();
//		parser.parseMib("SNMPv2-SMI");
//	}
//	
//	@Test
//	public void parse3() throws ResultException, IOException{
//		DebugInfo.debug("\n\nUNIT TEST - Parse a MIB but not its imports");
//
//		MibParser parser = new MibParser();
////		parser.parseImports(false);
//		
//		parser.parseMib("RMON2-MIB");
//		
//	}
}
