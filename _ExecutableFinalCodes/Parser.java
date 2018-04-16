package cop5556sp18;
/* *
 * Initial code for SimpleParser for the class project in COP5556 Programming Language Principles 
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


import cop5556sp18.Scanner.Token;
import cop5556sp18.Scanner.Kind;
import static cop5556sp18.Scanner.Kind.*;

import java.util.ArrayList;    // added

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
import cop5556sp18.AST.ExpressionPixelConstructor;
import cop5556sp18.AST.ExpressionPixel;
import cop5556sp18.AST.ExpressionPredefinedName;
import cop5556sp18.AST.ExpressionUnary;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
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



public class Parser {
	
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		Token tempvar = t;
		if (isKind(kinds)) {
			consume();
			return tempvar;
		}
		StringBuilder a = new StringBuilder();
		for (Kind kind1 : kinds) {
			a.append(kind1).append(kind1).append(" ");
		}
		
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!

	}
	
	private Token consume() throws SyntaxException {
		Token tmp = t;
		if (isKind( EOF)) {
			throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!  
			//Note that EOF should be matched by the matchEOF method which is called only in parse().  
			//Anywhere else is an error. */
		}
		t = scanner.nextToken();
		return tmp;
	}


	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (isKind(EOF)) {
			return t;
		}
		throw new SyntaxException(t,"Syntax Error"); //TODO  give a better error message!
	}
	
	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}
	
	public Program parse() throws SyntaxException {
		Program p =program();
		matchEOF();
		return p;
	}

	/*
	 * Program ::= Identifier Block
	 */
	Program  program() throws SyntaxException {
		Token first = t;
		Token name = match(IDENTIFIER);
		Block b = block();
		return new Program(first,name, b);
	}
	
	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }
	 */
	
	Kind[] firstProgram = {IDENTIFIER};
	Kind[] firstBlock = { LBRACE};
	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstStatement = {KW_input, KW_write, KW_while, KW_if, KW_show, KW_sleep,IDENTIFIER,
							KW_red, KW_green, KW_blue, KW_alpha };
	// firstStatement includes StateAssign + lhs +Color
	
	Kind[] firstLHS= {IDENTIFIER,KW_red, KW_green, KW_blue, KW_alpha };
	Kind[] firstColor = {KW_red, KW_green, KW_blue,KW_alpha};
	Kind[] firstPrimary = {INTEGER_LITERAL, BOOLEAN_LITERAL, FLOAT_LITERAL,LPAREN, IDENTIFIER,
							KW_Z, KW_default_height, KW_default_width, LPIXEL,KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, 
							KW_cart_y, KW_polar_a, KW_polar_r, KW_int, KW_float, KW_width, KW_height,KW_red, KW_green, KW_blue,KW_alpha };
	// firstPrimary includes PredefinedName + Pixel Constructor + FunctionName + Color
	
	Kind[] firstPixelSelector = {LSQUARE};
	Kind[] firstPixelConstructor = {LPIXEL};
	Kind[] firstPixelExpression = {IDENTIFIER};
	Kind[] firstFunctionApplication = {KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r,
										KW_int, KW_float, KW_width, KW_height,KW_red, KW_green, KW_blue, KW_alpha};
	//firstFunctionApplication includes FunctionName + Color
	
	Kind[] firstFunctionName = {KW_sin, KW_cos, KW_atan, KW_abs, KW_log, KW_cart_x, KW_cart_y, KW_polar_a, KW_polar_r,
								KW_int, KW_float, KW_width, KW_height,KW_red, KW_green, KW_blue, KW_alpha};
	Kind[] firstPredefinedName = {KW_Z, KW_default_height, KW_default_width};
	
	
	
	Block block() throws SyntaxException {
		Token first = t;
		
		match(LBRACE);   
		ArrayList<ASTNode> dec_state = new ArrayList<ASTNode>();

		while (isKind(firstDec)|isKind(firstStatement)) 
		{
			if (isKind(firstDec))
			{
 			System.out.println("declaration");    
				Declaration dec = declaration();      
				dec_state.add(dec);
			} else if (isKind(firstStatement)) {
//				System.out.println("statement");
				Statement state = statement();
				dec_state.add(state);
			}
//	     	System.out.println(t.kind);
			match(SEMI);
		}
		match(RBRACE);
		return new Block(first, dec_state  );

	}
	
	Declaration declaration() throws SyntaxException{
		//TODO
		 
	//	System.out.println("Inside declaration");
		Token first =t;
		
		Kind kind = t.kind;
		switch (kind) {
		case KW_int:                  // write for these statements as well
		{
			consume();
			Token name =match(IDENTIFIER);   
			return new Declaration(first,first,name, null,null);
		}
	//	break;
		case KW_float:
		{
			consume();
			Token name =match(IDENTIFIER);
			return new Declaration(first,first,name, null,null);
		}
	//	break;
		case KW_boolean:
		{
			consume();
			Token name =match(IDENTIFIER);
			return new Declaration(first,first,name, null,null);
		}
	//	break;
		case KW_filename:
		{
			consume();
			Token name = match(IDENTIFIER);
			return new Declaration(first,first,name, null,null);
		}
//		break;
		case KW_image:
		{
			//System.out.println("Inside image");
			Token type = consume();
			Token name = match(IDENTIFIER);
			Expression e0 = null;
			Expression e1 = null;
			if(t.kind==LSQUARE)
			{
			//	System.out.println("Inside image2");
				consume();
				 e0 = expression();
				match(COMMA);
				 e1 = expression();
				match(RSQUARE);
				
			}	
			return new Declaration(first,type,name, e0,e1);
		}
	//	break;
		
		default:
			throw new SyntaxException(t, "illegal dec");
		}
	//	System.out.println("leaving declaration"+t.kind);
	}
	
	Statement statement() throws SyntaxException
	{
		//TODO
		
//		System.out.println("Inside statement");
		Token first =t;
		Kind kind = t.kind;
		switch(kind) 
		{
		case KW_input:
	//		System.out.println("Inside input");
			consume();
			Token destinationName =match(IDENTIFIER);
			match(KW_from);
			match(OP_AT);
			Expression e0= expression();
			return new StatementInput(first, destinationName, e0);
	//		break;
		
		case KW_write:
			consume();
			Token sName= match(IDENTIFIER);
			match(KW_to);
			Token dName = match(IDENTIFIER);
			return new StatementWrite(first, sName, dName);	
	//		break;
			
		case KW_while:
		
	//		System.out.println("Inside while");
			consume();
			match(LPAREN);
		    Expression e = expression();
		    match(RPAREN);
			Block b =block();
			return new StatementWhile(first,e,b);
	//		break;
		
		case KW_if:
			consume();
			match(LPAREN);
			Expression e1 = expression();
			match(RPAREN);
			Block b1 = block();	
			return new StatementIf(first,e1,b1);
	//		break;
		
		case KW_show:
			consume();
			Expression e2 = expression();
			return new StatementShow(first, e2);
	//		break;
			
		case KW_sleep:
			consume();
			Expression e3 = expression();
			return new StatementSleep(first,e3);
	//		break;		
		
		case IDENTIFIER: 
			LHS lhs = lhs();
			match(OP_ASSIGN);
			Expression e4 = expression();
			return new StatementAssign(first, lhs, e4);
	//		break;
		
		case KW_red: 
			LHS lhs1 = lhs();
		//	System.out.println("lhs processed");
			match(OP_ASSIGN);
		//	System.out.println("assignment done");
			Expression e5 = expression();
			return new StatementAssign(first, lhs1, e5);
	//		break;
			
		case KW_blue: 
			LHS lhs2 = lhs();
			match(OP_ASSIGN);
			Expression e6 = expression();
			return new StatementAssign(first, lhs2, e6);
	//		break;
			
		case KW_green: 
			LHS lhs3 = lhs();
			match(OP_ASSIGN);
			Expression e7 = expression();
			return new StatementAssign(first, lhs3, e7);
	//		break;
			
		case KW_alpha: 
			LHS lhs4 = lhs();
			match(OP_ASSIGN);
			Expression e8 = expression();
			return new StatementAssign(first, lhs4, e8);
	//		break;
				
		default: throw new UnsupportedOperationException();	
       // may be return statement required
		}
		
	//	System.out.println("leaving statement"+t.kind);
		
	}

 
	LHS lhs() throws SyntaxException
	{
		Token first=t;
		if (t.kind== IDENTIFIER)
		{
			Token name =consume();
				if(isKind(firstPixelSelector))
				{
					PixelSelector p =pixelSelector();
					return new LHSPixel(first,name,p);
				}
				return new LHSIdent(first,name);
		}
//		else if (isKind(firstColor))
//		{
			Token color = consume();
			match(LPAREN);
			Token name = match(IDENTIFIER);
			PixelSelector p1 =pixelSelector();
			match (RPAREN);
			return new LHSSample (first,name, p1, color);
//		}
//		else
//			return null;
	}

		
	ExpressionPixelConstructor PixelConstructor() throws SyntaxException
	{
		Token first =t;
		if (t.kind == Kind.LPIXEL)                       //Comparing twice once while calling, once here but consuming here only
		{
			consume();
			Expression e0 = expression();
			match(COMMA);
			Expression e1 = expression();
			match(COMMA);
			Expression e2 = expression();
			match(COMMA);
			Expression e3 = expression();
			match(RPIXEL);	
			return new ExpressionPixelConstructor(first,e0,e1,e2,e3);
		}
		else
			return null;
	}
	
	PixelSelector pixelSelector() throws SyntaxException
	{
		Token first =t;
		if(t.kind == LSQUARE)
		{
			consume();
			Expression e0 = expression();
			match(COMMA);
			Expression e1 = expression();
			match(RSQUARE);
			PixelSelector pixel = new PixelSelector(first,e0,e1);
			return pixel;
		}
		else
			return null;
	}

