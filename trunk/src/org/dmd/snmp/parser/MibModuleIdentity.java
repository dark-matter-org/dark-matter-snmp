package org.dmd.snmp.parser;

/**
 * The MibModuleIdentity class is used to store information from the MODULE-IDENTITY
 * section of a MIB module. Example:
 *   alarmMIB MODULE-IDENTITY
      LAST-UPDATED "200409090000Z"  -- September 09, 2004
      ORGANIZATION "IETF Distributed Management Working Group"
      CONTACT-INFO
           "WG EMail: disman@ietf.org
           Subscribe: disman-request@ietf.org
           http://www.ietf.org/html.charters/disman-charter.html

           Chair:     Randy Presuhn
                      randy_presuhn@mindspring.com

           Editors:   Sharon Chisholm
                      Nortel Networks
                      PO Box 3511 Station C
                      Ottawa, Ont.  K1Y 4H7
                      Canada
                      schishol@nortelnetworks.com

                      Dan Romascanu
                      Avaya
                      Atidim Technology Park, Bldg. #3
                      Tel Aviv, 61131
                      Israel
                      Tel: +972-3-645-8414
                      Email: dromasca@avaya.com"
      DESCRIPTION
           "The MIB module describes a generic solution
           to model alarms and to store the current list
           of active alarms.

           Copyright (C) The Internet Society (2004).  The
           initial version of this MIB module was published
           in RFC 3877.  For full legal notices see the RFC
           itself.  Supplementary information may be available on:
           http://www.ietf.org/copyrights/ianamib.html"
      REVISION    "200409090000Z"  -- September 09, 2004
      DESCRIPTION
          "Initial version, published as RFC 3877."
      ::= { mib-2 118 }

 *
 */
public class MibModuleIdentity extends MibDefinition {
	
	static String defTypeName = "MODULE-IDENTITY";

	public MibModuleIdentity(MibOID moi){
		super(moi);
	}

	@Override
	public String getDefinitionTypeName() {
		return(defTypeName);
	}
}
