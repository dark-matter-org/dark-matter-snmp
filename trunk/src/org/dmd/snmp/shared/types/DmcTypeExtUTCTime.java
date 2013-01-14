package org.dmd.snmp.shared.types;

import org.dmd.dmc.DmcAttribute;
import org.dmd.dmc.DmcAttributeInfo;
import org.dmd.dmc.DmcInputStreamIF;
import org.dmd.dmc.DmcOutputStreamIF;
import org.dmd.dmc.DmcValueException;

@SuppressWarnings("serial")
public class DmcTypeExtUTCTime extends DmcAttribute<ExtUTCTime> {
	
	public DmcTypeExtUTCTime(){
		
	}
	
	public DmcTypeExtUTCTime(DmcAttributeInfo ai){
		super(ai);
	}

	@Override
	protected ExtUTCTime typeCheck(Object value) throws DmcValueException {
		ExtUTCTime rc = null;
		
		if (value instanceof String){
			rc = new ExtUTCTime((String)value);
		}
		else{
            throw(new DmcValueException("Object of class: " + value.getClass().getName() + " passed where object compatible with ExtUTCTime expected."));
		}
		
		return(rc);
	}

	@Override
	protected ExtUTCTime cloneValue(ExtUTCTime original) {
		return(new ExtUTCTime(original));
	}

	@Override
	public void serializeValue(DmcOutputStreamIF dos, ExtUTCTime value) throws Exception {
		value.serializeIt(dos);
	}

	@Override
	public ExtUTCTime deserializeValue(DmcInputStreamIF dis) throws Exception {
		ExtUTCTime rc = new ExtUTCTime();
		rc.deserializeIt(dis);
		return(rc);
	}

	@Override
	protected DmcAttribute<?> getNew() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DmcAttribute<ExtUTCTime> cloneIt() {
		// TODO Auto-generated method stub
		return null;
	}

}
