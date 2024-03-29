package org.dmd.snmp.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.dmd.dmc.types.CheapSplitter;
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
	
	final static String LEFT_CURLY	= "{";
	final static int LEFT_CURLY_ID = Token.CUSTOM + 5;
	
	final static String RIGHT_CURLY	= "}";
	final static int RIGHT_CURLY_ID = Token.CUSTOM + 6;
	
	final static String LEFT_ROUND	= "(";
	final static int LEFT_ROUND_ID = Token.CUSTOM + 7;
	
	final static String RIGHT_ROUND	= ")";
	final static int RIGHT_ROUND_ID = Token.CUSTOM + 8;
	
	final static String PERIOD	= ".";
	final static int PERIOD_ID = Token.CUSTOM + 9;
	
	final static String OBJECT_IDENTIFIER_STR = "OBJECT IDENTIFIER";
	
	final static String MODULE_IDENTITY_STR = "MODULE-IDENTITY";
	
	final static String OBJECT_IDENTITY_STR = "OBJECT-IDENTITY";
	
	final static String OBJECT_TYPE_STR = "OBJECT-TYPE";
	
	final static String CONTACT_INFO_STR = "CONTACT-INFO";
	
	final static String ORGANIZATION_STR = "ORGANIZATION";
	
	final static String TEXTUAL_CONVENTION_STR = "TEXTUAL-CONVENTION";
	
	final static String MACRO_STR = "MACRO";
	
	final static String END_STR = "END";
	
	final static String SIZE_STR = "SIZE";
	
	final static String SEQUENCE_OF_STR = "SEQUENCE OF";
	
	final static String SYNTAX_STR = "SYNTAX";
	
	final static String NOTIFICATION_TYPE_STR = "NOTIFICATION-TYPE";
	
	final static String MAX_ACCESS_STR = "MAX-ACCESS";
	
	final static String IMPORTS_STR = "IMPORTS";
	
	final static String DESCRIPTION_STR = "DESCRIPTION";
	
	final static String REVISION_STR = "REVISION";
	
	final static String NOT_ACCESSIBLE = "not-accessible";
	final static String ACCESSIBLE_FOR_NOTIFY = "accessible-for-notify";
	final static String READ_ONLY = "read-only";
	final static String READ_WRITE = "read-write";
	final static String READ_CREATE = "read-create";
	
	final static String CURRENT		= "current";
	final static String DEPRECATED	= "deprecated";
	final static String OBSOLETE	= "obsolete";
	
	
	Classifier	assignmentClassifier;
	
	Classifier	commaClassifier;
	
	Classifier	curlyClassifier;
	
	Classifier	syntaxClassifier;
	
	MibManager	mibManager;
	MibModule	currentModule;
	
	MibFinder	finder;
	
	// The line currently being processed - see getNextLine()
	String		currentLine;
	
	// The previous read from the file - see getNextLine()
	String		previousLine;
	
	boolean		parseImports;
	
	// The MibLocation that started a particular parsing run
	MibLocation	startLocation;
	
	// TEMPORARY
	TreeMap<String,String>	syntaxes;
	TreeMap<String,String>	objectSyntaxes;
	TreeMap<String,String>  typeDefs;
	
	public MibParser(){
		assignmentClassifier = new Classifier();
		assignmentClassifier.addKeyword(ASSIGNMENT_STR, ASSIGNMENT_ID);
		
		commaClassifier = new Classifier();
		commaClassifier.addKeyword(FROM_STR, FROM_ID);
		commaClassifier.addSeparator(COMMA, COMMA_ID);
		commaClassifier.addSeparator(SEMI_COLON, SEMI_COLON_ID);
		
		curlyClassifier = new Classifier();
		curlyClassifier.addSeparator(LEFT_CURLY, LEFT_CURLY_ID);
		curlyClassifier.addSeparator(RIGHT_CURLY, RIGHT_CURLY_ID);
		
		syntaxClassifier = new Classifier();
		syntaxClassifier.addSeparator(LEFT_ROUND, LEFT_ROUND_ID);
		syntaxClassifier.addSeparator(RIGHT_ROUND, RIGHT_ROUND_ID);
		syntaxClassifier.addSeparator(COMMA, COMMA_ID);
		syntaxClassifier.addSeparator(LEFT_CURLY, LEFT_CURLY_ID);
		syntaxClassifier.addSeparator(RIGHT_CURLY, RIGHT_CURLY_ID);
		syntaxClassifier.addSeparator(PERIOD, PERIOD_ID);
		
		parseImports = true;
		
		syntaxes = new TreeMap<String, String>();
		objectSyntaxes = new TreeMap<String, String>();
		typeDefs = new TreeMap<String, String>();
	}
	
	/**
	 * Allows you to control whether or not parse the entire import chain or just
	 * the single file taht you specify in parseMib().
	 * @param f
	 */
	public void parseImports(boolean f){
		parseImports = f;
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
		
		startLocation = new MibLocation(fn);
		
		parseMibInternal(startLocation);
		
		// We only try to resolve the OID structure if we've parsed everything
		if (parseImports)
			mibManager.resolveDefinitions();
		
		DebugInfo.debug("\n\nTEXTUAL CONVENTION SYNTAXES:\n\n");
		for(String s: syntaxes.values()){
			DebugInfo.debug("*" + s + "*");
		}
		
		DebugInfo.debug("\n\nOBJECT SYNTAXES:\n\n");
		for(String s: objectSyntaxes.values()){
			DebugInfo.debug(s);
		}
		
//		DebugInfo.debug("\n\nTYPES:\n\n");
//		for(String s: typeDefs.values()){
//			DebugInfo.debug(s);
//		}
	}
	
	public MibManager getMibManager(){
		return(mibManager);
	}
	
	public void dumpSummary(){
		if (mibManager != null)
			mibManager.dumpSummary();
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
        	
        	// See getNextLine()
        	currentLine = null;
        	previousLine = null;
        	
        	DebugInfo.debug("\n\n" + "Parsing: " + location.getFileName());
			
			currentModule = new MibModule(location.getMibName());
			
			mibManager.addModule(currentModule);
			
            String line;
//            while ((rawInput = in.readLine()) != null) {
            while ( (line = getNextLine(in)) != null) {
//            	String line = preProcessLine(rawInput);
//        		System.out.println(rawInput);
            	
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
                else if (line.contains(TEXTUAL_CONVENTION_STR)){
                	parseTextualConvention(in, line);
                }
                else if (line.contains(ASSIGNMENT_STR)){
                	ArrayList<String> tokens = CheapSplitter.split(line, ' ', false, true);
                	
                	if (tokens.size() == 2)
                		parseType(in, line);
                }
            }
            
            in.close();
            
            if (!parseImports)
            	return;
            
    		// We've finished parsing this module, now ensure that its imports
    		// have been read as well.
            Iterator<MibImport> imports = currentModule.getImports();
            if (imports != null){
            	while(imports.hasNext()){
            		MibImport mi = imports.next();
            		
            		if (!mibManager.hasModule(mi.getMibName())){
            			MibLocation mibloc = finder.getLocation(mi.getMibName());
            			if (mibloc == null){
            				
            				// try to find a local MIB import i.e. in the same folder where we started
            				mibloc = finder.findLocal(startLocation, mi.getMibName());
            				
            				if (mibloc == null)
            					throw(new ResultException("Couldn't find " + mi.getMibName() + " imported by " + currentModule.getName()));
            			}
            			parseMibInternal(mibloc);
            		}
            		
            	}
            }
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception ex){
			DebugInfo.debug(ex.toString());
			DebugInfo.debug("While processing file: " + currentModule.getName() + " line: " + in.getLineNumber() + " content: "+ currentLine);
//			throw(ex);
		}
		
	}
		
	/**
	 * Called when we encounter the IMPORTS string and parses until we hit a blank line.
	 * Note: we expect that the IMPORTS statement is on a line by itself.
	 * <p/>
	 * There are some screwed up imports, for example the RMON2-MIB where there are blank
	 * lines separating the import symbols from the FROM statement. We look for the ending
	 * semicolon to signal the end of the IMPORTS.
	 * @param in the reader.
	 * @param str the line where we encountered the IMPORTS statement.
	 * @throws IOException  
	 */
	void parseImports(LineNumberReader in) throws IOException {
        String				line;
        ArrayList<String>	symbols = new ArrayList<String>();
        boolean 			haveEndingSemicolon	= false;
        
//        while ((rawInput = in.readLine()) != null) {
        while ((line = getNextLine(in)) != null) {
//        	String line = preProcessLine(rawInput);
        	if ( (line.length() == 0) && (haveEndingSemicolon))
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
        	
        	if (line.contains(SEMI_COLON))
        		haveEndingSemicolon = true;
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
		
        String				line;
        while ((line = getNextLine(in)) != null) {
        	if (line.contains(END_STR))
        		break;
        }
        
        MibDefinitionName mdn = new MibDefinitionName(name);
        
        MibMacro macro = new MibMacro(mdn);
        macro.setLine(lineNumber);
        
        currentModule.addDefinition(macro);
        
//        DebugInfo.debug(mdn.toString());
		
	}
	
	/**
	 * Parses lines with OBJECT IDENTIFIER of the form:
	 * 
	 * alarmObjects OBJECT IDENTIFIER ::= { alarmMIB 1 }
	 * 
	 * OR
	 *
	 * snmpMIBConformance
	 *                        OBJECT IDENTIFIER ::= { snmpMIB 2 }
	 *                        
	 * @param in
	 */
	void parseObjectIdentifier(LineNumberReader in, String line){
		TokenArrayList 	tokens 		= curlyClassifier.classify(line, true);
		int 			lineNumber 	= in.getLineNumber();
		int 			id			= -1;
		String 			name		= null;
		String 			pname		= null;
		String			idStr		= null;
		
//		DebugInfo.debug("\n" + tokens.toString());
		
		// Not all OBJECT IDENTIFER definitions are contained on a single line, the SNMPv2-MIB
		// has definitions like:
		//
		// snmpMIBConformance
        //                         OBJECT IDENTIFIER ::= { snmpMIB 2 }
		// 
		// So, we have to be prepared to look back to the previous line to get the name
		// of the object identifier.
		
		if (line.startsWith(OBJECT_IDENTIFIER_STR)){
			name 	= previousLine;
			pname 	= tokens.nth(4).getValue();
			idStr	= tokens.nth(5).getValue();
		}
		else{
			name 	= tokens.nth(0).getValue();
			pname	= tokens.nth(5).getValue();
			idStr	= tokens.nth(6).getValue();
		}
		
		try{
			id 		= Integer.parseInt(idStr);
		}
		catch(NumberFormatException ex){
			DebugInfo.debug("Error parsing OBJECT IDENTIFIER in file: " + currentModule.getName() + " line:" + in.getLineNumber());
			throw(ex);
		}

        MibOID oid = new MibOID(pname, name, id);
        
        MibObjectIdentifier identifier = new MibObjectIdentifier(oid);
        identifier.setLine(lineNumber);
        
        currentModule.addDefinition(identifier);
        
//        DebugInfo.debug(oid.toString());
		
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
		
        String line;
        while ((line = getNextLine(in)) != null) {
//        	String line = preProcessLine(rawInput);
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
        
        MibObjectIdentity identity = new MibObjectIdentity(oid);
        identity.setLine(lineNumber);
        
        currentModule.addDefinition(identity);
        
        DebugInfo.debug(oid.toString());
		
	}
	
	/**
	 * Parses OBJECT-TYPE sections
	 * See SNMPv2-SMI - line 222 - for the definition of the syntax for this.
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
		MibSyntax		syntax = null;
		MaxAccessEnum	max	= null;
		TokenArrayList 	tokens = curlyClassifier.classify(first, true);
		int 			lineNumber = in.getLineNumber();
		
		String 	name 	= tokens.nth(0).getValue();
		String 	pname	= null;
		String description	= null;
		int		id 		= -1;
		boolean	haveAssignment 	= false;
		
        String line;
        while ((line = getNextLine(in)) != null) {
//        	String line = preProcessLine(rawInput);
        	if ( (line.length() == 0) && haveAssignment)
        		break;
        	
        	// We're looking for something like:
        	// ::= { parentName 2 }
        	if (line.startsWith(ASSIGNMENT_STR)){
        		tokens 	= curlyClassifier.classify(line, true);
//        		DebugInfo.debug("TOKENS:\n " + tokens);
        		pname 	= tokens.nth(2).getValue();
        		try{
        			id		= Integer.parseInt(tokens.nth(3).getValue());
        		}
        		catch(NumberFormatException ex){
        			DebugInfo.debug("Error parsing OBJECT-TYPE in file: " + currentModule.getName() + " line:" + in.getLineNumber());
        			throw(ex);
        		}
        		haveAssignment = true;
        	}
        	else if (line.startsWith(SYNTAX_STR)){
        		objectSyntaxes.put(line, line + " " + currentModule.getName() + " " + lineNumber);
        		
        		syntax = parseObjectTypeSyntax(in, line);
        		
        		// Tricky crap: the syntax may span several lines and we don't necessarily know that we've finished
        		// parsing it until we've encountered the MAX-ACCESS line. We always use the getNextLine() method to read
        		// the next line from the reader, so we'll check here to see if we've jumped ahead to that line
        		
        		if (currentLine.contains(MAX_ACCESS_STR)){
        			max = getMaxAccess(currentLine);
        		}
        	}
        	else if (line.contains(MAX_ACCESS_STR)){
        		max = getMaxAccess(line);
        	}
        	else if (line.contains(DESCRIPTION_STR)){
        		description = parseTextSection(in, first);
        	}
        }
        
        MibOID oid = new MibOID(pname, name, id);
        
        MibObjectType objtype = new MibObjectType(oid);
        
        objtype.setSyntax(syntax);
        objtype.setLine(lineNumber);
        objtype.setMaxAccess(max);
        objtype.setDescription(description);
        
        currentModule.addDefinition(objtype);
        
        DebugInfo.debug(oid.toString());
		
	}
	
	MaxAccessEnum getMaxAccess(String line){
		if(line.contains(NOT_ACCESSIBLE))
			return(MaxAccessEnum.NOT_ACCESSIBLE);
		else if(line.contains(ACCESSIBLE_FOR_NOTIFY))
			return(MaxAccessEnum.ACCESSIBLE_FOR_NOTIFY);
		else if(line.contains(READ_ONLY))
			return(MaxAccessEnum.READ_ONLY);
		else if(line.contains(READ_WRITE))
			return(MaxAccessEnum.READ_WRITE);
		else if(line.contains(READ_CREATE))
			return(MaxAccessEnum.READ_CREATE);
		
		return(null);
	}
	
	/**
	 * 
	 * @param in
	 * @param first
	 * @return
	 * What a dog's breakfast - what idiot would create a syntax that would allow keywords to have spaces!
	 * SYNTAX RowStatus
	 * SYNTAX OBJECT IDENTIFIER
	 * SYNTAX SEQUENCE OF IfEntry
	 * 
	 * SYNTAX INTEGER (0..127) 
	 * SYNTAX OCTET STRING (SIZE (6))
	 * SYNTAX DisplayString (SIZE (0..12))
	 * SYNTAX OCTET STRING (SIZE (0..100))
	 * 
	 * SYNTAX INTEGER {
	 * SYNTAX INTEGER { notInUse(1), inUse(2) }
	 * SYNTAX INTEGER
	 *            {
	 *                cleared(1),
	 *                indeterminate(2),
	 *                critical(3),
	 *                major(4),
	 *                minor(5),
	 *                warning(6),
	 *                informational(7)
	 *            }
	 * @throws IOException  
	 */
	MibSyntax parseObjectTypeSyntax(LineNumberReader in, String first) throws IOException {
		MibSyntax rc = null;
		boolean sequence 	= false;
		boolean	sized 		= false;
		boolean anyBrackets	= false;
		boolean leftCurly 	= false;
		boolean rightCurly 	= false;
		boolean leftRound 	= false;
		
		DebugInfo.debug(first);
		
		if (first.contains(LEFT_CURLY))
			leftCurly = true;
		
		if (first.contains(RIGHT_CURLY))
			rightCurly = true;
		
		if (first.contains(LEFT_ROUND))
			leftRound = true;
		
		if (first.contains(SEQUENCE_OF_STR))
			sequence = true;
		
		if (first.contains(SIZE_STR))
			sized = true;
		
		if (leftCurly || leftRound)
			anyBrackets = true;
		
		TokenArrayList tokens = syntaxClassifier.classify(first, true);
		
		DebugInfo.debug("HERE\n" + first + "\n" + tokens.toString());
		
		if (anyBrackets){
			if (leftCurly){
				// We have any enumeration, either on one line or several - this doesn't guarantee that it doesn't
				// start on the next line, but we'll check that later
				
				if (leftCurly && rightCurly){
					DebugInfo.debug("SINGLE LINE ENUMERATION");
					rc = new MibSyntax(tokens.nth(1).getValue());

					// An enumeration on one line: SYNTAX INTEGER { notInUse(1), inUse(2) }
					for(int i=3; i<tokens.size(); ){
		        		int value = Integer.parseInt(tokens.nth(i+2).getValue());
		        		
		        		rc.addEnumValue(tokens.nth(i).getValue(), value);
						
		        		i+=5;
					}
				}
				else{
					DebugInfo.debug("MULTI LINE ENUMERATION");
					rc = new MibSyntax(tokens.nth(1).getValue());
					
					boolean haveLeftCurly = leftCurly;
					boolean haveRightCurly = false;
					
			        String line;
			        while ((line = getNextLine(in)) != null) {
			        	if (haveLeftCurly){
//			        		if (haveRightCurly && (line.length() == 0))
				        	if (haveRightCurly)
			        			break;
			        	}
			        	else if (line.length() == 0)
			        		break;
			        	
			        	if (line.contains(LEFT_CURLY))
			        		haveLeftCurly = true;
			        	if (line.contains(RIGHT_CURLY))
			        		haveRightCurly = true;
			        	
			        	tokens = syntaxClassifier.classify(line, true);
			        	
			        	if (tokens.size() >= 4){
			        		DebugInfo.debug("    SYNTAX ENUM VALUE  " + tokens.nth(0).getValue() + " = " + tokens.nth(2).getValue());
			        		int value = Integer.parseInt(tokens.nth(2).getValue());
			        		
			        		rc.addEnumValue(tokens.nth(0).getValue(), value);
			        	}
					}
					
				}
			}
			else{
				String name = "";
				
				// Reclassify and strip the separators
				tokens = syntaxClassifier.classify(first, false);

				// We have right brackets
				if (sized){
					if (first.contains("..")){
						// We have a range:
						DebugInfo.debug("SIZE WITH RANGE");
						int rstart = 0;
						int rend = 0;
						
						if (tokens.size() == 5){
							// SYNTAX DisplayString (SIZE (0..12))
							rstart = Integer.parseInt(tokens.nth(3).getValue());
							rend = Integer.parseInt(tokens.nth(4).getValue());
							name = tokens.nth(1).getValue();
						}
						else if (tokens.size() == 6){
							// SYNTAX OCTET STRING (SIZE (0..100))
							rstart = Integer.parseInt(tokens.nth(4).getValue());
							rend = Integer.parseInt(tokens.nth(5).getValue());
							name = tokens.nth(1).getValue() + " " + tokens.nth(2).getValue();
						}
						else{
							throw(new IllegalStateException("Don't know how to parse: " + first));
						}
						
						rc = new MibSyntax(name);
						rc.setStart(rstart);
						rc.setEnd(rend);
						
					}
					else{
						// It's just a straight SIZE: SYNTAX OCTET STRING (SIZE (6))
						DebugInfo.debug("JUST SIZED");
						int size = 0;
						
						if (tokens.size() == 4){
							name = tokens.nth(1).getValue();
							size = Integer.parseInt(tokens.nth(3).getValue());
						}
						else if (tokens.size() == 5){
							name = tokens.nth(1).getValue() + " " + tokens.nth(2).getValue();
							size = Integer.parseInt(tokens.nth(4).getValue());
						}
						else{
							throw(new IllegalStateException("Don't know how to parse: " + first));
						}
	
						rc = new MibSyntax(name);
						rc.setStart(size);
					}
					
					
				}
				else{
					// We have a range: SYNTAX INTEGER (0..127) 
					
					// The last two tokens will be the start/end of the range
					DebugInfo.debug("RANGE");
					
					rc = new MibSyntax(tokens.nth(1).getValue());
					
					int rstart = Integer.parseInt(tokens.nth(2).getValue());
					int rend = Integer.parseInt(tokens.nth(3).getValue());
					
					rc.setStart(rstart);
					rc.setEnd(rend);
				}
			}
		}
		else{
			// 
			StringBuffer name = new StringBuffer();
			String spacer = "";
			int start = 1;
			if (sequence)
				start = 3;
			
			for(int i=start; i<tokens.size(); i++){
				name.append(spacer);
				name.append(tokens.nth(i).getValue());
				spacer = " ";
			}
			
			rc = new MibSyntax(name.toString());
		}
		
		if (rc != null){
			rc.isSequence(sequence);
			rc.isSized(sized);
			
			// And finally, we have to look one more line ahead to be sure that we don't 
			// have an open left curly that indicates the beginning of an enumeration.
			// What a pain in the butt!
			String line = getNextLine(in);
			
			if (line.contains(LEFT_CURLY)){
				DebugInfo.debug("HANGING ENUMERATION");
				boolean haveLeftCurly = true;
				boolean haveRightCurly = false;
				
		        while ((line = getNextLine(in)) != null) {
		        	if (haveLeftCurly){
//		        		if (haveRightCurly && (line.length() == 0))
			        	if (haveRightCurly)
		        			break;
		        	}
		        	else if (line.length() == 0)
		        		break;
		        	
		        	if (line.contains(LEFT_CURLY))
		        		haveLeftCurly = true;
		        	if (line.contains(RIGHT_CURLY))
		        		haveRightCurly = true;
		        	
		        	tokens = syntaxClassifier.classify(line, true);
		        	
		        	if (tokens.size() >= 4){
		        		DebugInfo.debug("    HANGING ENUM VALUE  " + tokens.nth(0).getValue() + " = " + tokens.nth(2).getValue());
		        		int value = Integer.parseInt(tokens.nth(2).getValue());
		        		
		        		rc.addEnumValue(tokens.nth(0).getValue(), value);
		        	}
				}
				
			}
			
			DebugInfo.debug("PARSED: " + rc.toString());
		}
				
		return(rc);
	}
	
	void parseNotificationType(LineNumberReader in, String first) throws IOException {
		TokenArrayList tokens = commaClassifier.classify(first, false);
		int lineNumber = in.getLineNumber();
		
		String 	name 	= tokens.nth(0).getValue();
		String 	pname	= null;
		int		id 		= -1;
		boolean	haveAssignment 	= false;
		
        String line;
        while ((line = getNextLine(in)) != null) {
//        	String line = preProcessLine(rawInput);
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
		TokenArrayList 	tokens 			= commaClassifier.classify(first, false);
		int 			lineNumber 		= in.getLineNumber();
		
		String 			name 			= tokens.nth(0).getValue();
		String 			pname			= null;
		String 			description		= null;
		String			contact			= null;
		String			org				= null;
		String			mmiDescr		= null;
		int				id 				= -1;
		boolean			haveAssignment 	= false;
		MibRevision		mibrev			= null;
		ArrayList<MibRevision>			revs = new ArrayList<MibRevision>();
		
		if (first.contains("ianaifType"))
			DebugInfo.debug("IANA");
		
        String	line;
        while ((line = getNextLine(in)) != null) {
//        	String line = preProcessLine(rawInput);
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
        	else if (line.contains(REVISION_STR)){
        		int start = line.indexOf('"');
        		int end = line.indexOf('"', start+1);
        		mibrev = new MibRevision(line.substring(start+1,end));
        		revs.add(mibrev);
        	}
        	else if (line.contains(DESCRIPTION_STR)){
        		description = parseTextSection(in, line);
        		if (mibrev == null)
        			mmiDescr = description;
        		else
        			mibrev.setDescription(description);
        	}
        	else if (line.contains(ORGANIZATION_STR)){
            	org = parseTextSection(in, line);
        	}
        	else if (line.contains(CONTACT_INFO_STR)){
            	contact = parseTextSection(in, line);
        	}
        }
        
        MibOID oid = new MibOID(pname, name, id);
        
        MibModuleIdentity mmi = new MibModuleIdentity(oid);
        mmi.setLine(lineNumber);
        mmi.setDescription(mmiDescr);
        mmi.setRevisions(revs);
        mmi.setContactInfo(contact);
        mmi.setOrganization(org);
        
        currentModule.setModuleIdentity(mmi);
        
        DebugInfo.debug(oid.toString());
		
	}
	
	/**
	 * Parses TEXTUAL-CONVENTION sections
	 * MacAddress ::= TEXTUAL-CONVENTION
	 *     DISPLAY-HINT "1x:"
	 *     STATUS       current
	 *     DESCRIPTION
	 *             "Represents an 802 MAC address represented in the
	 *             `canonical' order defined by IEEE 802.1a, i.e., as if it
	 *             were transmitted least significant bit first, even though
	 *             802.5 (in contrast to other 802.x protocols) requires MAC
	 *             addresses to be transmitted most significant bit first."
	 *     SYNTAX       OCTET STRING (SIZE (6))
	 * 
	 * @param in the input file reader
	 * @param first the first line of identity definition
	 * @throws IOException  
	 */
	void parseTextualConvention(LineNumberReader in, String first) throws IOException {
		TokenArrayList tokens = commaClassifier.classify(first, false);
		
		if (tokens.size() != 3){
			DebugInfo.debug("TEXTUAL-CONVENTION - not enough tokens: " + first);
			return;
		}
		
		if (!tokens.nth(1).getValue().equals(ASSIGNMENT_STR)){
			DebugInfo.debug("Missing assignment operator in TEXTUAL-CONVENTION: " + first);
			return;
		}
		
		DebugInfo.debug("Parsing TEXTUAL-CONVENTION: " + tokens.nth(0).getValue());
		
		MibDefinitionName		mdn = new MibDefinitionName(tokens.nth(0).getValue());
		MibTextualConvention 	mtc = new MibTextualConvention(mdn);
		mtc.setLine(in.getLineNumber());
		
        String line;
        while ((line = getNextLine(in)) != null) {
    		if (line.startsWith(SYNTAX_STR)){
    			parseTextualConventionSyntax(in,line,mtc);
    			break;
    		}
    		else if (line.contains(DESCRIPTION_STR)){
    			mtc.setDescription(parseTextSection(in, line));
    		}
        }
        
        currentModule.addDefinition(mtc);
        
	}
	
	/**
	 * SYNTAX can take a variety of forms:
	 * <p/>
	 * SYNTAX       OCTET STRING (SIZE (6))
	 * <p/>
	 * SYNTAX       INTEGER { true(1), false(2) }
	 * <p/>
	 * SYNTAX       INTEGER {
	 *                 other(1),       -- eh?
	 *                 volatile(2),    -- e.g., in RAM
	 *                 nonVolatile(3), -- e.g., in NVRAM
	 *                 permanent(4),   -- e.g., partially in ROM
	 *                 readOnly(5)     -- e.g., completely in ROM
	 *             }
	 * <p/>
	 * SYNTAX INTEGER
	 *            {
	 *                cleared(1),
	 *                indeterminate(2),
	 *                critical(3),
	 *                major(4),
	 *                minor(5),
	 *                warning(6),
	 *                informational(7)
	 *            }
	 * <p/>
	 * When we're finished parsing the SYNTAX, we're done parsing the textual convention.
	 *
	 * @param in the input file reader
	 * @param first the line with SYNTAX on it
	 * @param mtc the convention we're parsing
	 * @throws IOException  
	 */
	void parseTextualConventionSyntax(LineNumberReader in, String first, MibTextualConvention mtc) throws IOException {
		TokenArrayList tokens = null;
		boolean haveLeftCurly = false;
		boolean haveRightCurly = false;
		
		DebugInfo.debug(first);
		
		tokens = syntaxClassifier.classify(first, true);
		DebugInfo.debug("\n" + tokens.toString());
		
		if (first.contains(LEFT_CURLY))
			haveLeftCurly = true;
		
		if (first.contains(RIGHT_CURLY))
			haveRightCurly = true;
		
		StringBuffer sb = new StringBuffer();
		for(int i=1;i<tokens.size();i++){
			if (tokens.nth(i).getType() == LEFT_CURLY_ID)
				break;
			if (tokens.nth(i).getType() == LEFT_ROUND_ID)
				break;
			
			if (sb.length() == 0)
				sb.append(tokens.nth(i).getValue());
			else
				sb.append(" " + tokens.nth(i).getValue());
		}
		
		String s = sb.toString();
		syntaxes.put(s, s);
		
		mtc.setBaseTypeName(s);
				
		if (haveLeftCurly && haveRightCurly){
			// One line enumeration
		}
		else{
	        String line;
	        while ((line = getNextLine(in)) != null) {
	        	if (haveLeftCurly){
	        		if (haveRightCurly && (line.length() == 0))
	        			break;
	        	}
	        	else if (line.length() == 0)
	        		break;
	        	
	        	if (line.contains(LEFT_CURLY))
	        		haveLeftCurly = true;
	        	if (line.contains(RIGHT_CURLY))
	        		haveRightCurly = true;
	        	
	        	tokens = syntaxClassifier.classify(line, true);
	        	
	        	if (tokens.size() >= 4){
	        		int intval = Integer.parseInt(tokens.nth(2).getValue());
	        		mtc.addEnumValue(tokens.nth(0).getValue(), intval);
	        		DebugInfo.debug("    TXTCONV ENUM VALUE  " + tokens.nth(0).getValue() + " = " + tokens.nth(2).getValue());
	        	}
	        	
	        }
	        
	        DebugInfo.debug(mtc.getSyntax().toString());
		}
		
	}
	
	/**
	 * Types are defined by specifying "name ::=" followed by a variety other stuff! Examples follow:
	 * <p/>
	 * 
	 * @param in
	 * @param first
	 * @throws IOException
	 */
	void parseType(LineNumberReader in, String first) throws IOException {
    	TokenArrayList tokens = assignmentClassifier.classify(first, false);
    	
		DebugInfo.debug("Parsing type: " + tokens.nth(0).getValue());

		if ( (tokens.size() == 2) && (tokens.nth(1).getType() == ASSIGNMENT_ID) ){
    		typeDefs.put(tokens.nth(0).getValue(), tokens.nth(0).getValue()+ " " + currentModule.getName() + " " + in.getLineNumber());
    	}
		
		MibType mt = new MibType(new MibDefinitionName(tokens.nth(0).getValue()));

//    	if (tokens.size() != 3){
//			DebugInfo.debug("TEXTUAL-CONVENTION - not enough tokens: " + first);
//			return;
//		}
//		
//		if (!tokens.nth(1).getValue().equals(ASSIGNMENT_STR)){
//			DebugInfo.debug("Missing assignment operator in TEXTUAL-CONVENTION: " + first);
//			return;
//		}
//		
//		DebugInfo.debug("Parsing TEXTUAL-CONVENTION: " + tokens.nth(0).getValue());
//		
//		MibDefinitionName		mdn = new MibDefinitionName(tokens.nth(0).getValue());
//		MibTextualConvention 	mtc = new MibTextualConvention(mdn);
//		mtc.setLine(in.getLineNumber());
//		
        String line;
        while ((line = getNextLine(in)) != null) {
    		if (line.length() == 0)
    			break;
    		DebugInfo.debug("TYPELINE: " + line);
        }
//        
        currentModule.addDefinition(mt);
        
	}
	
	/**
	 * Parses quoted sections of text like DESCRIPTIONs:
	 * DESCRIPTION
	 * "blah blah blah"
	 * 
	 * DESCRIPTION
	 * "blah blah
	 * bla
	 * blah blah"
	 * 
	 * DESCRIPTION
	 * "blah blah
	 * blah
	 * "
	 * 
	 * DESCRIPTION "blah bla"
	 * 
	 * DESCRIPTION "blah
	 * blah"
	 * 
	 * @param in
	 * @param first
	 * @return
	 * @throws IOException 
	 */
	String parseTextSection(LineNumberReader in, String first) throws IOException{
		StringBuffer sb = new StringBuffer();
		boolean haveOpen = false;
		boolean haveClose = false;
		int lineCount = 0;
		int openIndex = 0;
		int closeIndex = 0;
		
		DebugInfo.debug("PARSING DESCRIPTION");
		
		if (first.contains("\"")){
			openIndex = first.indexOf('"');
			closeIndex = first.indexOf('"', openIndex+1);
			
			if (openIndex != -1)
				haveOpen = true;
			if (closeIndex != -1)
				haveClose = true;
			
			// All on one line
			if (haveOpen && haveClose)
				sb.append(first.substring(openIndex+1, closeIndex));
			else
				sb.append(first.substring(openIndex+1));
			
		}
		
		if (!(haveOpen && haveClose)){
		
	        String line;
	        while ((line = getNextLine(in)) != null) {
	        	
	        	DebugInfo.debug("LINE: " + line);
				if (!haveOpen){
					openIndex = line.indexOf('"');
					if (openIndex != -1)
						haveOpen = true;
					
					closeIndex = line.lastIndexOf('"');
					if (closeIndex != -1){
						if (closeIndex > openIndex)
							haveClose = true;
					}
				}
				else if (!haveClose){
					if (line.contains("\""))
						haveClose = true;
				}
				
				if (lineCount > 0)
					sb.append("\n<br>" + line);
				else
					sb.append(line);
				
				if (haveOpen && haveClose)
					break;
				
				lineCount++;
			}
		}
        
        String rc = sb.toString().replaceAll("\"", "");
        
        DebugInfo.debug("DESCRIPTION: " + rc);
		
		return(rc);
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
	
	String getNextLine(LineNumberReader in) throws IOException {
        String	rawInput = in.readLine();
        if (rawInput == null)
        	return(null);
        
        previousLine = currentLine;
        currentLine = preProcessLine(rawInput);
        
        return(currentLine);
	}
}
