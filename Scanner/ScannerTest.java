 /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Spring 2018.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Spring 2018 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2018
 */

package cop5556sp18;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(pos, t.pos);
		assertEquals(length, t.length);
		assertEquals(line, t.line());
		assertEquals(pos_in_line, t.posInLine());
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	
	
	


	/**
	 * Simple test case with an empty program.  The only Token will be the EOF Token.
	 *   
	 * @throws LexicalException
	 */
	@Test
	public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, the end of line 
	 * character would be inserted by the text editor.
	 * Showing the input will let you check your input is 
	 * what you think it is.
	 * 
	 * @throws LexicalException
	 */
	@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}
	

	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it an illegal character '~' in position 2
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	
	
	
	@Test    
	public void Testing_Float1()  	throws LexicalException
	{   String input="34521.001";  
	Scanner scanner=new Scanner(input).scan();  
	show (input);   show(scanner);
	checkNext(scanner,FLOAT_LITERAL,0,9,1,1); 
	}    
	@Test    
	public void Testing_Float2() throws LexicalException
	{   String input=".01";
	Scanner scanner=new Scanner(input).scan();
	show (input);  
	show(scanner);   
	checkNext(scanner,FLOAT_LITERAL,0,3,1,1);
	}   
	
	@Test 
	public void Testing_Float3() throws LexicalException
	{ 
		String input="1.1"; 
		Scanner scanner=new Scanner(input).scan(); 
	  show (input); 
	  show(scanner); 
	  checkNext(scanner,FLOAT_LITERAL,0,3,1,1); 
	  }
	
	
	
	
	@Test
	public void failIllegalChar() throws LexicalException {
		String input = ";;~";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //Catch the exception
			show(e);                    //Display it
			assertEquals(2,e.getPos()); //Check that it occurred in the expected position
			throw e;                    //Rethrow exception so JUnit will see it
		}
	}




	@Test
	public void testParens() throws LexicalException {
		String input = "()";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LPAREN, 0, 1, 1, 1);
		checkNext(scanner, RPAREN, 1, 1, 1, 2);
		checkNextIsEOF(scanner);
	}
	
	
	
	/*
	@Test   
	public void Testing_Keyword() 
			throws LexicalException
	{   String input="show";  
	Scanner scanner=new Scanner(input).scan();  
	show (input);   show(scanner);  
	checkNext(scanner,KW_show,1,4,1,6); 
	}  
	*/ 
	@Test 
	public void Testing_Integer()
			throws LexicalException
	{   String input="2018";  
	Scanner scanner=new Scanner(input).scan();
	show (input);   show(scanner);  
	checkNext(scanner,INTEGER_LITERAL,0,4,1,1); 
	}     
	
   
	  @Test     public void testCommentNegative()
			  throws LexicalException
	  {   String input="/*  */ sagar";  
	  Scanner scanner=new Scanner(input).scan();  
	  show (input);   
	  show(scanner); 
	  checkNext(scanner,IDENTIFIER,7,5,1,8);   }     
	  @Test   
	  public void testComment() throws LexicalException
	  {   String input="/*  "; 
	  thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException 
	  try {   new Scanner(input).scan();   }
	  catch (LexicalException e)
	  {  //Catch the exception 
		  show(e); //Display it  
		  assertEquals(4,e.getPos()); //Check that it occurred in the expected position 
		  throw e; //Rethrow exception so JUnit will see it  
		  }
		  } 
		  @Test     public void testComment3() throws LexicalException
		  {   String input="/* sagar is coolest*/";  
		  Scanner scanner=new Scanner(input).scan(); 
		  show (input);   show(scanner);   }      
		  
		  @Test     public void testCommentDivide() throws LexicalException
		  {   String input="/*  */ //";   Scanner scanner=new Scanner(input).scan();
		  show (input);   show(scanner);   
		  checkNext(scanner,OP_DIV,7,1,1,8);  
		  checkNext(scanner,OP_DIV,8,1,1,9);                 
	  }
	  
     
	  
	 
	  
	//Made Cases
	
	
	//Experimental Test Cases
	








