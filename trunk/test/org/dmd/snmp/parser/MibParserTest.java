package org.dmd.snmp.parser;

import java.io.File;
import java.io.IOException;

import org.dmd.util.exceptions.ResultException;
import org.junit.Before;
import org.junit.Test;


public class MibParserTest {
	
	static String rundir;
	
	@Before
	public void initialize() throws IOException{
        File curr = new File(".");
        rundir = curr.getCanonicalPath();
		System.out.println("*** Parsing running from: " + rundir);
		
	}

	@Test
	public void parse1() throws ResultException {
		MibParser parser = new MibParser();
		
//		parser.parseMib(rundir + "/mibs/ALARM-MIB");
		
//		parser.parseMib(rundir + "/mibs/BELAIR-SYSTEM");
		
		parser.parseMib(rundir + "/mibs/SNMPv2-SMI");
	}
}
