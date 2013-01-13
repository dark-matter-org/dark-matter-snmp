package org.dmd.snmp.parser;

public class MibRevision {
	
	String date;
	String description;
	String readableDate;
	
	public MibRevision(String d){
		date = d;
		description = null;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(date.substring(0, 4) + " ");
		sb.append(date.substring(4, 6) + " ");
		sb.append(date.substring(6, 8));
		
		readableDate = sb.toString();
	}
	
	public void setDescription(String d){
		description = d;
	}
	
	public String getDate(){
		return(date);
	}
	
	public String getReadableDate(){
		return(readableDate);
	}
	
	public String getDescription(){
		return(description);
	}
	
	public String toString(){
		return(date + " " + description);
	}

}
