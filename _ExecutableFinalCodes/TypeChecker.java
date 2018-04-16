package cop5556sp18;

import cop5556sp18.Scanner.Kind;

import cop5556sp18.Scanner.Token;

import java.util.List;

import cop5556sp18.AST.ASTNode;

import cop5556sp18.AST.ASTVisitor;
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
import cop5556sp18.AST.LHS;
import cop5556sp18.AST.LHSIdent;
import cop5556sp18.AST.LHSPixel;
import cop5556sp18.AST.LHSSample;
import cop5556sp18.AST.PixelSelector;
import cop5556sp18.AST.Program;
import cop5556sp18.AST.StatementAssign;
import cop5556sp18.AST.StatementIf;
import cop5556sp18.AST.StatementInput;
import cop5556sp18.AST.StatementShow;
import cop5556sp18.AST.StatementSleep;
import cop5556sp18.AST.StatementWhile;
import cop5556sp18.AST.StatementWrite;

import cop5556sp18.Types.Type;

public class TypeChecker implements ASTVisitor
{
	SymbolTable symboltab = new SymbolTable();

	TypeChecker() {
	}

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;
		public SemanticException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}	
	// Name is only used for naming the output file. 
	// Visit the child block to type check program.
	
	//Added
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		program.block.visit(this, arg);
		return null;
	}
	
	//Added
	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symboltab.enterScope();
		List<ASTNode> decsOrStatements = block.decsOrStatements;
		
		for(int i = 0; i < decsOrStatements.size(); i++) {
			ASTNode nodeElem = decsOrStatements.get(i);
			nodeElem.visit(this, arg);
		}
		
		symboltab.leaveScope();
		return null;
	}

	//Added
	
	@Override
	public Object visitDeclaration(Declaration declaration, Object arg) throws Exception 
	{
		System.out.println("declaration visited");
		if(symboltab.ifredeclared(declaration))     //declare this func in symboltable
			throw new SemanticException (declaration.firstToken, " ");
		
		if(Types.getType(declaration.type) == null) {

			throw new SemanticException(declaration.firstToken,"Declaration: Declaration type is null.");

		}
		Expression e0 = declaration.width;
		Expression e1 = declaration.height;
		
		
		if(Types.getType(declaration.type) == Type.IMAGE)               // get type func in symbol table
		{
			if(e0 != null) e0.visit(this, arg);
			if(e1 != null) e1.visit(this, arg);
			
			if(e0 != null && e1 != null) {
				if(e0.utiltype != Type.INTEGER || e1.utiltype != Type.INTEGER)               // check if its OR
					throw new SemanticException(declaration.firstToken, "");	
			}
		}
		else {
			if(e0 != null || e1 != null) throw new SemanticException(null, "");
		}
				
		symboltab.addition(declaration.name, declaration);    // declare this func in symbol table
		return null;/*
		if(Types.getType(declaration.type) == null) {

			throw new SemanticException(declaration.firstToken,"Declaration: Declaration type is null.");

		}

		if(symboltab.lookup(declaration.name) == null) {

			symboltab.addition(declaration.name, declaration);

		} else {

			throw new SemanticException(declaration.firstToken,"Declaration: Name already exists.");

		}

		if(declaration.type == Kind.KW_image && declaration.width != null) {

			declaration.width.visit(this,arg);

			declaration.height.visit(this,arg);

			if(declaration.width.utiltype != Type.INTEGER || declaration.height.utiltype != Type.INTEGER) {

				throw new SemanticException(declaration.firstToken,"Declaration: Width and height must be of Integer type.");

			}

		}
		return null;*/
	}
					
	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg) throws Exception {
		System.out.println("statement write visited");
		statementWrite.sourceDec = symboltab.lookup(statementWrite.sourceName);
		
		if(statementWrite.sourceDec == null)
		{
			throw new SemanticException(statementWrite.firstToken, "Mismatch Type");
		}
		
		statementWrite.destDec = symboltab.lookup(statementWrite.destName);
		
		if(statementWrite.destDec==null)
		{
			throw new SemanticException(statementWrite.firstToken, "Mismatch Type");
		}
		
		System.out.println(statementWrite.sourceName);
		System.out.println(statementWrite.destName);
		
		if(Types.getType(statementWrite.sourceDec.type)!= Type.IMAGE)
		{
			throw new SemanticException(statementWrite.firstToken, "Mismatch Type");
		}
		
		if(Types.getType(statementWrite.destDec.type)!= Type.FILE)
		{
			throw new SemanticException(statementWrite.firstToken, "Mismatch Type");
		}
		
		return statementWrite;
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg) throws Exception
	{

		statementInput.dec = symboltab.lookup(statementInput.destName);
	
		if(statementInput.dec == null)
		{
			throw new SemanticException(statementInput.firstToken, "Mismatch Type");
		}
		
		statementInput.e.visit(this, arg);
		
		if(statementInput.e.utiltype!= Type.INTEGER)
		{
			throw new SemanticException(statementInput.firstToken, " ABC Mismatch Type");
		}
		
		return statementInput;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		Expression ex=pixelSelector.ex;
		Expression ey=pixelSelector.ey;
		
		if(ex!=null)
		{
			ex.visit(this, arg);
		}
		
		if(ey!=null)
		{
			ey.visit(this, arg);
		}	
		
		System.out.println("pixelselector");
		System.out.println(ex.utiltype + " " + ey.utiltype);
		
		if(ex.utiltype != ey.utiltype)
		{
			throw new SemanticException(pixelSelector.firstToken, "Mismatch Type");
		}
		
		if(ex.utiltype != Type.INTEGER && ex.utiltype != Type.FLOAT)
		{
			throw new SemanticException(pixelSelector.firstToken, " Index");
		}		
		
		return pixelSelector;			
	}

	@Override
	public Object visitExpressionConditional(ExpressionConditional expressionConditional, Object arg) throws Exception {
		Expression e0=expressionConditional.guard;
		Expression e1=expressionConditional.trueExpression;
		Expression e2=expressionConditional.falseExpression;
		
		if(e0!=null)  { e0.visit(this, arg);	}
		if(e1!=null)  { e1.visit(this, arg);	}
		if(e2!=null)  { e2.visit(this, arg);	}
		if(e0.utiltype!=Type.BOOLEAN) {	throw new SemanticException(expressionConditional.firstToken, "Mismatch Type");	}
		if(e1.utiltype!= e2.utiltype) {	throw new SemanticException(expressionConditional.firstToken, "Mismatch Type");	}
		
		expressionConditional.utiltype = e1.utiltype;
		return expressionConditional;		
	}
	
	//Added - Recheck according to inferred type rules - CHANGE all the statements
	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary, Object arg) throws Exception
	{
		Expression e0 = expressionBinary.leftExpression;
		Expression e1 = expressionBinary.rightExpression;
		Kind op = expressionBinary.op;
		
		//if(e0!=null)  { e0.visit(this, arg);	}
		//if(e1!=null)  { e1.visit(this, arg);	}
		e0.visit(this, arg);
		e1.visit(this, arg);
		
		if(e0.utiltype == Type.INTEGER && e1.utiltype == Type.INTEGER && 
			((op == Kind.OP_PLUS)||	(op == Kind.OP_MINUS) ||(op == Kind.OP_TIMES)||
			(op == Kind.OP_DIV)||(op == Kind.OP_MOD)||(op == Kind.OP_POWER)||
			(op == Kind.OP_AND)||(op == Kind.OP_OR)))
		{			expressionBinary.utiltype = Type.INTEGER;}
		
		else if(e0.utiltype==Type.FLOAT && e1.utiltype==Type.FLOAT && 
				((op == Kind.OP_PLUS)||	(op == Kind.OP_MINUS) ||(op == Kind.OP_TIMES)||
				(op == Kind.OP_DIV)||(op == Kind.OP_POWER)))
			{			expressionBinary.utiltype = Type.FLOAT;		}
		
		else if(e0.utiltype==Type.FLOAT && e1.utiltype==Type.INTEGER && 
				((op == Kind.OP_PLUS)||	(op == Kind.OP_MINUS) ||(op == Kind.OP_TIMES)||
				(op == Kind.OP_DIV)||(op == Kind.OP_POWER)))
			{			expressionBinary.utiltype = Type.FLOAT;		}
		
		else if(e0.utiltype==Type.INTEGER && e1.utiltype==Type.FLOAT && 
				((op == Kind.OP_PLUS)||	(op == Kind.OP_MINUS) ||(op == Kind.OP_TIMES)||
				(op == Kind.OP_DIV)||(op == Kind.OP_POWER)))
			{			expressionBinary.utiltype = Type.FLOAT;		}
		
		else if(e0.utiltype==Type.BOOLEAN && e1.utiltype==Type.BOOLEAN && 
				((op == Kind.OP_AND)||	(op == Kind.OP_OR)))
			{			expressionBinary.utiltype = Type.BOOLEAN;		}
		
		else if(e0.utiltype==Type.INTEGER && e1.utiltype==Type.INTEGER && 
				((op == Kind.OP_AND)||	(op == Kind.OP_OR)))
			{			expressionBinary.utiltype = Type.INTEGER;		}
		
		else if(e0.utiltype==Type.INTEGER && e1.utiltype==Type.INTEGER && 
				((op == Kind.OP_EQ)||	(op == Kind.OP_NEQ) ||(op == Kind.OP_GT)||
				(op == Kind.OP_GE)||(op == Kind.OP_LT)||(op == Kind.OP_LE)))
			{			expressionBinary.utiltype = Type.BOOLEAN;		}
		
		else if(e0.utiltype==Type.FLOAT && e1.utiltype==Type.FLOAT && 
				((op == Kind.OP_EQ)||	(op == Kind.OP_NEQ) ||(op == Kind.OP_GT)||
				(op == Kind.OP_GE)||(op == Kind.OP_LT)||(op == Kind.OP_LE)))
			{			expressionBinary.utiltype = Type.BOOLEAN;		}
		
		else if(e0.utiltype==Type.BOOLEAN && e1.utiltype==Type.BOOLEAN && 
				((op == Kind.OP_EQ)||	(op == Kind.OP_NEQ) ||(op == Kind.OP_GT)||
				(op == Kind.OP_GE)||(op == Kind.OP_LT)||(op == Kind.OP_LE)))
			{			expressionBinary.utiltype = Type.BOOLEAN;		}
		else {
			expressionBinary.utiltype = null;
		}
		return expressionBinary;
	}

	@Override
	public Object visitExpressionUnary(ExpressionUnary expressionUnary, Object arg) throws Exception {
		Expression e = expressionUnary.expression;
		if(e!=null)  { e.visit(this, arg);	}
	
		expressionUnary.utiltype=e.utiltype;
		return expressionUnary;
	}

	@Override
	public Object visitExpressionIntegerLiteral(ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		expressionIntegerLiteral.utiltype= Type.INTEGER;
		return expressionIntegerLiteral;				
	}

	@Override
	public Object visitBooleanLiteral(ExpressionBooleanLiteral expressionBooleanLiteral, Object arg) throws Exception {
		expressionBooleanLiteral.utiltype = Type.BOOLEAN;
		return expressionBooleanLiteral;	
	}

	@Override
	public Object visitExpressionPredefinedName(ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		expressionPredefinedName.utiltype = Type.INTEGER;
		return expressionPredefinedName;	
	}

	@Override
	public Object visitExpressionFloatLiteral(ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		expressionFloatLiteral.utiltype=Type.FLOAT;                   //Diff Type.FLOAT
		return expressionFloatLiteral;	
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg, Object arg)
			throws Exception {
		Expression e=expressionFunctionAppWithExpressionArg.e;
		Kind fun =expressionFunctionAppWithExpressionArg.function;
		
		//if(e!=null)  { e.visit(this, arg);	}
		expressionFunctionAppWithExpressionArg.e.visit(this, arg);
		
		if(e.utiltype==Type.INTEGER  && 
			((fun == Kind.KW_red)||	(fun == Kind.KW_green) ||(fun == Kind.KW_alpha)||
			(fun == Kind.KW_blue)||(fun == Kind.KW_abs)))
		{			expressionFunctionAppWithExpressionArg.utiltype=Type.INTEGER;		}
		
		else if(e.utiltype == Type.FLOAT && 
				((fun == Kind.KW_abs)||	(fun == Kind.KW_sin) ||(fun == Kind.KW_cos)||
				(fun == Kind.KW_atan)||(fun == Kind.KW_log)))
			{			expressionFunctionAppWithExpressionArg.utiltype=Type.FLOAT;		}
		
		else if(e.utiltype==Type.IMAGE && 
				((fun == Kind.KW_width)||	(fun == Kind.KW_height)) )
			{			expressionFunctionAppWithExpressionArg.utiltype=Type.INTEGER;		}
		
		else if(e.utiltype==Type.INTEGER && (fun == Kind.KW_float))
			{			expressionFunctionAppWithExpressionArg.utiltype=Type.FLOAT;		}
		
		else if(e.utiltype==Type.FLOAT && (fun == Kind.KW_float))
		{			expressionFunctionAppWithExpressionArg.utiltype=Type.FLOAT;		}
		
		else if(e.utiltype==Type.FLOAT && (fun == Kind.KW_int))
		{			expressionFunctionAppWithExpressionArg.utiltype=Type.INTEGER;		}
		
		else if(e.utiltype == Type.INTEGER && (fun == Kind.KW_int))
		{			expressionFunctionAppWithExpressionArg.utiltype=Type.INTEGER;		}
		else {
			expressionFunctionAppWithExpressionArg.utiltype=null;
		}
		return expressionFunctionAppWithExpressionArg;
	}

	//Added  - Complete with exact definitions
	@Override
	public Object visitExpressionFunctionAppWithPixel(ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		Expression e0 = expressionFunctionAppWithPixel.e0;
		Expression e1 = expressionFunctionAppWithPixel.e1;
		
		Kind name = expressionFunctionAppWithPixel.name;
		
		if(e0!=null) {e0.visit(this, null); }
		if(e1!=null) {e1.visit(this, null); }
		
		if(name == Kind.KW_cart_x || name == Kind.KW_cart_y)
		{
			if(e0.utiltype!=Type.FLOAT || e1.utiltype!=Type.FLOAT) {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken, "Mismatch Type");
			}
			
			expressionFunctionAppWithPixel.utiltype = Type.INTEGER;
		}
		
		
		if(name == Kind.KW_polar_a || name == Kind.KW_polar_r)
		{
			if(e0.utiltype!=Type.INTEGER || e1.utiltype!=Type.INTEGER) {
				throw new SemanticException(expressionFunctionAppWithPixel.firstToken, "Mismatch Type");	
			}
			
			expressionFunctionAppWithPixel.utiltype = Type.FLOAT;
		}
		
		
		return expressionFunctionAppWithPixel;
	}

	@Override
	public Object visitExpressionPixelConstructor(ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		Expression e0=expressionPixelConstructor.alpha;
		Expression e1=expressionPixelConstructor.red;
		Expression e2=expressionPixelConstructor.green;
		Expression e3=expressionPixelConstructor.blue;
		
		if(e0!=null) {e0.visit(this, null); }
		if(e1!=null) {e1.visit(this, null); }
		if(e2!=null) {e2.visit(this, null); }
		if(e3!=null) {e3.visit(this, null); }
					
		if(e0.utiltype!=Type.INTEGER || e1.utiltype!=Type.INTEGER ||e2.utiltype!=Type.INTEGER ||e3.utiltype!=Type.INTEGER  )
		{
			throw new SemanticException(expressionPixelConstructor.firstToken, "Mismatch Type");
		}	
		
		expressionPixelConstructor.utiltype = Type.INTEGER;
		return expressionPixelConstructor;		
	}

	//Added
	@Override
	public Object visitStatementAssign(StatementAssign statementAssign, Object arg) throws Exception {
		LHS lhs = statementAssign.lhs;
		Expression e = statementAssign.e;
		
		lhs.visit(this,arg);
		e.visit(this, arg);
		
		
		if(lhs.type != (e.utiltype))    // or Type Type.Expression
			throw new SemanticException(statementAssign.firstToken,"ILLEGAL TYPE");
		
		return statementAssign;
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg) throws Exception {
		statementShow.e.visit(this, arg);
		
		//exp.visit(this, arg);
		if((statementShow.e.utiltype != (Type.INTEGER)) && (statementShow.e.utiltype != (Type.BOOLEAN)) &&
			(statementShow.e.utiltype != (Type.FLOAT)) && (statementShow.e.utiltype != (Type.IMAGE)) )	{	
		throw new SemanticException(statementShow.firstToken,"ILLEGAL TYPE");		
		}
		return statementShow;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel, Object arg) throws Exception {
		Declaration dec = symboltab.lookup(expressionPixel.name);
		expressionPixel.pixelSelector.visit(this, arg);
		
		if(dec == null)
		{
			throw new SemanticException(expressionPixel.firstToken, "Mismatch Type");
		}
		
		if(Types.getType(dec.type)!=Type.IMAGE)
		{
			throw new SemanticException(expressionPixel.firstToken, "Mismatch Type");
		}
		
		expressionPixel.utiltype = Type.INTEGER;
		return expressionPixel;
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent, Object arg) throws Exception {

		Declaration dec = symboltab.lookup(expressionIdent.name);
		if(dec==null)
		{
			throw new SemanticException(expressionIdent.firstToken, "PQR Mismatch Type");
		}
		
		expressionIdent.utiltype = Types.getType(dec.type);
		return expressionIdent;		
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg) throws Exception {
		System.out.println("lhsSample name:" + lhsSample.name);
		lhsSample.pixelSelector.visit(this, arg);
		lhsSample.dec = symboltab.lookup(lhsSample.name);
		
		if(lhsSample.dec==null)
		{
			throw new SemanticException(lhsSample.firstToken, "Mismatch Type");
		}
		if(Types.getType(lhsSample.dec.type)== Type.IMAGE)
		{
			lhsSample.type = Type.INTEGER;
		} else {
			throw new SemanticException(lhsSample.firstToken, "Mismatch Type");
		}
		
		//lhsSample.pixelSelector.visit(this, arg);
		//lhsSample.type = Type.INTEGER;
		return lhsSample;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg) throws Exception {
		System.out.println("visiting lhsPixel");
		lhsPixel.dec = symboltab.lookup(lhsPixel.name);
		
		if(lhsPixel.dec==null)
		{
			throw new SemanticException(lhsPixel.firstToken, "Mismatch Type");
		}
		
		if(Types.getType(lhsPixel.dec.type)!=Type.IMAGE)
		{
			throw new SemanticException(lhsPixel.firstToken, "Mismatch Type");
		}
		
		lhsPixel.pixelSelector.visit(this, arg);
		lhsPixel.type = Type.INTEGER;
		return lhsPixel;
	}

	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg) throws Exception {
		lhsIdent.dec = symboltab.lookup(lhsIdent.name);
		
		if(lhsIdent.dec==null)
		{
			throw new SemanticException(lhsIdent.firstToken, "Mismatch Type");
		}
		
		lhsIdent.type = Types.getType(lhsIdent.dec.type);
		return lhsIdent;  
	}

	//Added
	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg) throws Exception {
		Expression exp = statementIf.guard;
		exp.visit(this,arg);
		
		if(exp.utiltype != (Type.BOOLEAN))
		throw new SemanticException(statementIf.firstToken,"ILLEGAL TYPE");
		
		statementIf.b.visit(this,arg);
		return statementIf;				
	}

	
	//Added
	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg) throws Exception {
		Expression exp = statementWhile.guard;
		exp.visit(this,arg);
		
		if(exp.utiltype != (Type.BOOLEAN))    // Check KW_boolean or BOOLEAN_LITERAL
			throw new SemanticException(statementWhile.firstToken,"ILLEGAL TYPE");
		
		statementWhile.b.visit(this, arg);
		return statementWhile;		
	}

	
	//Added
	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg) throws Exception {
		Expression exp = statementSleep.duration;
		exp.visit(this,arg);
		
		if(exp.utiltype != (Type.INTEGER))
			throw new SemanticException(statementSleep.firstToken,"ILLEGAL TYPE");
			
		return statementSleep;
	}


}
