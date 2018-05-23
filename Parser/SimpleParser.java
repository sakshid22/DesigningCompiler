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

//// Cross check all header files with original files
public class SimpleParser {
	
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

	SimpleParser(Scanner scanner) {
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
	private Token match(Kind kind) throws SyntaxException {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
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
	
	public void parse() throws SyntaxException {
		program();
		matchEOF();
	}

	/*
	 * Program ::= Identifier Block
	 */
	public void program() throws SyntaxException {
		match(IDENTIFIER);
		block();
	}
	
	/*
	 * Block ::=  { (  (Declaration | Statement) ; )* }
	 */
	
	Kind[] firstProgram = {IDENTIFIER};
	Kind[] firstBlock = { LBRACE};
	Kind[] firstDec = { KW_int, KW_boolean, KW_image, KW_float, KW_filename };
	Kind[] firstStatement = {KW_input, KW_write, KW_while, KW_if, KW_show, KW_sleep,IDENTIFIER,
							KW_red, KW_green, KW_blue, KW_alpha };
	// firstStatement includes StateAssign + LHS +Color
	
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
	
	
	
	public void block() throws SyntaxException {
		match(LBRACE);
		while (isKind(firstDec)|isKind(firstStatement)) {
			if (isKind(firstDec)) {
//				System.out.println("declaration");
				declaration();
			} else if (isKind(firstStatement)) {
//				System.out.println("statement");
				statement();
			}
//	     	System.out.println(t.kind);
			match(SEMI);
		}
		match(RBRACE);

	}
	
	public void declaration() throws SyntaxException{
		//TODO
		 
	//	System.out.println("Inside declaration");
		Kind kind = t.kind;
		switch (kind) {
		case KW_int:
		{
			consume();
			match(IDENTIFIER);      
		}
		break;
		case KW_float:
		{
			consume();
			match(IDENTIFIER);
		}
		break;
		case KW_boolean:
		{
			consume();
			match(IDENTIFIER);
		}
		break;
		case KW_filename:
		{
			consume();
			match(IDENTIFIER);
		}
		break;
		case KW_image:
		{
			//System.out.println("Inside image");
			consume();
			match(IDENTIFIER);
			if(t.kind==LSQUARE)
			{
			//	System.out.println("Inside image2");
				consume();
				Expression();
				match(COMMA);
				Expression();
				match(RSQUARE);
			}			
		}
		break;
		
		default:
			throw new SyntaxException(t, "illegal dec");
		}
	//	System.out.println("leaving declaration"+t.kind);
	}
	
	public void statement() throws SyntaxException
	{
		//TODO
		//
	//	System.out.println("Inside statement");
		Kind kind = t.kind;
		switch(kind) 
		{
		case KW_input:
	//		System.out.println("Inside input");
			consume();
			match(IDENTIFIER);
			match(KW_from);
			match(OP_AT);
			Expression();
			break;
		
		case KW_write:
			consume();
			match(IDENTIFIER);
			match(KW_to);
			match(IDENTIFIER);
			break;
			
		case KW_while:
			consume();
			match(LPAREN);
			Expression();
			match(RPAREN);
			block();
			break;
		
		case KW_if:
			consume();
			match(LPAREN);
			Expression();
			match(RPAREN);
			block();		
			break;
		
		case KW_show:
			consume();
			Expression();
			break;
			
		case KW_sleep:
			consume();
			Expression();
			break;		
		
		case IDENTIFIER: 
			LHS();
			match(OP_ASSIGN);
			Expression();
			break;
		
		case KW_red: 
			LHS();
		//	System.out.println("lhs processed");
			match(OP_ASSIGN);
		//	System.out.println("assignment done");
			Expression();
			break;
		case KW_blue: 
			LHS();
			match(OP_ASSIGN);
			Expression();
			break;
		case KW_green: 
			LHS();
			match(OP_ASSIGN);
			Expression();
			break;
		case KW_alpha: 
			LHS();
			match(OP_ASSIGN);
			Expression();
			break;
				
		default: throw new SyntaxException(t, "Illegal Declaration");	
		}
		
	//	System.out.println("leaving statement"+t.kind);
		
	}

 
	void LHS() throws SyntaxException
	{
		if (t.kind== IDENTIFIER)
		{
			consume();
				if(isKind(firstPixelSelector))
				{
					PixelSelector();
				}
		}
		else if (isKind(firstColor))
		{
			consume();
			match(LPAREN);
			match(IDENTIFIER);
			PixelSelector();
			match (RPAREN);
		}	
	}

		
	void PixelConstructor() throws SyntaxException
	{
		if (t.kind == Kind.LPIXEL)                       //Comparing twice once while calling, once here but consuming here only
		{
			consume();
			Expression();
			match(COMMA);
			Expression();
			match(COMMA);
			Expression();
			match(COMMA);
			Expression();
			match(RPIXEL);			
		}
	}
	
	void PixelSelector() throws SyntaxException
	{
		if(t.kind == LSQUARE)
		{
			consume();
			Expression();
			match(COMMA);
			Expression();
			match(RSQUARE);
		}
		else
		throw new SyntaxException(t,"Illegal dec ");
	}

/* 
 *Not using this segment
	void PixelExpression() throws SyntaxException     //Clubbed with Identifier & PixelSelector, practically not using this func
	{
		if(t.kind == Kind.IDENTIFIER) 
		{			
			consume();
		} 
		else if(t.kind == Kind.LSQUARE)
		{
			consume();
			PixelSelector();
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
	void FunctionName() throws SyntaxException 
	{
	//	System.out.println("***Function name"+t.kind);
		if (isKind(firstFunctionName)|| (isKind(firstColor)))
		{
			consume();
		}

	}

		
	void FunctionApplication() throws SyntaxException 
	{
	//	System.out.println("**Function name"+t.kind);
		FunctionName();
		if(t.kind == Kind.LPAREN)
		{
			consume();
			Expression();
			match(Kind.RPAREN);			
		}
		else if(t.kind == Kind.LSQUARE)
		{
			consume();
			Expression();
			match(Kind.COMMA);
			Expression();
			match(Kind.RSQUARE);
		}
		else 
		{
			throw new SyntaxException(t, "Illegal token  " );
		}		
	}

	void Primary() throws SyntaxException 
	{
	//	System.out.println("Inside primary"+ t.kind);
		
		if((t.kind == Kind.INTEGER_LITERAL)|| (t.kind == Kind.BOOLEAN_LITERAL) ||(t.kind == Kind.FLOAT_LITERAL))
		{
		//	System.out.println("integer found");
			consume();
		//	System.out.println(t.kind);
		}
		else if(t.kind == Kind.LPAREN)
		{
			consume();
			Expression();
			match(RPAREN);
		} 
		else if (isKind(firstFunctionName) || isKind(firstColor)) 
		{
			
			//consume();
			FunctionApplication();
		}
		else if(t.kind == Kind.IDENTIFIER) //Identifier + Pixel Selector
		{
		//	System.out.println("identifier found");
			consume();	
			if (t.kind == LSQUARE)
			{
				PixelSelector();
			}		
		}
		else if (isKind(firstPredefinedName))
		{
			consume();
		}
		else if(t.kind == Kind.LPIXEL)     //Comparison of LPixel twice
		{
			PixelConstructor();
		}	
	//	System.out.println("leaving primary"+t.kind);
	}
	
	
	void UnaryExpressionNotPlusMinus() throws SyntaxException
	{
	//	System.out.println("Inside UnaryExpressionNotPlusMinus");
		if(t.kind == Kind.OP_EXCLAMATION)
		{
			consume();
			UnaryExpression();
		} 
		 else 
		 {
			 Primary();
		 }
		
	//	System.out.println("leaving ue!+- expression"+t.kind);
	}
	
	
		void UnaryExpression() throws SyntaxException
		{
		//	System.out.println("Inside UnaryExpression");
			
			if(t.kind == Kind.OP_PLUS)
			{
				consume();
				UnaryExpression();
			} 
			else if(t.kind == OP_MINUS)
			{
				consume();
				UnaryExpression();
			}
			else if(t.kind == OP_EXCLAMATION || isKind (firstPrimary))
			{
				UnaryExpressionNotPlusMinus();
			}
			else
				throw new SyntaxException(t,"Illegal dec ");	
			
		//	System.out.println("leaving unary  expression"+t.kind);
		}
		

	void PowerExpression() throws SyntaxException 
	{
	//	System.out.println("Inside Power");
		
		UnaryExpression();
		
			//consume();
			if( t.kind== OP_POWER)
			{
			consume();
			PowerExpression();
			}		
			
	//		System.out.println("leaving pow expression"+t.kind);
	}
		

		void MultExpression() throws SyntaxException
		{
		//	System.out.println("Inside mult");
			PowerExpression();
			while(t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == OP_MOD)
			{
				consume();
				PowerExpression();
			}		
		//	System.out.println("leaving mult expression"+t.kind);
		}
	
	
		void AddExpression() throws SyntaxException 
		{
		//	System.out.println("Inside Add");
			MultExpression();
			while(t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS)
			{
				consume();
				MultExpression();
			}	
			
		//	System.out.println("leaving add expression"+t.kind);
		}


	void RelExpression() throws SyntaxException
	{		
	//	System.out.println("Inside Rel");
		AddExpression();
		while(t.kind == Kind.OP_LT || t.kind == Kind.OP_GT || t.kind == Kind.OP_LE || t.kind == Kind.OP_GE) 
		{
			consume();
			AddExpression();
		}		
	//	System.out.println("leaving rel expression"+t.kind);
	}
	

	void EqExpression() throws SyntaxException
	{		
	//	System.out.println("Inside Eq");
		RelExpression();
		while(t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ)
		{
			consume();
			RelExpression();
		}		
	//	System.out.println("leaving eq expression"+t.kind);
	}
	

	void AndExpression() throws SyntaxException
	{
	//	System.out.println("Inside and");
		EqExpression();
		while(t.kind == Kind.OP_AND) 
		{
			consume();
			EqExpression();
		}		
	//	System.out.println("leaving And expression"+t.kind);
	}


	void OrExpression() throws SyntaxException
	{
	//	System.out.println("Inside or");
		AndExpression();
		while(t.kind == Kind.OP_OR) 
		{
			consume();
			AndExpression();
		}
	//	System.out.println("leaving or expression"+t.kind);
	}
	

	void Expression() throws SyntaxException        //Cross check this part - NULL Case
	{
	//	System.out.println("Inside exp");
		OrExpression();		
		if(t.kind == Kind.OP_QUESTION)
		{
			consume();
			Expression();
			match(Kind.OP_COLON);
			Expression();
		}
		//else
		//{
		//	throw new SyntaxException(t, "Illegal ");	
		//}
	//	System.out.println("leaving expression"+t.kind);
		
	}
	
	
}

