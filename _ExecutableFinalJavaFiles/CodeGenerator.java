/**
 * Starter code for CodeGenerator.java used n the class project in COP5556 Programming Language Principles 
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
import cop5556sp18.Scanner.Kind;

import cop5556sp18.Scanner.Token;
import cop5556sp18.TypeChecker.SemanticException;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp18.Types.Type;
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

import cop5556sp18.CodeGenUtils;

public class CodeGenerator implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */

	static final int Z = 255;

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	int slot_counter;
	
	MethodVisitor mv; // visitor of method currently under construction
	
	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	final int defaultWidth;
	final int defaultHeight;
	// final boolean itf = false;
	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 * @param defaultWidth
	 *            default width of images
	 * @param defaultHeight
	 *            default height of images
	 */
	public CodeGenerator(boolean DEVEL, boolean GRADE, String sourceFileName,
			int defaultWidth, int defaultHeight) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
		this.slot_counter = 0;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		for (ASTNode node : block.decsOrStatements) {
			node.visit(this, null);
		}
		return null;
	}

	//CrossCheck
	@Override
	public Object visitBooleanLiteral(
			ExpressionBooleanLiteral expressionBooleanLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionBooleanLiteral.value);
		return null;
	}

	@Override
	public Object visitDeclaration(Declaration declaration, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("declaration visited");	
		declaration.current_slot = ++slot_counter;
		
		Type type = Types.getType(declaration.type);
        switch(type){
        case INTEGER:   
        	mv.visitInsn(ACONST_NULL);
        	mv.visitVarInsn(ASTORE, declaration.current_slot);
            break;                
        case FLOAT:     
        	mv.visitInsn(ACONST_NULL);
        	mv.visitVarInsn(ASTORE, declaration.current_slot);
            break;
        case BOOLEAN:   
        	mv.visitInsn(ACONST_NULL);
        	mv.visitVarInsn(ASTORE, declaration.current_slot);
            break;
        case FILE:   
        	mv.visitInsn(ACONST_NULL);
        	mv.visitVarInsn(ASTORE, declaration.current_slot);
            break;
        case IMAGE:
        	if(declaration.height != null && declaration.width != null)
        	{
        		declaration.width.visit(this, null);
        		declaration.height.visit(this, null);        		
        	}
        	else
        	{
        		mv.visitLdcInsn(defaultWidth);
        		mv.visitLdcInsn(defaultHeight);
        	}
        	
        	mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "makeImage", RuntimeImageSupport.makeImageSig, false);
        	mv.visitVarInsn(ASTORE, declaration.current_slot);
        default:
           break;
        }
        return null;
	}
	
	@Override
	public Object visitExpressionBinary(ExpressionBinary expressionBinary,
            Object arg) throws Exception {
		System.out.println("Exp Binary visted");
        expressionBinary.leftExpression.visit(this, arg);
        expressionBinary.rightExpression.visit(this, arg);
        Kind op = expressionBinary.op;
        Expression leftExp = expressionBinary.leftExpression;
        Type leftExpType = leftExp.getType();
        Expression rightExp = expressionBinary.rightExpression;
        Type rightExpType = rightExp.getType();
        
        Label startLabel = new Label();
		Label endLabel = new Label();
        switch (op) {
            case OP_PLUS:
                    if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER)
                    {
                            mv.visitInsn(IADD);    
                    } 
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.FLOAT) 
                    {
                            mv.visitInsn(FADD);
                    } 
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.INTEGER)
                    {
                            mv.visitInsn(I2F);
                            mv.visitInsn(FADD);
                    } 
                    else if(leftExpType == Type.INTEGER && rightExpType == Type.FLOAT)
                    {
                            mv.visitInsn(SWAP);
                            mv.visitInsn(I2F);
                            mv.visitInsn(SWAP);
                            mv.visitInsn(FADD);
                    } 
                    else 
                    {
                            throw new UnsupportedOperationException("ADD operation is only supported for INTEGER and FLOAT operands.");
                    }
                break;
            case OP_MINUS: 
                    if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER)
                    {
                            mv.visitInsn(ISUB);    
                    } 
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.FLOAT) 
                    {
                            mv.visitInsn(FSUB);
                    }
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.INTEGER)
                    {
                            mv.visitInsn(I2F);
                            mv.visitInsn(FSUB);
                    } 
                    else if(leftExpType == Type.INTEGER && rightExpType == Type.FLOAT) 
                    {
                            mv.visitInsn(SWAP);
                            mv.visitInsn(I2F);
                            mv.visitInsn(SWAP);
                            mv.visitInsn(FSUB);
                    }
                    else
                    {
                            throw new UnsupportedOperationException("SUB operation is only supported for INTEGER and FLOAT operands.");
                    }
                break;
            case OP_TIMES: 
                    if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER)
                    {
                            mv.visitInsn(IMUL);    
                    }
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.FLOAT) 
                    {
                            mv.visitInsn(FMUL);
                    }
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.INTEGER) 
                    {
                            mv.visitInsn(I2F);
                            mv.visitInsn(FMUL);
                    }
                    else if(leftExpType == Type.INTEGER && rightExpType == Type.FLOAT) 
                    {
                            mv.visitInsn(SWAP);
                            mv.visitInsn(I2F);
                            mv.visitInsn(SWAP);
                            mv.visitInsn(FMUL);
                    }
                    else 
                    {
                        throw new UnsupportedOperationException("MUL operation is only supported for INTEGER and FLOAT operands.");
                    }
                break;
            case OP_DIV:
                    if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER) 
                    {
                            mv.visitInsn(IDIV);    
                    }
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.FLOAT) 
                    {
                            mv.visitInsn(FDIV);
                    } 
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.INTEGER) 
                    {
                            mv.visitInsn(I2F);
                            mv.visitInsn(FDIV);
                    }
                    else if(leftExpType == Type.INTEGER && rightExpType == Type.FLOAT)
                    {
                            mv.visitInsn(SWAP);
                            mv.visitInsn(I2F);
                            mv.visitInsn(SWAP);
                            mv.visitInsn(FDIV);
                    } else
                    {
                            throw new UnsupportedOperationException("DIV operation is only supported for INTEGER and FLOAT operands.");
                    }
                break;
            case OP_POWER:
                    if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER)
                    {
                    		mv.visitInsn(I2F);
                    		mv.visitInsn(SWAP);
                    		mv.visitInsn(I2F);
                    		mv.visitInsn(SWAP);
                    		mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "pow", RuntimeFunctions.powSignature, false);    
                    		mv.visitInsn(F2I);
                    }
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.FLOAT) 
                    {
                    		mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "pow", RuntimeFunctions.powSignature, false);
                    } 
                    else if(leftExpType == Type.FLOAT && rightExpType == Type.INTEGER)
                    {
                    	mv.visitInsn(I2F);
                        mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "pow", RuntimeFunctions.powSignature, false);
                    } 
                    else if(leftExpType == Type.INTEGER && rightExpType == Type.FLOAT)
                    {
                        mv.visitInsn(SWAP);
                        mv.visitInsn(I2F);
                        mv.visitInsn(SWAP);
                        mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "pow", RuntimeFunctions.powSignature, false);
                    }
                    else
                    {
                        throw new UnsupportedOperationException("EXPONENT operation is only supported for INTEGER and FLOAT operands.");
                    }
                break;
            case OP_MOD:
                if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER) 
                        mv.visitInsn(IREM);    
                else 
                    throw new UnsupportedOperationException("MOD operation is only supported for INTEGER operands.");
                break;
            case OP_AND:
                    if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER) 
                        mv.visitInsn(IAND);
                    else if(leftExpType == Type.BOOLEAN && rightExpType == Type.BOOLEAN) 
                    	mv.visitInsn(IAND);
                    else 
                        throw new UnsupportedOperationException("AND operation is only supported for INTEGER operands.");
                break;
            case OP_OR:
                    if(leftExpType == Type.INTEGER && rightExpType == Type.INTEGER) 
                        mv.visitInsn(IOR);
                    else if(leftExpType == Type.BOOLEAN && rightExpType == Type.BOOLEAN) 
                    	mv.visitInsn(IOR);
                    else
                        throw new UnsupportedOperationException("OR operation is only supported for INTEGER operands.");
                break;
                
            case OP_EQ:
            {
                
                if(leftExpType == Type.INTEGER || leftExpType == Type.BOOLEAN) 
                {
                    mv.visitJumpInsn(IF_ICMPEQ, startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitLabel(endLabel); 
                }
                else if(leftExpType==Type.FLOAT )
                {
                    mv.visitInsn(FCMPL);
                    mv.visitJumpInsn(IFNE, startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitLabel(endLabel);
                }
                  else 
                  {
                          throw new UnsupportedOperationException("ADD operation is only supported for INTEGER and FLOAT operands.");
                  }
            }
            break;
          
       //          throw new UnsupportedOperationException("AND operation is only supported for INTEGER operands.");
             
                
            case OP_NEQ:
            {
                
                if(leftExpType == Type.INTEGER || leftExpType == Type.BOOLEAN) 
                {
                    mv.visitJumpInsn(IF_ICMPNE, startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitLabel(endLabel); 
                }
                else if(leftExpType==Type.FLOAT )
                {
                    mv.visitInsn(FCMPL);
                    mv.visitJumpInsn(IFEQ, startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitLabel(endLabel);
                }
                  else 
                  {
                          throw new UnsupportedOperationException("ADD operation is only supported for INTEGER and FLOAT operands.");
                  }
            }
              break;
     
            case OP_GE:
                {
                    
                    if( leftExpType == Type.INTEGER || leftExpType == Type.BOOLEAN)
                    {
                        mv.visitJumpInsn(IF_ICMPGE, startLabel);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(startLabel);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endLabel); 
                    }
                    else if(leftExpType == Type.FLOAT)
                    {
                        mv.visitInsn(FCMPL);
                    mv.visitJumpInsn(IFLT, startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitLabel(endLabel);
                    }
                }
               break;
                
    
                
            case OP_LE:
                    {
                        if( leftExpType == Type.INTEGER || leftExpType == Type.BOOLEAN)
                        {
                            mv.visitJumpInsn(IF_ICMPLE, startLabel);
                            mv.visitInsn(ICONST_0);
                            mv.visitJumpInsn(GOTO, endLabel);
                            mv.visitLabel(startLabel);
                            mv.visitInsn(ICONST_1);
                            mv.visitLabel(endLabel); 
                        }
                        else if(leftExpType == Type.FLOAT && rightExpType == Type.FLOAT) {
                            mv.visitInsn(FCMPL);
		                    mv.visitJumpInsn(IFGT, startLabel);
		                    mv.visitInsn(ICONST_1);
		                    mv.visitJumpInsn(GOTO, endLabel);
		                    mv.visitLabel(startLabel);
		                    mv.visitInsn(ICONST_0);
		                    mv.visitLabel(endLabel);
		                }
                    }
                    break;       
                
            case OP_GT:
            {
                
                if(leftExpType == Type.INTEGER || leftExpType == Type.BOOLEAN) 
                {
                    mv.visitJumpInsn(IF_ICMPLE, startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitLabel(endLabel); 
                }
                else if(leftExpType==Type.FLOAT )
                {
                    mv.visitInsn(FCMPL);
                    mv.visitJumpInsn(IFLE, startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitLabel(endLabel);
                }
            }
            break;                        
       
            case OP_LT:
            {
                
                if(leftExpType == Type.INTEGER || leftExpType == Type.BOOLEAN) 
                {
                    mv.visitJumpInsn(IF_ICMPLT, startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitLabel(endLabel); 
                    
                //    mv.visitJumpInsn(IF_ICMPLT, startLabel);;
                //    mv.visitLdcInsn(false);
                }
                else if(leftExpType==Type.FLOAT )
                {
                    mv.visitInsn(FCMPL);
                    mv.visitJumpInsn(IFGE, startLabel);
                    mv.visitInsn(ICONST_1);
                    mv.visitJumpInsn(GOTO, endLabel);
                    mv.visitLabel(startLabel);
                    mv.visitInsn(ICONST_0);
                    mv.visitLabel(endLabel);
                }
            }
            break;               
            
            default:
                throw new UnsupportedOperationException("Operation not supported.");
        }    	
       
           
        return null;
    }	

	@Override
	public Object visitExpressionConditional(
			ExpressionConditional expressionConditional, Object arg)
		throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Conditional visted");
		
		Expression e0=expressionConditional.guard;
		Expression e1=expressionConditional.trueExpression;
		Expression e2=expressionConditional.falseExpression;
		
		Label startLabel= new Label();
		Label endLabel = new Label();
		
		e0.visit(this, arg);
		mv.visitJumpInsn(IFNE, startLabel);
		
		e2.visit(this, arg);
		mv.visitJumpInsn(GOTO, endLabel);
		
		mv.visitLabel(startLabel);
		e1.visit(this, arg);
		
		mv.visitLabel(endLabel);
		
		return null;
	//	throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionFloatLiteral(
			ExpressionFloatLiteral expressionFloatLiteral, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		mv.visitLdcInsn(expressionFloatLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithExpressionArg(
			ExpressionFunctionAppWithExpressionArg expressionFunctionAppWithExpressionArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("FuncArg visted");
		
		Expression e=expressionFunctionAppWithExpressionArg.e;
		Kind fun =expressionFunctionAppWithExpressionArg.function;
		
		e.visit(this, arg);
		
		System.out.println("func args"+fun);
		
		switch (expressionFunctionAppWithExpressionArg.function)
		{
			case KW_sin:
				System.out.println("arg sin visited");
				if(e.utiltype == Type.FLOAT)
					mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"sin", RuntimeFunctions.sinSignature,false);
			break;			
			case KW_cos:
				if(e.utiltype == Type.FLOAT)
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"cos", RuntimeFunctions.cosSignature,false);
			break;
			case KW_atan:
				if(e.utiltype == Type.FLOAT)
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"atan", RuntimeFunctions.atanSignature,false);
				break;
			case KW_log:
				if(e.utiltype == Type.FLOAT)
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"log", RuntimeFunctions.logSignature,false);
				break;
			case KW_abs:
				if(e.utiltype == Type.FLOAT)
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"absF", RuntimeFunctions.absFSignature,false);
				else
				if(e.utiltype == Type.INTEGER)
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"absI", RuntimeFunctions.absISignature,false);
				break;
			case KW_int:
				if(e.utiltype == Type.FLOAT) mv.visitInsn(F2I);
				break;
			case KW_float:
				if(e.utiltype == Type.INTEGER) mv.visitInsn(I2F);
				break;
			case KW_height:				
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className,"getHeight", RuntimeImageSupport.getHeightSig,false);	
				break;
			case KW_width:			
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className,"getWidth", RuntimeImageSupport.getWidthSig,false);
				break;
			case KW_red:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className,"getRed", RuntimePixelOps.getRedSig,false);
				break;
			case KW_green:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className,"getGreen", RuntimePixelOps.getGreenSig,false);
				break;
			case KW_blue:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className,"getBlue", RuntimePixelOps.getBlueSig,false);
				break;
			case KW_alpha:
				mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className,"getAlpha", RuntimePixelOps.getAlphaSig,false);
				break;
				

				
			default:
				break;
		}
		return null;
	}

	@Override
	public Object visitExpressionFunctionAppWithPixel(
			ExpressionFunctionAppWithPixel expressionFunctionAppWithPixel,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("FunAppPixel visted");
		Expression e0 = expressionFunctionAppWithPixel.e0;
		Expression e1 = expressionFunctionAppWithPixel.e1;
	
		if(e0!=null) {e0.visit(this, null); }
		if(e1!=null) {e1.visit(this, null); }
		
		switch(expressionFunctionAppWithPixel.name)
		{
			case KW_cart_x:
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"cart_x", RuntimeFunctions.cart_xSignature,false);
				break;
			case KW_cart_y:
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"cart_y", RuntimeFunctions.cart_ySignature,false);
				break;
			case KW_polar_r:
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"polar_r", RuntimeFunctions.polar_rSignature,false);
				break;
			case KW_polar_a:
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className,"polar_a", RuntimeFunctions.polar_aSignature,false);
				break;
			default:
				break;
		}
		return null;	
