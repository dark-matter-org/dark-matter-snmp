//	---------------------------------------------------------------------------
//	dark-matter-snmp
//	Copyright (c) 2013 dark-matter-snmp committers
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.dmd.snmp.shared.types;

import org.dmd.dmc.DmcInputStreamIF;
import org.dmd.dmc.DmcOutputStreamIF;
import org.dmd.dmc.DmcValueException;

/**
 * The ExtUTCTime class is used to represent values of type ExtUTCTime as defined
 * in SNMPv2-SMI.
 *    -- format is YYMMDDHHMMZ or YYYYMMDDHHMMZ
 *    --   where: YY   - last two digits of year (only years
 *    --                 between 1900-1999)
 *    --          YYYY - last four digits of the year (any year)
 *    --          MM   - month (01 through 12)
 *    --          DD   - day of month (01 through 31)
 *    --          HH   - hours (00 through 23)
 *    --          MM   - minutes (00 through 59)
 *    --          Z    - denotes GMT (the ASCII character Z)
 *    --
 *    -- For example, "9502192015Z" and "199502192015Z" represent
 *    -- 8:15pm GMT on 19 February 1995. Years after 1999 must use
 *    -- the four digit year format. Years 1900-1999 may use the
 *    -- two or four digit format.
 *
 */
public class ExtUTCTime {
	
	final static int SHORT_LENGTH = 11;
	final static int LONG_LENGTH = 13;

	String value;
	
	transient String year;
	transient String month;
	transient String day;
	transient String hour;
	transient String minute;
	
	public ExtUTCTime(){
		value = null;
	}
	
	public ExtUTCTime(ExtUTCTime obj){
		value = new String(obj.value);
	}
	
	public ExtUTCTime(String v) throws DmcValueException {
		
		if ( (v.length() != SHORT_LENGTH) && (v.length() != LONG_LENGTH) ){
			throw(new DmcValueException("ExtUTCTime must be 11 or 13 characters long. YYMMDDHHmmZ or YYYYMMDDHHmmZ"));
		}
		
		if (v.length() == SHORT_LENGTH){
			year 	= check("YY",v.substring(0, 2),0,99);
			month 	= check("MM",v.substring(2, 4),1,12);
			day 	= check("DD",v.substring(4, 6),1,31);
			hour 	= check("HH",v.substring(6, 8),0,23);
			minute 	= check("mm",v.substring(8, 10),0,59);
			if (v.charAt(10) != 'Z')
				throw(new DmcValueException("ExtUTCTime must be 11 or 13 characters long and end with Z. YYMMDDHHmmZ or YYYYMMDDHHmmZ"));
		}
		else{
			year 	= check("YYYY",v.substring(0, 4),1900,9999);
			month 	= check("MM",v.substring(4, 6),1,12);
			day 	= check("DD",v.substring(6, 8),1,31);
			hour 	= check("HH",v.substring(8, 10),0,23);
			minute 	= check("mm",v.substring(10, 12),0,59);
			if (v.charAt(12) != 'Z')
				throw(new DmcValueException("ExtUTCTime must be 11 or 13 characters long and end with Z. YYMMDDHHmmZ or YYYYMMDDHHmmZ"));
		}
	}
	
	/**
	 * Validates the specified field base on the range specified.
	 * @param name the name of the field being checked
	 * @param subval the value string that should be an integer
	 * @param start the range start
	 * @param end the range end
	 * @return the String if it was okay.
	 * @throws DmcValueException
	 */
	String check(String name, String subval, int start, int end) throws DmcValueException {
		
		try{
			int val = Integer.parseInt(subval);
			
			if ( (val<start) || (val>end))
				throw(new DmcValueException("The " + name + " must be in the range: " + start + " to " + end + " in an ExtUTCTime value. It was: " + subval));
		}
		catch(NumberFormatException ex){
			throw(new DmcValueException("The " + name + " must be an integer in the range: " + start + " to " + end + " in an ExtUTCTime value. It was: " + subval));
		}
		
		return(subval);
	}
	
	public String getValue() {
		return value;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return month;
	}

	public String getDay() {
		return day;
	}

	public String getHour() {
		return hour;
	}

	public String getMinute() {
		return minute;
	}
	
	public String toString(){
		return(value);
	}
	
	/**
	 * @return just the YYYY MM DD form of the date.
	 */
	public String getReadableDate(){
		return(year + " " + month + " " + day);
	}
	
	public void serializeIt(DmcOutputStreamIF dos) throws Exception {
		dos.writeUTF(value);
	}
	
	public void deserializeIt(DmcInputStreamIF dis) throws Exception {
		value = dis.readUTF();
	}
}
