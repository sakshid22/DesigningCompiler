package cop5556sp18;

import java.util.*;

import java.util.HashMap;
import java.util.Stack;



import cop5556sp18.AST.Declaration;



public class SymbolTable {
	
	HashMap<String,List<ScopeNode>> symboltab= new HashMap<>();
	int curscope, nextscope;
	Stack<Integer> scopestack = new Stack<Integer>();

	void enterScope()
	{
	curscope = nextscope++;
	scopestack.push(curscope);
	}

	void leaveScope()
	{
	curscope=scopestack.pop();
	}

	
	public void addition(String s, Declaration dec)
	{
		System.out.println("entering value: " + s);
		ScopeNode scopenode = new ScopeNode(dec);
		scopenode.setindex(curscope);
			if(!symboltab.containsKey(s))
			{
			symboltab.put(s, new Stack<ScopeNode>());
			}
			symboltab.get(s).add(scopenode);
	}

	public boolean ifredeclared(Declaration dec)
	{
	String name = dec.name;
		if(symboltab.containsKey(name)==false)
		return false;
		else
		{
			List<ScopeNode> l=symboltab.get(name);
			for(ScopeNode length:l)
			{
				if(length.index==curscope)
					return true;
			}
		}
		return false;
	}

	public class ScopeNode extends Stack<ScopeNode>
	{
		public int index;
		private Declaration dec;
		public ScopeNode( Declaration dec)
		{ this.dec = dec;}
		public int getindex() 
		{ return index;}
		public void setindex( int index)
		{ this.index = index; }
		public Declaration getdec()
		{ return dec;}
		public void setdec(Declaration dec)
		{ this.dec = dec ; }
	}
	
	public Declaration lookup(String s)
    {        
        if(symboltab.containsKey(s)) {
            List<ScopeNode> range = symboltab.get(s);
            Declaration dec = null;
       //     System.out.println("symbol aru true"+range);
            for(int i=range.size()-1;i>=0;i--)
            {
                int temp_scope = range.get(i).getindex();
                if(scopestack.contains(temp_scope))
                {
                    dec = range.get(i).getdec();
                    break;
                }
            }
            return dec;
        }
        return null;
    }
}