//		throw new UnsupportedOperationException();
		
	}

	@Override
	public Object visitExpressionIdent(ExpressionIdent expressionIdent,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("expression ident ");
		
		if(Types.getType(expressionIdent.declaration.type) == Type.INTEGER || Types.getType(expressionIdent.declaration.type) == Type.BOOLEAN) {
			mv.visitVarInsn(ILOAD, expressionIdent.declaration.current_slot);
		}
		else if(Types.getType(expressionIdent.declaration.type) == Type.FLOAT) {
			mv.visitVarInsn(FLOAD, expressionIdent.declaration.current_slot);
		}
		else if(Types.getType(expressionIdent.declaration.type) == Type.FILE || Types.getType(expressionIdent.declaration.type) == Type.IMAGE) {
			mv.visitVarInsn(ALOAD, expressionIdent.declaration.current_slot);
		}
		
		return null;	
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionIntegerLiteral(
			ExpressionIntegerLiteral expressionIntegerLiteral, Object arg)
			throws Exception {
		// This one is all done!
		mv.visitLdcInsn(expressionIntegerLiteral.value);
		return null;
	}

	@Override
	public Object visitExpressionPixel(ExpressionPixel expressionPixel,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Exp Pixel visted");
		
		mv.visitVarInsn(ALOAD, expressionPixel.declaration.current_slot);
		expressionPixel.pixelSelector.visit(this, arg);
		
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "getPixel", RuntimeImageSupport.getPixelSig, false);
		return null;
		
	//	throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionPixelConstructor(
			ExpressionPixelConstructor expressionPixelConstructor, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Exp Pixel Constructor visted");
		
		Expression e0 = expressionPixelConstructor.alpha;
		Expression e1 = expressionPixelConstructor.red;
		Expression e2 = expressionPixelConstructor.green;
		Expression e3 = expressionPixelConstructor.blue;
		
		if(e0!=null) {e0.visit(this, arg); }
		if(e1!=null) {e1.visit(this, arg); }
		if(e2!=null) {e2.visit(this, arg); }
		if(e3!=null) {e3.visit(this, arg); }

		//Leave 4 values on stack - Check this
		
		//Check this		
		
		mv.visitMethodInsn(INVOKESTATIC, RuntimePixelOps.className,"makePixel", RuntimePixelOps.makePixelSig,false);
				
		return null;
//		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpressionPredefinedName(
			ExpressionPredefinedName expressionPredefinedName, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Kind token=expressionPredefinedName.name;
		if(token==Kind.KW_Z)
		{
			mv.visitLdcInsn(new Integer(255)); 
		}
		if(token==Kind.KW_default_width)
		{
			mv.visitLdcInsn(defaultWidth);
		}
		if(token==Kind.KW_default_height)
		{
			mv.visitLdcInsn(defaultHeight);
		}
		return null;
	//	throw new UnsupportedOperationException();
	}

	@Override
	 public Object visitExpressionUnary(ExpressionUnary expressionUnary,
	            Object arg) throws Exception {
	        expressionUnary.expression.visit(this, arg);
	        Type expUnaryType = expressionUnary.getType();
	        Kind op = expressionUnary.op;
	        switch(expUnaryType) {
	        case INTEGER:
	            switch(op) {
	            case OP_PLUS:
	                break;
	            case OP_MINUS:
	                mv.visitInsn(INEG);
	                break;
	            case OP_EXCLAMATION:
	                mv.visitInsn(ICONST_M1);
	                mv.visitInsn(IXOR);
	                break;
	            default:
	                break;
	            }
	            break;
	        case FLOAT:
	            switch(op) {
	            case OP_PLUS:
	                break;
	            case OP_MINUS:
	                mv.visitInsn(FNEG);
	                break;
	            default:
	                break;
	            }
	            break;
	        case BOOLEAN:
	            switch(op) {
	            case OP_EXCLAMATION:
	                mv.visitInsn(ICONST_1);
	                mv.visitInsn(IXOR);
	                break;
	            default:
	                break;
	            }
	            break;
	        default:
	            break;
	        }
	        return null;
	    }


	@Override
	public Object visitLHSIdent(LHSIdent lhsIdent, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("lhs ident visited "+lhsIdent.type);
		 Type decType = Types.getType(lhsIdent.dec.type);
	        switch(decType) {
	        case INTEGER:
	        	mv.visitVarInsn(ISTORE, lhsIdent.dec.current_slot);
	            break;
	        case BOOLEAN:
	        	mv.visitVarInsn(ISTORE, lhsIdent.dec.current_slot);
	            break;
	        case FLOAT:
	        	mv.visitVarInsn(FSTORE, lhsIdent.dec.current_slot);
	            break;
	        case IMAGE:
	            mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className,"deepCopy", RuntimeImageSupport.deepCopySig, false);
	            mv.visitVarInsn(ASTORE, lhsIdent.dec.current_slot);
	            break;
	        case FILE:
	        	mv.visitVarInsn(ASTORE, lhsIdent.dec.current_slot);
	        	break;
	        default:
	            break;
	        }
	        return null;
	}

	@Override
	public Object visitLHSPixel(LHSPixel lhsPixel, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("lhs pixel visited");

		mv.visitVarInsn(ALOAD, lhsPixel.dec.current_slot);
		lhsPixel.pixelSelector.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className,"setPixel", RuntimeImageSupport.setPixelSig,false);
	
		return null;
	}

	@Override
	public Object visitLHSSample(LHSSample lhsSample, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("LHSSample visted");
		
	    mv.visitVarInsn(ALOAD,lhsSample.dec.current_slot);

	    lhsSample.pixelSelector.visit(this, arg);
		
	    if(lhsSample.color.equals(Kind.KW_green))
		{
			mv.visitLdcInsn(RuntimePixelOps.GREEN);
		}
		else if(lhsSample.color.equals(Kind.KW_red))
		{
			mv.visitLdcInsn(RuntimePixelOps.RED);
		}
		else if(lhsSample.color.equals(Kind.KW_blue))
		{
			mv.visitLdcInsn(RuntimePixelOps.BLUE);
		}
		else if(lhsSample.color.equals(Kind.KW_alpha))
		{		
			mv.visitLdcInsn(RuntimePixelOps.ALPHA);
		}	
		
	    mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className,"updatePixelColor", RuntimeImageSupport.updatePixelColorSig,false);
		
		return null;   //check if return null;
	}

	@Override
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		pixelSelector.ex.visit(this, arg);
		pixelSelector.ey.visit(this, arg);
		
        if(pixelSelector.ex.getType() == Type.FLOAT && pixelSelector.ey.getType() == Type.FLOAT) {
        	
            mv.visitInsn(POP);
            mv.visitInsn(POP);
        	
         	pixelSelector.ex.visit(this, arg);
			mv.visitInsn(F2D);
			pixelSelector.ey.visit(this, arg);
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "cos", "(D)D", false);
			mv.visitInsn(DMUL);
			mv.visitInsn(D2I);
			
			pixelSelector.ex.visit(this, arg);
			mv.visitInsn(F2D);
			pixelSelector.ey.visit(this, arg);
			mv.visitInsn(F2D);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "sin", "(D)D", false);
			mv.visitInsn(DMUL);
			mv.visitInsn(D2I);
        	
        }
		
		return null;
