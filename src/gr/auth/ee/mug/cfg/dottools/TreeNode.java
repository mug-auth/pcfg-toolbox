package gr.auth.ee.mug.cfg.dottools;

import java.util.ArrayList;

import gr.auth.ee.mug.cfg.grammar.Alphabet;
import gr.auth.ee.mug.cfg.grammar.ContextFreeGrammar;
import gr.auth.ee.mug.cfg.parsers.ParserInterface;
import gr.auth.ee.mug.cfg.parsers.ParserItemInterface;

/**
 * Class that represents a node in a tree.<br>
 * <br>
 * The binary tree can only be traversed top-down (no father pointers are
 * stored). It can be used to organize any class' objects that implements
 * ParserItemInterface.
 * 
 * @author Vasileios Papapanagiotou
 */
public class TreeNode {

	/**
	 * An array of pointers to the node's children
	 */
	public final ArrayList<TreeNode> children = new ArrayList<>();

	/**
	 * Pointer to the actual object of class {@code Type}
	 */
	public ParserItemInterface o = null;

	/**
	 * Compute the probability of this node by recursively summing all
	 * children's probabilities.
	 * 
	 * @param cfg
	 *            The context-free grammar that was used in the parsing
	 * @return
	 */
	public double reduceSum(ContextFreeGrammar cfg) {
		double x;
		if (o.getRuleId() == ParserInterface.noRuleId) {
			x = 0;
		} else {
			x = cfg.getRule(o.getRuleId()).getProbability();
		}
		for (int i = 0; i < children.size(); i++) {
			x += children.get(i).reduceSum(cfg);
		}
		return x;
	}

	public double reduceProduct(ContextFreeGrammar cfg) {
		double x;
		if (o.getRuleId() == ParserInterface.noRuleId) {
			x = 1;
		} else {
			x = cfg.getRule(o.getRuleId()).getProbability();
		}
		for (int i = 0; i < children.size(); i++) {
			x *= children.get(i).reduceProduct(cfg);
		}
		return x;
	}

	@Override
	public String toString() {

		String s = String.valueOf(o.getSymbolId());
		if (children.size() == 0) {
			return s;
		}
		s += " [";
		for (int i = 0; i < children.size(); i++) {
			s += children.get(i).toString() + ", ";
		}
		s = s.substring(0, s.length() - 2) + "]";
		return s;
	}

	public String toString(Alphabet al) {

		String s = al.getSymbol(o.getSymbolId());
		if (children.size() == 0) {
			return s;
		}
		s += " [";
		for (int i = 0; i < children.size(); i++) {
			s += children.get(i).toString(al) + ", ";
		}
		s = s.substring(0, s.length() - 2) + "]";
		return s;
	}

}