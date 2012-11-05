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
	
	final static String MODULE_IDENTITY_STR = "MODULE-IDENTITY";
	
	final static String OBJECT_IDENTITY_STR = "OBJECT-IDENTITY";
	
	final static String OBJECT_TYPE_STR = "OBJECT-TYPE";
	
	final static String IMPORTS_STR = "IMPORTS";
	
	Classifier	classifier;
	
	Classifier	commaClassifier;
	
	MibManager	mibManager;
	MibModule	currentModule;
	
	public MibParser(){
		classifier = new Classifier();
		classifier.addKeyword(ASSIGNMENT_STR, ASSIGNMENT_ID);
		
		commaClassifier = new Classifier();
		commaClassifier.addKeyword(FROM_STR, FROM_ID);
		commaClassifier.addSeparator(COMMA, COMMA_ID);
		commaClassifier.addSeparator(SEMI_COLON, SEMI_COLON_ID);
	}
	
	public void parseMib(String fn){
		mibManager = new MibManager();
		
		parseMibInternal(fn);
	}

	public void parseMibInternal(String fn){
		
		if (mibManager.hasModule(fn))
			return;
		
        LineNumberReader	in			= null;
        try {
			in = new LineNumberReader(new FileReader(fn));
			
			currentModule = new MibModule(fn);
			
			mibManager.addModule(currentModule);
			
            String rawInput;
            while ((rawInput = in.readLine()) != null) {
            	String line = preProcessLine(rawInput);
        		System.out.println(rawInput);
            	
            	if (line.length() == 0){
//            		System.out.println("Skipping: " + rawInput);
            		continue;
            	}
            	
//            	if (rawInput.length() != line.length())
//            		System.out.println("Adjusted: " + line);
                
            	if (line.contains(OBJECT_IDENTIFIER_STR) && line.contains(ASSIGNMENT_STR)){
            		parseObjectIdentifier(line);
            	}
            	else if (line.contains(OBJECT_IDENTITY_STR)){
            		parseObjectIdentity(in, line);
            	}
            	else if (line.contains(MODULE_IDENTITY_STR)){
            		parseModuleIdentity(in, line);
            	}
            	else if (line.contains(OBJECT_TYPE_STR)){
            		parseObjectType(in, line);
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
		
	/**
	 * Called when we encounter the IMPORTS string and parses until we hit a blank line.
	 * Note: we expect that the IMPORTS statement is on a line by itself.
	 * @param in the reader.
	 * @param str the line where we encountered the IMPORTS statement.
	 * @throws IOException  
	 */
	void parseImports(LineNumberReader in) throws IOException {
        String				rawInput;
        ArrayList<String>	symbols = new ArrayList<String>();
        
        while ((rawInput = in.readLine()) != null) {
        	String line = preProcessLine(rawInput);
        	if (line.length() == 0)
        		break;
        	
        	TokenArrayList syms = commaClassifier.classify(line, false);
        	for(int i=0; i<syms.size(); i++){
        		if (syms.nth(i).getType() == FROM_ID){
        			// The FROM will be followed by the MIB where the symbols are defined
        	        MibImport mibImport = new MibImport(syms.nth(i+1).getValue(), symbols);
        	        currentModule.addImport(mibImport);
        	        
        	        DebugInfo.debug(mibImport.toString());
        	        symbols = new ArrayList<String>();
        			break;
        		}
        		else
        			symbols.add(syms.nth(i).getValue());
        	}
        }
        
	}
	
	/**
	 * Parses lines with OBJECT IDENTIFIER of the form:
	 * alarmObjects OBJECT IDENTIFIER ::= { alarmMIB 1 }
	 * @param in
	 */
	void parseObjectIdentifier(String line){
		TokenArrayList tokens = commaClassifier.classify(line, false);
		
		String 	name 	= tokens.nth(0).getValue();
		String 	pname	= tokens.nth(5).getValue();
		int		id 		= Integer.parseInt(tokens.nth(6).getValue());
		        
        MibOID oid = new MibOID(pname, name, id);
        
        MibObjectIdentifier identifier = new MibObjectIdentifier(oid);
        
        currentModule.addDefinition(identifier);
        
        DebugInfo.debug(oid.toString());
		
	}
	
	/**
	 * Parses OBJECT-IDENTITY sections
	 * @param in the input file reader
	 * @param first the first line of identity definition
	 * @throws IOException  
	 */
	void parseObjectIdentity(LineNumberReader in, String first) throws IOException {
		TokenArrayList tokens = commaClassifier.classify(first, false);
		
		String 	name 	= tokens.nth(0).getValue();
		String 	pname	= null;
		int		id 		= -1;
		boolean	haveAssignment 	= false;
		
        String				rawInput;
        while ((rawInput = in.readLine()) != null) {
        	String line = preProcessLine(rawInput);
        	if ( (line.length() == 0) && haveAssignment)
        		break;
        	
        	// We're looking for something like:
        	// ::= { parentName 2 }
        	if (line.contains(ASSIGNMENT_STR)){
        		tokens 	= commaClassifier.classify(line, false);
//        		DebugInfo.debug("TOKENS:\n " + tokens);
        		pname 	= tokens.nth(2).getValue();
        		id		= Integer.parseInt(tokens.nth(3).getValue());
        		haveAssignment = true;
        	}
        }
        
        MibOID oid = new MibOID(pname, name, id);
        
        MibObjectIdenity identity = new MibObjectIdenity(oid);
        
        currentModule.addDefinition(identity);
        
        DebugInfo.debug(oid.toString());
		
	}
	
	/**
	 * Parses OBJECT-TYPE sections
	 * <pre>
	 * alarmModelLastChanged  OBJECT-TYPE
	 *      SYNTAX      TimeTicks
	 *      MAX-ACCESS  read-only
	 *      STATUS      current
	 *      DESCRIPTION
	 *         "The value of sysUpTime at the time of the last
	 *         creation, deletion or modification of an entry in
	 *         the alarmModelTable.
	 *
	 *         If the number and content of entries has been unchanged
	 *         since the last re-initialization of the local network
	 *         management subsystem, then the value of this object
	 *         MUST be zero."
	 *
	 *      ::= { alarmModel 1 }
     * </pre>
	 * @param in the input file reader
	 * @param first the first line of object type definition
	 * @throws IOException  
	 */
	void parseObjectType(LineNumberReader in, String first) throws IOException {
		TokenArrayList tokens = commaClassifier.classify(first, false);
		
		String 	name 	= tokens.nth(0).getValue();
		String 	pname	= null;
		int		id 		= -1;
		boolean	haveAssignment 	= false;
		
        String				rawInput;
        while ((rawInput = in.readLine()) != null) {
        	String line = preProcessLine(rawInput);
        	if ( (line.length() == 0) && haveAssignment)
        		break;
        	
        	// We're looking for something like:
        	// ::= { parentName 2 }
        	if (line.contains(ASSIGNMENT_STR)){
        		tokens 	= commaClassifier.classify(line, false);
//        		DebugInfo.debug("TOKENS:\n " + tokens);
        		pname 	= tokens.nth(2).getValue();
        		id		= Integer.parseInt(tokens.nth(3).getValue());
        		haveAssignment = true;
        	}
        }
        
        MibOID oid = new MibOID(pname, name, id);
        
        MibObjectType objtype = new MibObjectType(oid);
        
        currentModule.addDefinition(objtype);
        
        DebugInfo.debug(oid.toString());
		
	}
	
	/**
	 * Parses MODULE-IDENTITY sections
	 * @param in the input file reader
	 * @param first the first line of identity definition
	 * @throws IOException  
	 */
	void parseModuleIdentity(LineNumberReader in, String first) throws IOException {
		TokenArrayList tokens = commaClassifier.classify(first, false);
		
		String 	name 			= tokens.nth(0).getValue();
		String 	pname			= null;
		int		id 				= -1;
		boolean	haveAssignment 	= false;
		
        String				rawInput;
        while ((rawInput = in.readLine()) != null) {
        	String line = preProcessLine(rawInput);
        	if ( (line.length() == 0) && haveAssignment)
        		break;
        	
        	// We're looking for something like:
        	// ::= { parentName 2 }
        	if (line.contains(ASSIGNMENT_STR)){
        		tokens 	= commaClassifier.classify(line, false);
//        		DebugInfo.debug("TOKENS:\n " + tokens);
        		pname 	= tokens.nth(2).getValue();
        		id		= Integer.parseInt(tokens.nth(3).getValue());
        		haveAssignment = true;
        	}
        }
        
        MibOID oid = new MibOID(pname, name, id);
        
        MibModuleIdentity mmi = new MibModuleIdentity(oid);
        
        currentModule.addDefinition(mmi);
        
        DebugInfo.debug(oid.toString());
		
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
