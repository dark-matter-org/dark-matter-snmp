package org.dmd.snmp.parser;

import java.io.IOException;

import org.dmd.util.exceptions.ResultException;
import org.junit.Test;

public class MibFinderTest {

	@Test
	public void findMIBsInJar() throws ResultException, IOException {
		MibFinder finder = new MibFinder();
		
		finder.debug(true);
		finder.addMibFolder("iana");
		finder.addMibFolder("ietf");
		finder.addJarPrefix("mibble-mibs");
		
		finder.findMIBs();
	}
}
