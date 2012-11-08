package org.dmd.snmp.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.dmd.util.exceptions.DebugInfo;
import org.dmd.util.exceptions.ResultException;
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
	
	final static String MACRO_STR = "MACRO";
	
	final static String END_STR = "END";
	
	final static String SEQUENCE_STR = "SEQUENCE";
	
	final static String NOTIFICATION_TYPE_STR = "NOTIFICATION-TYPE";
	
	final static String IMPORTS_STR = "IMPORTS";
	
	Classifier	classifier;
	
	Classifier	commaClassifier;
	
	MibManager	mibManager;
	MibModule	currentModule;
	
	MibFinder	finder;
	
	public MibParser(){
		classifier = new Classifier();
		classifier.addKeyword(ASSIGNMENT_STR, ASSIGNMENT_ID);
		
		commaClassifier = new Classifier();
		commaClassifier.addKeyword(FROM_STR, FROM_ID);
		commaClassifier.addSeparator(COMMA, COMMA_ID);
		commaClassifier.addSeparator(SEMI_COLON, SEMI_COLON_ID);
		
	}
	
	public void parseMib(String fn) throws ResultException, IOException {
		mibManager = new MibManager();
		
		// Set up the finder that will find the location of base MIBs
		finder = new MibFinder();
		
//		finder.debug(true);
		finder.addMibFolder("iana");
		finder.addMibFolder("ietf");
		finder.addJarPrefix("mibble-mibs");
		
		finder.findMIBs();
		
		MibLocation location = new MibLocation(fn);
		
		parseMibInternal(location);
		
		mibManager.resolveDefinitions();
	}

	public void parseMibInternal(MibLocation loc) throws ResultException {
		MibLocation location = loc;
		
		if (mibManager.hasModule(location.getMibName()))
			return;
		
		if (!location.isFromJAR()){
			// If this isn't from a JAR but the directory is null, it means that
			// someone just specified the name of (hopefully) one of the base MIBs
			// We'll try to find that MIB as a MibLocation from our finder
			if (location.getDirectory() == null){
				location = finder.getLocation(loc.getMibName());
				
				if (location == null)
					throw(new ResultException("Couldn't find base MIB: " + loc.getMibName() + " in our MIB jar."));
			}
		}
		
        LineNumberReader	in	= null;
        
        try {
        	if (location.isFromJAR()){
    			InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream(location.getFileName()));
    			in = new LineNumberReader(isr);        		
        	}
        	else
        		in = new LineNumberReader(new FileReader(location.getFileName()));
			
			currentModule = new MibModule(location.getMibName());
			
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
                
            	if (line.contains(MACRO_STR)){
            		parseMacro(in, line);            		
            	}
            	else if (line.contains(OBJECT_IDENTIFIER_STR) && line.contains(ASSIGNMENT_STR)){
            		parseObjectIdentifier(in,line);
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
            	else if (line.contains(NOTIFICATION_TYPE_STR)){
            		parseNotificationType(in, line);
            	}
                else if (line.contains(IMPORTS_STR)){
                	parseImports(in);
                }
                else if (line.contains(ASSIGNMENT_STR)){
                	
                }
            }
            
//    		// We've finished parsing this module, now ensure that its imports
//    		// have been read as well.
//            Iterator<MibImport> imports = currentModule.getImports();
//            if (imports != null){
//            	while(imports.hasNext()){
//            		MibImport mi = imports.next();
//            		
//            	}
//            }
            
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
	 * Parses MACRO sections - for example:
	 * <pre>
	 * MODULE-IDENTITY MACRO ::=
	 * BEGIN
	 *     TYPE NOTATION ::=
	 *                   "LAST-UPDATED" value(Update ExtUTCTime)
	 *                   "ORGANIZATION" Text
	 *                   "CONTACT-INFO" Text
	 *                   "DESCRIPTION" Text
	 *                   RevisionPart
	 * 
	 *     VALUE NOTATION ::=
	 *                   value(VALUE OBJECT IDENTIFIER)
	 * 
	 *     RevisionPart ::=
	 *                   Revisions
	 *                 | empty
	 *     Revisions ::=
	 *                   Revision
	 *                 | Revisions Revision
	 *     Revision ::=
	 *                   "REVISION" value(Update ExtUTCTime)
	 *                   "DESCRIPTION" Text
	 * 
	 *     -- a character string as defined in section 3.1.1
	 *     Text ::= value(IA5String)
	 * END
	 * </pre>
	 * @param in the input file reader
	 * @param first the first line of identity definition
	 * @throws IOException  
	 */
	void parseMacro(LineNumberReader in, String first) throws IOException {
		TokenArrayList tokens = commaClassifier.classify(first, false);
		int lineNumber = in.getLineNumber();
		
		String 	name 	= tokens.nth(0).getValue();
		
        String				rawInput;
        while ((rawInput = in.readLine()) != null) {
        	if (rawInput.contains(END_STR))
        		break;
        }
        
        MibDefinitionName mdn = new MibDefinitionName(name);
        
        MibMacro macro = new MibMacro(mdn);
        macro.setLine(lineNumber);
        
        currentModule.addDefinition(macro);
        
        DebugInfo.debug(mdn.toString());
		
	}
	
	/**
	 * Parses lines with OBJECT IDENTIFIER of the form:
	 * alarmObjects OBJECT IDENTIFIER ::= { alarmMIB 1 }
	 * @param in
	 */
	void parseObjectIdentifier(LineNumberReader in, String line){
		TokenArrayList tokens = commaClassifier.classify(line, false);
		int lineNumber = in.getLineNumber();
		
		String 	name 	= tokens.nth(0).getValue();
		String 	pname	= tokens.nth(5).getValue();
		int		id 		= Integer.parseInt(tokens.nth(6).getValue());
		        
        MibOID oid = new MibOID(pname, name, id);
        
        MibObjectIdentifier identifier = new MibObjectIdentifier(oid);
        identifier.setLine(lineNumber);
        
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
		int lineNumber = in.getLineNumber();
		
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
        identity.setLine(lineNumber);
        
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
		int lineNumber = in.getLineNumber();
		
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
        objtype.setLine(lineNumber);
        
        currentModule.addDefinition(objtype);
        
        DebugInfo.debug(oid.toString());
		
	}
	
	void parseNotificationType(LineNumberReader in, String first) throws IOException {
		TokenArrayList tokens = commaClassifier.classify(first, false);
		int lineNumber = in.getLineNumber();
		
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
        
        MibNotificationType notification = new MibNotificationType(oid);
        notification.setLine(lineNumber);
        
        currentModule.addDefinition(notification);
        
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
		int lineNumber = in.getLineNumber();
		
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
        mmi.setLine(lineNumber);
        
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