@Test

public void testLeftShift() throws LexicalException{ String input="@<=<!<";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,OP_AT,0,1,1,1);

checkNext(scanner,OP_LE,1,2,1,2);

checkNext(scanner,OP_LT,3,1,1,4);

checkNext(scanner,OP_EXCLAMATION,4,1,1,5);

checkNext(scanner,OP_LT,5,1,1,6);

}

@Test

public void testOperators() throws LexicalException{ String input="@+/-*";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,OP_AT,0,1,1,1);

}


 

@Test

public void testkeywords() throws LexicalException{ String input="Z default_width s$ s_"; Scanner scanner=new Scanner(input).scan(); show (input);

show(scanner);

checkNext(scanner,KW_Z,0,1,1,1);

checkNext(scanner,KW_default_width,2,13,1,3);

checkNext(scanner,IDENTIFIER,16,2,1,17);

checkNext(scanner,IDENTIFIER,19,2,1,20);

}

@Test

public void testInteger() throws LexicalException{ String input="12345";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,INTEGER_LITERAL,0,5,1,1);

}

@Test

public void testFloat() throws LexicalException{

String input="12345.0001";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,FLOAT_LITERAL,0,10,1,1);

}

@Test

public void testFloat1() throws LexicalException{ String input=".0001";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,FLOAT_LITERAL,0,5,1,1);

}

@Test

public void testFloat2() throws LexicalException{ String input=".0001";

Scanner scanner=new Scanner(input).scan();
 
show (input);

show(scanner);

checkNext(scanner,FLOAT_LITERAL,0,5,1,1);

}

/*@Test

public void testIntegerNegative() throws LexicalException{ String input="012345";

show(input);

thrown.expect(LexicalException.class); //Tell JUnit to expect a LexicalException try {

new Scanner(input).scan();

} catch (LexicalException e) { //Catch the exception

show(e);	//Display it

assertEquals(6,e.getPos()); //Check that it occurred in the expected position

throw e;	//Rethrow exception so JUnit will see it

}

}*/

@Test

public void testZero() throws LexicalException{

String input="0";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,INTEGER_LITERAL,0,1,1,1);

}

@Test

public void testFloat3() throws LexicalException{ String input="1000.000011";
Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,FLOAT_LITERAL,0,11,1,1);

}

@Test

public void testFloat4() throws LexicalException{

String input="123456.11 12345 sagar$_ true";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,FLOAT_LITERAL,0,9,1,1);
 
checkNext(scanner,INTEGER_LITERAL,10,5,1,11); checkNext(scanner,IDENTIFIER,16,7,1,17); checkNext(scanner,BOOLEAN_LITERAL,24,4,1,25); }



/*
@Test

public void test_Comment() throws LexicalException{ String input="/* ";

thrown.expect(LexicalException.class); //Tell JUnit to expect a LexicalException try {

new Scanner(input).scan();

} 
catch (LexicalException e)
{ //Catch the exception

show(e);	//Display it

assertEquals(4,e.getPos()); //Check that it occurred in the expected position

throw e;	//Rethrow exception so JUnit will see it

}

}
*/
@Test

public void testComment31() throws LexicalException{ String input="/* sagar is coolest*/";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}


@Test

public void test() throws LexicalException{

String input="*** ::\n:=";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,OP_POWER,0,2,1,1);

checkNext(scanner,OP_TIMES,2,1,1,3);

checkNext(scanner,OP_COLON,4,1,1,5);

checkNext(scanner,OP_COLON,5,1,1,6);

checkNext(scanner,OP_ASSIGN,7,2,2,1);

}

/*
	@Test

public void testFloatRange() throws LexicalException{

String input="999999999999999999999999999999.99999"; thrown.expect(LexicalException.class); //Tell JUnit to expect a LexicalException try {

new Scanner(input).scan();

}
	catch (LexicalException e) { //Catch the exception

show(e);	//Display it

assertEquals(35,e.getPos()); //Check that it occurred in the expected position

throw e;	//Rethrow exception so JUnit will see it

}

}
*/
@Test