/* 
 *Not using this segment
	void PixelExpression() throws SyntaxException     //Clubbed with Identifier & pixelSelector, practically not using this func
	{
		if(t.kind == Kind.IDENTIFIER) 
		{			
			consume();
		} 
		else if(t.kind == Kind.LSQUARE)
		{
			consume();
			pixelSelector();
			match(Kind.RSQUARE);
		}
	}
*/	
	void PredefinedName() throws SyntaxException
	{
		if (isKind(firstPredefinedName))
		{
			consume();
		}
	}
	Token FunctionName() throws SyntaxException 
	{
	//	System.out.println("***Function name"+t.kind);
		Token tmp =t;
		if (isKind(firstFunctionName)|| (isKind(firstColor)))
		{
			Token t =consume();
			return tmp;
		}
		throw new SyntaxException (t, "Syntax Error");
	}

		
	Expression FunctionApplication() throws SyntaxException 
	{
	//	System.out.println("**Function name"+t.kind);
		Token first =t;
		Token name = match(firstFunctionName);

		if(t.kind == Kind.LPAREN)
		{
			consume();
			Expression e0 = expression();
			match(Kind.RPAREN);	
			return new ExpressionFunctionAppWithExpressionArg(first, name, e0);
		}
		else if(t.kind == Kind.LSQUARE)
		{
			consume();
			Expression e0 = expression();
			match(Kind.COMMA);
			Expression e1 = expression();
			match(Kind.RSQUARE);
			return new ExpressionFunctionAppWithPixel(first, name, e0, e1);
		}
		else
			throw new SyntaxException (t, "Syntax Error");
	
	//	return null;		
	}

	protected Expression Primary() throws SyntaxException     //write protected as well
	{
	//	System.out.println("Inside primary"+ t.kind);
		Token first = t;
		
		if(t.kind == Kind.INTEGER_LITERAL)
		{
		//	System.out.println("integer found");
			Token Integer_Literal = consume();
			return new ExpressionIntegerLiteral(first, Integer_Literal);
		//	System.out.println(t.kind);
		}
		if(t.kind == Kind.BOOLEAN_LITERAL)
		{
			Token Boolean_Literal = consume();
			return new ExpressionBooleanLiteral(first, Boolean_Literal);
		}
		if(t.kind == Kind.FLOAT_LITERAL)
		{
			Token Float_Literal = consume();
			return new ExpressionFloatLiteral(first, Float_Literal);
		}
		
		else if(t.kind == Kind.LPAREN)   
		{
			consume();
			Expression e = expression();
			match(RPAREN);
			return e;
		} 
		else if (isKind(firstFunctionName) || isKind(firstColor)) 
		{		
			//consume();
			Expression e =FunctionApplication();
			return e;
		}
		else if(t.kind == Kind.IDENTIFIER) //Identifier + Pixel Selector
		{
		//	System.out.println("identifier found");
			Token name = consume();	
			if (t.kind == LSQUARE)
			{
				PixelSelector ps = pixelSelector();            
				return new ExpressionPixel ( first, name, ps);
			}	
			return new ExpressionIdent(first, name);
		}
		else if (isKind(firstPredefinedName))
		{
			Token t = consume();
			return new ExpressionPredefinedName(first,t);
		}
		else if(t.kind == Kind.LPIXEL)     //Comparison of LPixel twice
		{
			Expression e = PixelConstructor();
			return e;
		}
		else
			return null;
	//	System.out.println("leaving primary"+t.kind);
	}
	
	
	Expression UnaryExpressionNotPlusMinus() throws SyntaxException
	{
	//	System.out.println("Inside UnaryExpressionNotPlusMinus");
		Token first = t;
		if(t.kind == Kind.OP_EXCLAMATION)
		{
			Token op = consume();
			Expression e0 = UnaryExpression();
			return  new ExpressionUnary(first,op,e0);
	//		consume();
		} 
		 else 
		 {
			 return Primary();
		 }
		
	//	System.out.println("leaving ue!+- expression"+t.kind);
	}
	
	
	Expression UnaryExpression() throws SyntaxException
		{
		//	System.out.println("Inside UnaryExpression");
		Token first = t;
			if(t.kind == Kind.OP_PLUS)
			{
				Token op = consume();
				Expression e0 = UnaryExpression();
				return  new ExpressionUnary(first,op,e0);
			} 
			else if(t.kind == OP_MINUS)
			{
				Token op = consume();
				Expression e0 = UnaryExpression();
				return  new ExpressionUnary(first,op,e0);
			}
			else if(t.kind == OP_EXCLAMATION || isKind (firstPrimary))
			{
				return UnaryExpressionNotPlusMinus();
				
			}
			else
				throw new SyntaxException(t,"Illegal dec ");	
			
		//	System.out.println("leaving unary  expression"+t.kind);
		}
		

		Expression PowerExpression() throws SyntaxException 
	{
	//	System.out.println("Inside Power");
			Token first = t;
			Expression e0 = UnaryExpression();
		
			//consume();
			if( t.kind== OP_POWER)
			{
				Token op = consume();
				Expression e1 = PowerExpression();
				e0 = new ExpressionBinary(first, e0, op, e1);
			}		
			return e0;
	//		System.out.println("leaving pow expression"+t.kind);
	}
		

	Expression MultExpression() throws SyntaxException
		{
		//	System.out.println("Inside mult");
		Token first = t;
		Expression e0 = PowerExpression();
			while(t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == OP_MOD)
			{
				Token op = consume();
				Expression e1 = PowerExpression();
				e0 = new ExpressionBinary(first, e0, op, e1);
			}	
			return e0;
		//	System.out.println("leaving mult expression"+t.kind);
		}
	
	
		Expression AddExpression() throws SyntaxException 
		{
		//	System.out.println("Inside Add");
			Token first = t;
			Expression e0 = MultExpression();
			while(t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS)
			{
				Token op = consume();
				Expression e1 = MultExpression();
				e0 = new ExpressionBinary(first, e0, op, e1);
			}	
			return e0;
		//	System.out.println("leaving add expression"+t.kind);
		}


	Expression RelExpression() throws SyntaxException
	{		
	//	System.out.println("Inside Rel");
		Token first = t;
		Expression e0 = AddExpression();
		while(t.kind == Kind.OP_LT || t.kind == Kind.OP_GT || t.kind == Kind.OP_LE || t.kind == Kind.OP_GE) 
		{
			Token op = consume();
			Expression e1 = AddExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}	
		return e0;
	//	System.out.println("leaving rel expression"+t.kind);
	}
	

	Expression EqExpression() throws SyntaxException
	{		
	//	System.out.println("Inside Eq");
		Token first = t;
		Expression e0 = RelExpression();
		while(t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ)
		{
			Token op = consume();
			Expression e1 = RelExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}	
		return e0;
	//	System.out.println("leaving eq expression"+t.kind);
	}
	

	Expression AndExpression() throws SyntaxException
	{
	//	System.out.println("Inside and");
		Token first =t;
		Expression e0 = EqExpression();
		while(t.kind == Kind.OP_AND) 
		{
			Token op = consume();
			Expression e1 = EqExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	//	System.out.println("leaving And expression"+t.kind);
	}


	Expression OrExpression() throws SyntaxException
	{
	//	System.out.println("Inside or");
		
		Token first = t;
		Expression e0 = AndExpression();
		while(t.kind == Kind.OP_OR) 
		{
			Token op = consume();
			Expression e1 = AndExpression();
			e0 = new ExpressionBinary(first, e0, op, e1);
		}
		return e0;
	//	System.out.println("leaving or expression"+t.kind);
	}
	

	Expression expression() throws SyntaxException       
	{
		
	//	System.out.println("Inside exp");
		Token first=t;
		Expression e0 = OrExpression();		
		if(t.kind == Kind.OP_QUESTION)
		{
			consume();
			Expression e1 =expression();
			match(Kind.OP_COLON);
			Expression e2 =expression();
			e0 = new ExpressionConditional(first, e0, e1, e2);
		}
		
		return e0;
		//else
		//{
		//	throw new SyntaxException(t, "Illegal ");	
		//}
	//	System.out.println("leaving expression"+t.kind);
		
	}
	
	
}

