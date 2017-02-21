package gr.auth.ee.mug.cfg.dottools;

import gr.auth.ee.mug.cfg.grammar.Alphabet;
import gr.auth.ee.mug.cfg.grammar.ContextFreeGrammar;

/**
 * A class that aids the creation of DOT code that describes a parse tree.
 * 
 * @author Vasileios Papapanagiotou
 */
public class Tree2DOT {

	public final String dotCode;

	private final ContextFreeGrammar cfg;
	private final Alphabet al;

	private int noofTerminals = 0;

	/**
	 * Creates a parse tree dot code.
	 * 
	 * @param root
	 *            The root of the parse
	 * @param cfg
	 *            The context-free grammar that was used to parse the string
	 * @param graphName
	 *            A name for the dot graph code
	 */
	public Tree2DOT(TreeNode root, ContextFreeGrammar cfg, String graphName) {
		this.cfg = cfg;
		al = cfg.getAlphabet();

		// Start building the DOT code
		String dot = "digraph " + graphName + " {\n";

		// Append terminal symbols
		String s1 = parseTerminalSymbols(root, "");
		s1 = s1.substring(0, s1.length() - 3);
		dot += "  str [shape=record width=" + String.valueOf(noofTerminals) + ", label=" + q(s1) + "];\n";

		// Append non-terminal symbols
		String s2 = parseNonTerminalSymbols(root, "");
		dot += s2;

		// Append productions
		String s3 = "";
		s3 = parseProductions(root, s3);
		dot += s3;

		// Finish building
		dot += "}\n";

		// Finalize dotCode
		dotCode = dot;
	}

	private String parseNonTerminalSymbols(TreeNode node, String s) {
		if (!al.isTerminal(node.o.getSymbolId())) {
			String s0 = " [" + String.valueOf(cfg.getRule(node.o.getRuleId()).getProbability()) + "]";
			String s1 = "  " + q(node.o.getId()) + "[label=" + q(al.getSymbol(node.o.getSymbolId()) + s0) + "];\n";
			String[] si = new String[node.children.size()];
			for (int i = 0; i < si.length; i++) {
				si[i] = parseNonTerminalSymbols(node.children.get(i), s);
			}
			s += s1;
			for (int i = 0; i < si.length; i++) {
				s += si[i];
			}
		}
		return s;
	}

	private String parseProductions(TreeNode node, String s) {
		if (!al.isTerminal(node.o.getSymbolId())) {
			String s1[] = new String[node.children.size()];
			for (int i = 0; i < s1.length; i++) {
				s1[i] = parseProductions1(node, node.children.get(i));
			}
			String s2[] = new String[node.children.size()];
			for (int i = 0; i < s2.length; i++) {
				s2[i] = parseProductions(node.children.get(i), s);
			}
			for (int i = 0; i < s1.length; i++) {
				s += s1[i];
			}
			for (int i = 0; i < s2.length; i++) {
				s += s2[i];
			}
		}
		return s;
	}

	private String parseProductions1(TreeNode parent, TreeNode child) {
		String s = "  " + q(parent.o.getId()) + " -> ";
		if (al.isTerminal(child.o.getSymbolId())) {
			s += "str:";
		}
		s += q(child.o.getId()) + ";\n";
		return s;
	}

	private String parseTerminalSymbols(TreeNode node, String s) {
		if (al.isTerminal(node.o.getSymbolId())) {
			noofTerminals++;
			s += "<" + String.valueOf(node.o.getId()) + "> " + al.getSymbol(node.o.getSymbolId());
			String description = al.getDescription(node.o.getSymbolId());
			if (description != null) {
				s += "\\n" + description;
			}
			s += " | ";
		} else {
			String[] si = new String[node.children.size()];
			for (int i = 0; i < si.length; i++) {
				si[i] = parseTerminalSymbols(node.children.get(i), s);
			}
			for (int i = 0; i < si.length; i++) {
				s += si[i];
			}
		}
		return s;
	}

	private static final String q(double x) {
		return q(String.valueOf(x));
	}

	private static final String q(long x) {
		return q(String.valueOf(x));
	}

	private static final String q(String s) {
		return "\"" + s + "\"";
	}

}
