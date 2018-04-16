 /**
 * JUunit tests for the Parser for the class project in COP5556 Programming Language Principles 
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
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp18.Scanner.Kind;
import cop5556sp18.Parser;
import cop5556sp18.Scanner;
import cop5556sp18.AST.ASTNode;
import cop5556sp18.AST.Block;
import cop5556sp18.AST.Declaration;
import cop5556sp18.AST.Expression;
import cop5556sp18.AST.ExpressionBinary;
import cop5556sp18.AST.ExpressionBooleanLiteral;
import cop5556sp18.AST.ExpressionConditional;
import cop5556sp18.AST.ExpressionFloatLiteral;
import cop5556sp18.AST.ExpressionFunctionAppWithExpressionArg;
import cop5556sp18.AST.ExpressionFunctionAppWithPixel;
import cop5556sp18.AST.ExpressionIdent;
import cop5556sp18.AST.ExpressionIntegerLiteral;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.LHS;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.Statement;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementWrite;
import cop5556sp18.Parser.SyntaxException;
import cop5556sp18.Scanner.LexicalException;
import cop5556sp18.Scanner.Token;
import static cop5556sp18.Scanner.Kind.*;

public class ParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	//creates and returns a parser for the given input.
	private Parser makeParser(String input) throws LexicalException {
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);
		return parser;
	}
	

	
	/**
	 * Simple test case with an empty program.  This throws an exception 
	 * because it lacks an identifier and a block
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		@SuppressWarnings("unused")
		Program p = parser.parse();
	}
	
	/**
	 * Smallest legal program.
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testSmallest() throws LexicalException, SyntaxException {
		String input = "b{}";  
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);
		assertEquals("b", p.progName);
		assertEquals(0, p.block.decsOrStatements.size());
	}	
	
	
	/**
	 * Checks that an element in a block is a declaration with the given type and name.
	 * The element to check is indicated by the value of index.
	 * 
	 * @param block
	 * @param index
	 * @param type
	 * @param name
	 * @return
	 */
	Declaration checkDec(Block block, int index, Kind type,
			String name) {
		ASTNode node = block.decOrStatement(index);
		assertEquals(Declaration.class, node.getClass());
		Declaration dec = (Declaration) node;
		assertEquals(type, dec.type);
		assertEquals(name, dec.name);
		return dec;
	}	
	
	@Test
	public void testDec0() throws LexicalException, SyntaxException {
		String input = "b{int c; image j;}";
		Parser parser = makeParser(input);
		Program p = parser.parse();
		show(p);	
		checkDec(p.block, 0, Kind.KW_int, "c");
		checkDec(p.block, 1, Kind.KW_image, "j");
	} 
	
	
	/** This test illustrates how you can test specific grammar elements by themselves by
	 * calling the corresponding parser method directly, instead of calling parse.
	 * This requires that the methods are visible (not private). 
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	
	@Test
	public void testExpression() throws LexicalException, SyntaxException {
		String input = "x + 2";
		Parser parser = makeParser(input);
		Expression e = parser.expression();  //call expression here instead of parse
		show(e);	
		assertEquals(ExpressionBinary.class, e.getClass());
		ExpressionBinary b = (ExpressionBinary)e;
		assertEquals(ExpressionIdent.class, b.leftExpression.getClass());
		ExpressionIdent left = (ExpressionIdent)b.leftExpression;
		assertEquals("x", left.name);
		assertEquals(ExpressionIntegerLiteral.class, b.rightExpression.getClass());
		ExpressionIntegerLiteral right = (ExpressionIntegerLiteral)b.rightExpression;
		assertEquals(2, right.value);
		assertEquals(OP_PLUS, b.op);
	}
	
	
	//Additional Test cases
	//Test1
			@Test
			public void testfloat() throws LexicalException, SyntaxException {
				String input = "c{float d;}";
				Parser parser = makeParser(input);
				parser.parse();
			}
		//Test2
				@Test
					public void testbool() throws LexicalException, SyntaxException {
						String input = "c{boolean d;}";
						Parser parser = makeParser(input);
						parser.parse();
					}
		//Test3
				@Test
				public void test4() throws LexicalException, SyntaxException {
					String input = "sd22 {}";
					Parser parser = makeParser(input);
					parser.parse();
				}
		//Test4
				@Test
				 public void testcase4() throws LexicalException, SyntaxException {
		                String input = "testcase4{"
		               		+ "show a;"
		               		+ "}";
		                Parser parser = makeParser(input);
		                parser.parse();
		        }
		//Test5
				@Test
				 public void testcase5() throws LexicalException, SyntaxException {
		                String input = "testcase5{"
		               		+ "int x;"
		               		+ "}";
		                Parser parser = makeParser(input);
		                parser.parse();
		        }
		//Test6
				@Test
				 public void testcase6() throws LexicalException, SyntaxException {
		                String input = "testcase6{"
		                	+ "image magnify[576,576];"
		               		+ "}";
		                Parser parser = makeParser(input);
		                parser.parse();
		        }
		//Test7
				@Test
				 public void testcase7() throws LexicalException, SyntaxException {
		                String input = "testcase7{"
		                	+ "while(abc==height(wall)) {};"
		               		+ "}";
		                Parser parser = makeParser(input);
		                parser.parse();
		        }
		//Test8
				@Test
				 public void testcase8() throws LexicalException, SyntaxException {
		                String input = "testcase8{"
		                	+ "sd[l,b]:=<<4,4,4,4>>;"
		               		+ "}";
		                Parser parser = makeParser(input);
		                parser.parse();
		        }
		//Test9
				@Test
				 public void testcase9() throws LexicalException, SyntaxException {
		                String input = "testcase9{"
		                	+ "float a;"
		               		+ "}";
		                Parser parser = makeParser(input);
		                parser.parse();
		        }
		//Test10
				@Test
				 public void testcase10() throws LexicalException, SyntaxException {
		                String input = "testcase10{"
		                	+ " input GoGators from @0;"
		               		+ "}";
		                Parser parser = makeParser(input);
		                parser.parse();
		        }
			
		
	//TA Cases
	@Test
	public void testStatementWhile3() throws LexicalException, SyntaxException {
		String input = "while x {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	
	public void testStatementIf33() throws LexicalException, SyntaxException {
		String input = " a[1,()]";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementWhile4() throws LexicalException, SyntaxException {
		String input = "while 1 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementWhile5() throws LexicalException, SyntaxException {
		String input = "while 2.0 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementWhile6() throws LexicalException, SyntaxException {
		String input = "while Z {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementWhile7() throws LexicalException, SyntaxException {
		String input = "while 1+2 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementWhile8() throws LexicalException, SyntaxException {
		String input = "while 5&4 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementIf3() throws LexicalException, SyntaxException {
		String input = "if x {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementIf4() throws LexicalException, SyntaxException {
		String input = "if 1 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementIf5() throws LexicalException, SyntaxException {
		String input = "if 1.0 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementIf6() throws LexicalException, SyntaxException {
		String input = "if Z {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementIf7() throws LexicalException, SyntaxException {
		String input = "if 1+2 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testStatementIf8() throws LexicalException, SyntaxException {
		String input = "if 8&2 {}";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		parser.statement();
	}
	@Test
	public void testExpressionFunctionArg2() throws LexicalException, SyntaxException {
		String input = "sin x";
		thrown.expect(SyntaxException.class);
		Parser parser = makeParser(input);
		
		parser.expression();
	}
	
	
}
				