public void testCommentStar() throws LexicalException{ String input="/****/00.12334";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,INTEGER_LITERAL,6,1,1,7); checkNext(scanner,FLOAT_LITERAL,7,7,1,8);
}


/*
@Test

public void testComment1() throws LexicalException{ String input="/**//*";
 
Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,OP_TIMES,4,1,1,5);

checkNext(scanner,OP_DIV,5,1,1,6);

} */

@Test

public void testNegativeIntegerTwoTokens() throws LexicalException{ String input="-25";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,OP_MINUS,0,1,1,1); checkNext(scanner,INTEGER_LITERAL,1,2,1,2);
}

@Test

public void testDotSeperator() throws LexicalException{ String input="0.1.abc";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,FLOAT_LITERAL,0,3,1,1);

checkNext(scanner,DOT,3,1,1,4);

checkNext(scanner,IDENTIFIER,4,3,1,5);

}

@Test

public void testDotSecond() throws LexicalException{ String input="abc.def";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,IDENTIFIER,0,3,1,1);

checkNext(scanner,DOT,3,1,1,4);

checkNext(scanner,IDENTIFIER,4,3,1,5);

}

@Test

public void testComment4() throws LexicalException{ String input="/**";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);
 
}

/* it should throw an exception since identifier cannot start with _ testcase is flawed */ @Test

public void testIdentifier() throws LexicalException{ String input="_123";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,IDENTIFIER,0,4,1,1);

}

@Test

public void testBoolean() throws LexicalException{ String input="true@false";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}
/*
@Test

public void testIdentifier2() throws LexicalException{ String input="abcde$ /*";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,IDENTIFIER,0,4,1,1);

}
*/

@Test

public void testPower() throws LexicalException{ String input="***";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,IDENTIFIER,0,4,1,1);

}

@Test

public void testotEqual() throws LexicalException{ String input="!===";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

checkNext(scanner,IDENTIFIER,0,4,1,1);

}
 
@Test

public void tesC() throws LexicalException{

String input="/* /**/*/";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}

@Test

public void tesC2() throws LexicalException{

String input="/*//////*/***/";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}

@Test

public void tesFloat0() throws LexicalException{

String input="0.";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}

@Test

public void tesFloat23() throws LexicalException{

String input="0.	* 0.23";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}

@Test

public void tesFloat24() throws LexicalException{

String input="0.	* 0.23";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}

@Test

public void tesFloat2() throws LexicalException{

String input="73..";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}
 
@Test

public void testSomething() throws LexicalException{ String input="Z.show";

Scanner scanner=new Scanner(input).scan();

show (input);

show(scanner);

}

