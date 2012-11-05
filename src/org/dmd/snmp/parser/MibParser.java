package org.dmd.snmp.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;


import org.dmd.util.exceptions.DebugInfo;
import org.dmd.util.parsing.Classifier;
import org.dmd.util.parsing.Token;
import org.dmd.util.parsing.TokenArrayList;


public class MibParser {
	
	final static String COMMENT = "--";
	
	final static String ASSIGNMENT_STR = "::=";
	final static int ASSIGNMENT_ID = Token.CUSTOM + 1;
	
	final static String COMMA	= ",";
	final static int COMMA_ID = Token.CUSTOM + 2;
	
	final static String FROM_STR = "FROM";
	final static int FROM_ID = Token.CUSTOM + 3;
	
	final static String SEMI_COLON	= ";";
	final static int SEMI_COLON_ID = Token.CUSTOM + 4;
	
	final static String OBJECT_IDENTIFIER_STR = "OBJECT IDENTIFIER";
	
	final static String OBJECT_IDENTITY_STR = "OBJECT-IDENTITY";
	
	final static String OBJECT_TYPE_STR = "OBJECT-TYPE";
	
	final static String IMPORTS_STR = "IMPORTS";
	
	Classifier	classifier;
	
	Classifier	commaClassifier;
	
	public MibParser(){
		classifier = new Classifier();
		classifier.addKeyword(ASSIGNMENT_STR, ASSIGNMENT_ID);
		
		commaClassifier = new Classifier();
		commaClassifier.addKeyword(FROM_STR, FROM_ID);
		commaClassifier.addSeparator(COMMA, COMMA_ID);
		commaClassifier.addSeparator(SEMI_COLON, SEMI_COLON_ID);
	}
	

	public void parseMib(String fn){
        LineNumberReader	in			= null;
        try {
			in = new LineNumberReader(new FileReader(fn));
			
            String rawInput;
            while ((rawInput = in.readLine()) != null) {
            	String line = preProcessLine(rawInput);
        		System.out.println(rawInput);
            	
            	if (line.length() == 0){
            		System.out.println("Skipping: " + rawInput);
            		continue;
            	}
            	
            	if (rawInput.length() != line.length())
            		System.out.println("Adjusted: " + line);
                
            	if (line.contains(OBJECT_IDENTIFIER_STR)){
            		parseObjectIdentifier(line);
            	}
            	else if (line.contains(OBJECT_IDENTITY_STR)){
            		
            	}
            	else if (line.contains(ASSIGNMENT_STR)){
                               	
                }
                else if (line.contains(IMPORTS_STR)){
                	parseImports(in);
                	
                	// Once we've parsed the 
                }
            }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void parseAssignment(String str){
		TokenArrayList tokens = classifier.classify(str, false);
		
		
	}
	
	/**
	 * Called when we encounter the IMPORTS string and parses until we hit a blank line.
	 * Note: we expect that the IMPORTS statement is on a line by itself.
	 * @param in the reader.
	 * @param str the line where we encountered the IMPORTS statement.
	 * @throws IOException  
	 */
	void parseImports(LineNumberReader in) throws IOException {
        String				rawInput;
        ArrayList<String>	symbols = new ArrayList();
        
        while ((rawInput = in.readLine()) != null) {
        	String line = preProcessLine(rawInput);
        	if (line.length() == 0)
        		break;
        	
        	TokenArrayList syms = commaClassifier.classify(line, false);
        	for(int i=0; i<syms.size(); i++){
        		if (syms.nth(i).getType() == FROM_ID){
        			// The FROM will be followed by the MIB where the symbols are defined
        	        MibImport mibImport = new MibImport(syms.nth(i+1).getValue(), symbols);
        	        DebugInfo.debug(mibImport.toString());
        	        symbols = new ArrayList();        	        
        			break;
        		}
        		else
        			symbols.add(syms.nth(i).getValue());
        	}
        }
        
	}
	
	/**
	 * Parses lines with OBJECT IDENTIFIER
	 * @param in
	 */
	void parseObjectIdentifier(String line){
		
	}
	
	/**
	 * Parses OBJECT-IDENTITY sections
	 * @param in the input file reader
	 * @param first the first line of identity definition
	 */
	void parseObjectIdentity(LineNumberReader in, String first){
		TokenArrayList tokens = commaClassifier.classify(first, false);
		
		
	}
	
	/**
	 * Processes the line to remove comments.
	 * @param in
	 * @return A string with the comments removed - an empty String
	 */
	String preProcessLine(String in){
		String rc = in;
		int commentPos = in.indexOf(COMMENT);
		if (commentPos != -1)
			rc = in.substring(0,commentPos);
		return(rc.trim());
	}
	
}
