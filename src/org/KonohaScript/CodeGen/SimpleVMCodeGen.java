package org.KonohaScript.CodeGen;

import java.util.ArrayList;

import org.KonohaScript.*;
import org.KonohaScript.AST.AndNode;
import org.KonohaScript.AST.AssignNode;
import org.KonohaScript.AST.BlockNode;
import org.KonohaScript.AST.BoxNode;
import org.KonohaScript.AST.ConstNode;
import org.KonohaScript.AST.DoneNode;
import org.KonohaScript.AST.ErrorNode;
import org.KonohaScript.AST.FieldNode;
import org.KonohaScript.AST.FunctionNode;
import org.KonohaScript.AST.IfNode;
import org.KonohaScript.AST.JumpNode;
import org.KonohaScript.AST.LabelNode;
import org.KonohaScript.AST.LetNode;
import org.KonohaScript.AST.LocalNode;
import org.KonohaScript.AST.LoopNode;
import org.KonohaScript.AST.MethodCallNode;
import org.KonohaScript.AST.NewNode;
import org.KonohaScript.AST.NullNode;
import org.KonohaScript.AST.OrNode;
import org.KonohaScript.AST.ReturnNode;
import org.KonohaScript.AST.SwitchNode;
import org.KonohaScript.AST.ThrowNode;
import org.KonohaScript.AST.TryNode;
import org.KonohaScript.AST.TypedNode;


public class SimpleVMCodeGen extends CodeGenerator implements ASTVisitor {
	ArrayList<String> Program;

	public SimpleVMCodeGen() {
		super();
		this.Program = new ArrayList<String>();
	}

	private String pop() {
		return this.Program.remove(this.Program.size() - 1);
	}

	private void push(String Program) {
		this.Program.add(Program);
	}

	boolean IsUnboxedType(KClass Class) {
		return false; //Node.TypeInfo
	}
	
	@Override
	public CompiledMethod Compile(TypedNode Block) {
		Visit(Block);
		CompiledMethod Mtd = new CompiledMethod();
		assert (this.Program.size() == 1);
		Mtd.CompiledCode = this.Program.remove(0);
		return Mtd;
	}

	@Override
	public boolean Visit(TypedNode Node) {
		return Node.Evaluate(this);
	}

