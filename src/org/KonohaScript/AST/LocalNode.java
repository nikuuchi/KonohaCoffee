package org.KonohaScript.AST;

import org.KonohaScript.KClass;
import org.KonohaScript.KToken;
import org.KonohaScript.CodeGen.ASTVisitor;

public class LocalNode extends TypedNode {
	/* frame[$Index] (or TermToken->text) */
	public KToken TermToken;
	@Deprecated
	int ClassicKonohaIndex;

	public LocalNode(KClass TypeInfo, KToken TermToken, int Index) {
		super(TypeInfo);
		this.TermToken = TermToken;
		this.ClassicKonohaIndex = Index;
	}

	@Override
	public boolean Evaluate(ASTVisitor Visitor) {
		Visitor.EnterLocal(this);
		return Visitor.ExitLocal(this);
	}
}