//		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO refactor and extend as necessary
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cw = new ClassWriter(0); //If the call to mv.visitMaxs(1, 1) crashes,
		// it is
		// sometime helpful to
		// temporarily run it without COMPUTE_FRAMES. You probably
		// won't get a completely correct classfile, but
		// you will be able to see the code that was
		// generated.
		className = program.progName;
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null,
				"java/lang/Object", null);
		cw.visitSource(sourceFileName, null);

		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
				"([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();

		// add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);

		CodeGenUtils.genLog(DEVEL, mv, "entering main");

		program.block.visit(this, arg);

		// generates code to add string to log
		CodeGenUtils.genLog(DEVEL, mv, "leaving main");

		// adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		// adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart,
				mainEnd, 0);
		// Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the
		// constructor,
		// asm will calculate this itself and the parameters are ignored.
		// If you have trouble with failures in this routine, it may be useful
		// to temporarily change the parameter in the ClassWriter constructor
		// from COMPUTE_FRAMES to 0.
		// The generated classfile will not be correct, but you will at least be
		// able to see what is in it.
		mv.visitMaxs(0, 0);

		// terminate construction of main method
		mv.visitEnd();

		// terminate class construction
		cw.visitEnd();

		// generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitStatementAssign(StatementAssign statementAssign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("assign visited");
		
		statementAssign.e.visit(this, arg);
		statementAssign.lhs.visit(this, arg);
		
		return null;
	//	throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementIf(StatementIf statementIf, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("StatemtIf visted");

		{
			Label afterIf = new Label();
			statementIf.guard.visit(this, arg);
			mv.visitJumpInsn(IFEQ, afterIf); 
			Label startIf = new Label();
			mv.visitLabel(startIf);
			statementIf.b.visit(this, arg); 
			Label endIf = new Label();
			mv.visitLabel(endIf);
			mv.visitLabel(afterIf); 
			Label endAfterIf = new Label();
			mv.visitLabel(endAfterIf);
		}
			return null;
		
		/*		
		statementIf.guard.visit(this, arg);
		Label AFTER = new Label();
		mv.visitJumpInsn(IFEQ, AFTER);
		statementIf.b.visit(this, arg);
		mv.visitLabel(AFTER);
		return null;
//		throw new UnsupportedOperationException();
 * */
		
 
	}

	@Override
	public Object visitStatementInput(StatementInput statementInput, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Input visted");
		mv.visitVarInsn(ALOAD,0);
		
		statementInput.e.visit(this,null);
		mv.visitInsn(AALOAD);
		
		
		switch(Types.getType(statementInput.dec.type)){
			case INTEGER:{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I",false);
				mv.visitVarInsn(ISTORE, statementInput.dec.current_slot);
				break;
			}
			case BOOLEAN:{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z",false);
				mv.visitVarInsn(ISTORE, statementInput.dec.current_slot);
				break;
			}
			case FLOAT:{
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F",false);
				mv.visitVarInsn(FSTORE, statementInput.dec.current_slot);
				break;
			}			
			//change this one
			case IMAGE:{
				
				Declaration declaration_Image = (Declaration) statementInput.dec;
				if(declaration_Image.width != null && declaration_Image.height != null)
				{
					declaration_Image.width.visit(this, arg);
			         mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
					declaration_Image.height.visit(this, arg);
			         mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				}
				else
				{
					mv.visitInsn(ACONST_NULL);
					mv.visitInsn(ACONST_NULL);
				}
				
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className,"readImage", RuntimeImageSupport.readImageSig,false);
				mv.visitVarInsn(ASTORE, statementInput.dec.current_slot);
			}
			break;
			case FILE:{
				mv.visitVarInsn(ASTORE, statementInput.dec.current_slot);
	//			mv.visitFieldInsn(GETSTATIC, className, statementInput.destName, CodeGenUtils.getJVMType(Type.FILE));
			}break;
		}
		return null;