	@Override
	public void EnterDone(DoneNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitDone(DoneNode Node) {
		return true;
	}

	@Override
	public void EnterConst(ConstNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitConst(ConstNode Node) {
		if (IsUnboxedType(Node.TypeInfo)) {
			if (/* IsInt(Node) */true) {
				push(Integer.toString((int) Node.ConstValue));
			} else if (/* IsFloat(Node) */false) {
				push(Double.toString(Double.longBitsToDouble(Node.ConstValue)));
			} else if (/* IsBoolean(Node) */false) {
				push(Boolean.toString(Node.ConstValue == 0));
			}
		} else {
			push(Node.ConstObject.toString());
		}
		return true;
	}

	@Override
	public void EnterNew(NewNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitNew(NewNode Node) {
		push("new " + Node.TypeInfo.ShortClassName.toString() + "()");
		return true;

	}

	@Override
	public void EnterNull(NullNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitNull(NullNode Node) {
		push("null");
		return true;

	}

	@Override
	public void EnterLocal(LocalNode Node) {
		AddLocalVarIfNotDefined(Node.TermToken.ParsedText);
	}

	@Override
	public boolean ExitLocal(LocalNode Node) {
		push(Node.TermToken.ParsedText);
		return true;

	}

	@Override
	public void EnterField(FieldNode Node) {
		AddLocalVarIfNotDefined(Node.TermToken.ParsedText);
	}

	@Override
	public boolean ExitField(FieldNode Node) {
		String Expr = Node.TermToken.ParsedText;
		push(Expr + "." + Node.TypeInfo.FieldNames.get(Node.Xindex));
		return true;

	}

	@Override
	public void EnterBox(BoxNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitBox(BoxNode Node) {
		/* do nothing */return true;

	}

	@Override
	public void EnterMethodCall(MethodCallNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitMethodCall(MethodCallNode Node) {
		String thisNode = pop();
		String Params = "";
		String methodName = "mtd";
		int ParamSize = Node.Params.size();
		for (int i = 0; i < ParamSize; i = i + 1) {
			String Expr = pop();
			if (i != 0) {
				Params = "," + Params;
			}
			Params = Expr + Params;
		}
		push(thisNode + "." + methodName + "(" + Params + ")");
		return true;

	}

	@Override
	public void EnterAnd(AndNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitAnd(AndNode Node) {
		String Right = pop();
		String Left = pop();
		push(Left + " && " + Right);
		return true;

	}

	@Override
	public void EnterOr(OrNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitOr(OrNode Node) {
		String Right = pop();
		String Left = pop();
		push(Left + " || " + Right);
		return true;

	}

	@Override
	public void EnterAssign(AssignNode Node) {
		AddLocalVarIfNotDefined(Node.TermToken.ParsedText);
	}

	@Override
	public boolean ExitAssign(AssignNode Node) {
		String Right = pop();
		push(Node.TermToken.ParsedText + " = " + Right);
		return true;

	}

	@Override
	public void EnterLet(LetNode Node) {
		AddLocalVarIfNotDefined(Node.TermToken.ParsedText);
	}

	@Override
	public boolean ExitLet(LetNode Node) {
		String Right = pop();
		String Block = pop();
		push(Node.TermToken.ParsedText + " = " + Right + Block);
		return true;

	}

	@Override
	public void EnterBlock(BlockNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitBlock(BlockNode Node) {
		String Exprs = "";
		int Size = Node.ExprList.size();
		for (int i = 0; i < Size; i = i + 1) {
			String Expr = pop();
			if (i != 0) {
				Exprs = "," + Exprs;
			}
			Exprs = Expr + ";" + Exprs;
		}
		push("{" + Exprs + "}");
		return true;

	}

	@Override
	public void EnterIf(IfNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitIf(IfNode Node) {
		String ElseBlock = pop();
		String ThenBlock = pop();
		String CondExpr = pop();
		push("if (" + CondExpr + ") " + ThenBlock + " else " + ElseBlock);
		return true;

	}

	@Override
	public void EnterSwitch(SwitchNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitSwitch(SwitchNode Node) {
		int Size = Node.Labels.size();
		String Exprs = "";
		for (int i = 0; i < Size; i = i + 1) {
			String Label = Node.Labels.get(Size - i);
			String Block = pop();
			Exprs = "case " + Label + ":" + Block + Exprs;
		}
		String CondExpr = pop();
		push("switch (" + CondExpr + ") {" + Exprs + "}");
		return true;

	}

	@Override
	public void EnterLoop(LoopNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitLoop(LoopNode Node) {
		String LoopBody = pop();
		String IterExpr = pop();
		String CondExpr = pop();
		push("while (" + CondExpr + ") {" + LoopBody + IterExpr + "}");
		return true;

	}

	@Override
	public void EnterReturn(ReturnNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitReturn(ReturnNode Node) {
		String Expr = pop();
		push("return " + Expr);
		return false;

	}

	@Override
	public void EnterLabel(LabelNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitLabel(LabelNode Node) {
		String Label = Node.Label;
		if (Label.compareTo("continue") == 0) {
			push("");
		} else if (Label.compareTo("continue") == 0) {
			push("");
		} else {
			push(Label + ":");
		}
		return true;

	}

	@Override
	public void EnterJump(JumpNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitJump(JumpNode Node) {
		String Label = Node.Label;
		if (Label.compareTo("continue") == 0) {
			push("continue;");
		} else if (Label.compareTo("continue") == 0) {
			push("break;");
		} else {
			push("goto " + Label);
		}
		return false;

	}

	@Override
	public void EnterTry(TryNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitTry(TryNode Node) {
		String FinallyBlock = pop();
		String CatchBlocks = "";
		for (int i = 0; i < Node.CatchBlock.size(); i = i + 1) {
			String Block = pop();
			CatchBlocks = "catch() " + Block + CatchBlocks;
		}
		String TryBlock = pop();
		push("try " + TryBlock + "" + CatchBlocks + FinallyBlock);
		return true;

	}

	@Override
	public void EnterThrow(ThrowNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitThrow(ThrowNode Node) {
		String Expr = pop();
		push("throw " + Expr + ";");
		return false;

	}

	@Override
	public void EnterFunction(FunctionNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitFunction(FunctionNode Node) {
		// TODO Auto-generated method stub
		return true;

	}

	@Override
	public void EnterError(ErrorNode Node) {
		/* do nothing */
	}

	@Override
	public boolean ExitError(ErrorNode Node) {
		String Expr = pop();
		push("throw new Exception(" + Expr + ";");
		return false;

	}
}