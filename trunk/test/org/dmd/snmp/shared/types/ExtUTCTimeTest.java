package org.dmd.snmp.shared.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.dmd.dmc.DmcValueException;
import org.junit.Test;

public class ExtUTCTimeTest {

	@Test
	public void testBasic() throws DmcValueException{
		ExtUTCTime longForm = new ExtUTCTime("201301142048Z");
		
		assertEquals("Year should be 2013", "2013", longForm.getYear());
		assertEquals("Month should be 01", "01", longForm.getMonth());
		assertEquals("Day should be 14", "14", longForm.getDay());
		assertEquals("Hour should be 20", "20", longForm.getHour());
		assertEquals("Minute should be 48", "48", longForm.getMinute());
		
		
		ExtUTCTime shortForm = new ExtUTCTime("9801142048Z");
		
		assertEquals("Year should be 98", "98", shortForm.getYear());
		assertEquals("Month should be 01", "01", shortForm.getMonth());
		assertEquals("Day should be 14", "14", shortForm.getDay());
		assertEquals("Hour should be 20", "20", shortForm.getHour());
		assertEquals("Minute should be 48", "48", shortForm.getMinute());
		
		try{
			@SuppressWarnings("unused")
			ExtUTCTime utcTime = new ExtUTCTime("189801011205Z");
			assertTrue("Should have indicated that the year was out of range", false);
		} catch (DmcValueException e1) {
			System.out.println(e1.toString());
			assertTrue("Expected exception", true);
		}
		
		try{
			@SuppressWarnings("unused")
			ExtUTCTime utcTime = new ExtUTCTime("201313011205Z");
			assertTrue("Should have indicated that the month was out of range", false);
		} catch (DmcValueException e1) {
			System.out.println(e1.toString());
			assertTrue("Expected exception", true);
		}
		
		try{
			@SuppressWarnings("unused")
			ExtUTCTime utcTime = new ExtUTCTime("201307321205Z");
			assertTrue("Should have indicated that the day was out of range", false);
		} catch (DmcValueException e1) {
			System.out.println(e1.toString());
			assertTrue("Expected exception", true);
		}
		
		try{
			@SuppressWarnings("unused")
			ExtUTCTime utcTime = new ExtUTCTime("201309232505Z");
			assertTrue("Should have indicated that the hour was out of range", false);
		} catch (DmcValueException e1) {
			System.out.println(e1.toString());
			assertTrue("Expected exception", true);
		}
		
		try{
			@SuppressWarnings("unused")
			ExtUTCTime utcTime = new ExtUTCTime("201308252069Z");
			assertTrue("Should have indicated that the minute was out of range", false);
		} catch (DmcValueException e1) {
			System.out.println(e1.toString());
			assertTrue("Expected exception", true);
		}
		
		
	}
}