//		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementShow(StatementShow statementShow, Object arg)
			throws Exception {
		/**
		 * TODO refactor and complete implementation.
		 * 
		 * For integers, booleans, and floats, generate code to print to
		 * console. For images, generate code to display in a frame.
		 * 
		 * In all cases, invoke CodeGenUtils.genLogTOS(GRADE, mv, type); before
		 * consuming top of stack.
		 */
		statementShow.e.visit(this, arg);
		Type type = statementShow.e.getType();
		
		switch (type) {
			case INTEGER : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(I)V", false);
			}
				break;
			case BOOLEAN : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
						"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
						"println", "(Z)V", false);
				// TODO implement functionality
			//	throw new UnsupportedOperationException();
			}
			 break; 
			
			case FLOAT : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				// TODO implement functionality
				mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",	"Ljava/io/PrintStream;");
				mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream","println", "(F)V", false);
			//	throw new UnsupportedOperationException();
			}
			break;
			case IMAGE : {
				CodeGenUtils.genLogTOS(GRADE, mv, type);
				// TODO implement functionality
			//	mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
			//			"Ljava/io/PrintStream;");
				//mv.visitInsn(Opcodes.SWAP);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className, "makeFrame", RuntimeImageSupport.makeFrameSig, false);   
		 		mv.visitInsn(POP);
				
			}
				break;
		default:
			break;			
		}
		return null;
	}

	@Override
	public Object visitStatementSleep(StatementSleep statementSleep, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("StatementSleep visted");
		Expression exp = statementSleep.duration;
		exp.visit(this,arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
//		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatementWhile(StatementWhile statementWhile, Object arg)
			throws Exception {
		// TODO Auto-generated method stub

		Label l1 = new Label();
        Label l2 = new Label();
        
        mv.visitJumpInsn(GOTO, l2);
        
        mv.visitLabel(l1);
        statementWhile.b.visit(this, arg);
        
        mv.visitLabel(l2);
        statementWhile.guard.visit(this, arg);
        mv.visitJumpInsn(IFNE, l1);
 
        return null;
	}

	@Override
	public Object visitStatementWrite(StatementWrite statementWrite, Object arg)
			throws Exception {
		Declaration sdec = statementWrite.sourceDec;
		Declaration ddec = statementWrite.destDec;

		mv.visitVarInsn(ALOAD, sdec.current_slot);
		mv.visitVarInsn(ALOAD, ddec.current_slot);
		statementWrite.destDec.visit(this, arg);

		mv.visitMethodInsn(INVOKESTATIC, RuntimeImageSupport.className,"write", RuntimeImageSupport.writeSig,false);
		return null;
	}		
}