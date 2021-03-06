package jadx.dex.instructions.args;

import jadx.codegen.TypeGen;
import jadx.utils.exceptions.JadxRuntimeException;

public class LiteralArg extends InsnArg {

	private final long literal;

	public LiteralArg(long value, ArgType type) {
		this.literal = value;
		this.typedVar = new TypedVar(type);
		if (literal != 0 && type.isObject())
			throw new RuntimeException("wrong literal type");
	}

	public long getLiteral() {
		return literal;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public String toString() {
		try {
			return "(" + TypeGen.literalToString(literal, getType()) + " " + typedVar + ")";
		} catch (JadxRuntimeException ex) {
			// can't convert literal to string
			return "(" + literal + " " + typedVar + ")";
		}
	}
}