//Different Set
/*
@Test

public void testIdentifierLegal() throws LexicalException { String input = "abc9$";

String input2 = "true";

Scanner scanner = new Scanner(input).scan();

show(input);

show(scanner);

checkNext(scanner, IDENTIFIER, 0, 5, 1, 1);

scanner = new Scanner(input2).scan();

show(input2);

show(scanner);

checkNext(scanner, BOOLEAN_LITERAL, 0, 4, 1, 1); //checkNext(scanner, RPAREN, 1, 1, 1, 2); checkNextIsEOF(scanner); String input3="Z";

scanner = new Scanner(input3).scan();

show(input3);

show(scanner);

checkNext(scanner, KW_Z, 0, 1, 1, 1);

//checkNext(scanner, RPAREN, 1, 1, 1, 2);

checkNextIsEOF(scanner);

String input4="abc9$ true Z";

scanner = new Scanner(input4).scan();

show(input4);

show(scanner);

checkNext(scanner, IDENTIFIER, 0, 5, 1, 1); checkNext(scanner, BOOLEAN_LITERAL, 6, 4, 1, 7); checkNext(scanner, KW_Z, 11, 1, 1, 12); checkNextIsEOF(scanner);
 
String input5="abc9$\ntrue\rZ";

scanner = new Scanner(input5).scan();

show(input4);

show(scanner);

checkNext(scanner, IDENTIFIER, 0, 5, 1, 1); checkNext(scanner, BOOLEAN_LITERAL, 6, 4, 2, 1); checkNext(scanner, KW_Z, 11, 1, 3, 1); checkNextIsEOF(scanner);

}

@Test

public void failIllegalIdentifier() throws LexicalException { String input = "a^";

show(input);

thrown.expect(LexicalException.class); //Tell JUnit to expect a LexicalException try {

new Scanner(input).scan();

} 
catch (LexicalException e)
{ 
	//Catch the exception

show(e);	//Display it

assertEquals(1,e.getPos()); //Check that it occurred in the expected position
 

throw e;
 

//Rethrow exception so JUnit will see it
 

}

}
 

*/
/*

@Test

public void testing_1Integer() throws LexicalException { String input = "12340";

Scanner scanner = new Scanner(input).scan();

show(input);

show(scanner);

checkNext(scanner, Kind.INTEGER_LITERAL, 0, 5, 1, 1); //checkNext(scanner, RPAREN, 1, 1, 1, 2); checkNextIsEOF(scanner);

} 

@Test

public void testingFloat() throws LexicalException {

String input = "1.234 000";

Scanner scanner = new Scanner(input).scan();

show(input);

show(scanner);
 
checkNext(scanner, Kind.FLOAT_LITERAL, 0, 5, 1, 1); 
checkNext(scanner, Kind.INTEGER_LITERAL, 6, 1, 1, 7); 
checkNext(scanner, Kind.INTEGER_LITERAL, 7, 1, 1, 8); 
checkNext(scanner, Kind.INTEGER_LITERAL, 8, 1, 1, 9); 
checkNextIsEOF(scanner);

}
*/
@Test

public void testComments() throws LexicalException { String input = "/**/";

Scanner scanner = new Scanner(input).scan();

show(input);

show(scanner);

checkNextIsEOF(scanner);

}
}
/*
@Test

public void testFloatMix() throws LexicalException { String input = "1.234 0.01";

Scanner scanner = new Scanner(input).scan();

show(input);

show(scanner);

checkNext(scanner, Kind.FLOAT_LITERAL, 0, 5, 1, 1);

checkNext(scanner, Kind.FLOAT_LITERAL, 6, 4, 1, 7);

checkNextIsEOF(scanner);

String input2 = "1.";

scanner = new Scanner(input2).scan();

show(input2);

show(scanner);

checkNext(scanner, Kind.FLOAT_LITERAL, 0, 2, 1, 1);

checkNextIsEOF(scanner);

String input3 = ".01";

scanner = new Scanner(input3).scan();

show(input3);

show(scanner);

checkNext(scanner, Kind.FLOAT_LITERAL, 0, 3, 1, 1);

checkNextIsEOF(scanner);

String input4 = "0.1";

scanner = new Scanner(input4).scan();

show(input4);

show(scanner);

checkNext(scanner, Kind.FLOAT_LITERAL, 0, 3, 1, 1); checkNextIsEOF(scanner);

}
 

@Test

public void testIntegerOOR() throws LexicalException { String input = "2147483647111111";

//String input = ";;~";

show(input);

thrown.expect(LexicalException.class); //Tell JUnit to expect a LexicalException try {

new Scanner(input).scan();

} catch (LexicalException e) { //Catch the exception

show(e);	//Display it

assertEquals(0,e.getPos()); //Check that it occurred in the expected

position
 

}
 

throw e;

}
 

//Rethrow exception so JUnit will see it
*/ 
/*
@Test

public void testFloatOOR() throws LexicalException 
{ String input ="17000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

0000000000000000000000000000000000000000000000000000000000000000000000000000

000000000000000000000000000000000000000000000000000000000000000.0001"; //System.out.println(input);
*/
//String input = ";;~";

//show(input);


 

//Rethrow exception so JUnit will see it
 






 

//Rethrow exception so JUnit will see it
 



	
	

	

