package cop5556sp18;

import cop5556sp18.Scanner.Kind;
//import cop5556sp18.Scanner.Token;

public class Types {

	public static enum Type {
		INTEGER, BOOLEAN, IMAGE, FLOAT, FILE, NONE;
	}

	public static Type getType(Kind kind)      // Changed the name here from getType (Token token) - mine
	{
		switch (kind) 					//Changed the name here from switch(token.kind)
		{
		case KW_int: {
			return Type.INTEGER;
		}
		case KW_boolean: {
			return Type.BOOLEAN;
		}
		case KW_image: {
			return Type.IMAGE;
		}
		case KW_filename: {
			return Type.FILE;
		}
		case KW_float: {
			return Type.FLOAT;
		}
		default:
			break;
		}
		// should not reach here
		assert false: "invoked getType with Kind that is not a type"; 
		return null;
	}
}
