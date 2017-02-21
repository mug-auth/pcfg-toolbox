package gr.auth.ee.mug.cfg.parsers.cnfparser;

import java.util.ArrayList;

import gr.auth.ee.mug.cfg.dottools.TreeNode;
import gr.auth.ee.mug.cfg.grammar.Alphabet;
import gr.auth.ee.mug.cfg.grammar.ContextFreeGrammar;
import gr.auth.ee.mug.cfg.grammar.Rule;
import gr.auth.ee.mug.cfg.parsers.ParserInterface;
import gr.auth.ee.mug.cfg.tools.Utilities;

/**
 * A parser for a given {@code ContextFreeGrammar} in Chomsky Normal form and a
 * string.
 * 
 * @author Vasileios Papapanagiotou
 */
public class CNFParser implements ParserInterface {

	/**
	 * The {@code ContextFreeGrammar} that was passed to the constructor.
	 */
	public final ContextFreeGrammar cfg;

	/**
	 * The string that was passed to the constructor.
	 */
	public final int[] stringIDs;

	/**
	 * A flag indicating if the grammar {@code cfg} can produce the string
	 * {@code stringIDs}.
	 */
	public final boolean canGenerate;

	/**
	 * Number of different parse trees that can produce the string
	 * {@code stringIDs}.
	 */
	public final int noofTrees;

	private final Alphabet A;
	private ArrayList<ArrayList<ArrayList<CNFItem>>> N;
	private int n;

	/**
	 * Initializes a parser for a grammar and a string, and performs the
	 * parsing.
	 * 
	 * @param cfg
	 *            A context-free grammar in Chomsky normal form
	 * @param stringIDs
	 *            An array of integers, where the {@code stringIDs[i]}
	 *            corresponds to the i-th symbols id. It is required that
	 *            {@code stringIDs[i] > 1} as only terminals excluding the empty
	 *            string symbols can be used, and also
	 *            {@code max stringIDs[i] <= A.getNoofTerminals()}
	 */
	public CNFParser(ContextFreeGrammar G, int[] stringIDs) {
		this.cfg = G;
		this.stringIDs = stringIDs;
		A = G.getAlphabet();
		parse();
		canGenerate = canGenerate();
		noofTrees = getNoofTrees();
	}

	/**
	 * @return If the string can be generated by the grammar
	 */
	@Override
	public boolean canGenerate() {
		CNFItem e = new CNFItem(Alphabet.idStart);
		return N.get(n - 1).get(0).contains(e);
	}

	/**
	 * @return The number of different parse trees
	 */
	@Override
	public int getNoofTrees() {
		int count = 0;
		CNFItem e = new CNFItem(Alphabet.idStart);
		for (int i = 0; i < N.get(n - 1).get(0).size(); i++) {
			if (N.get(n - 1).get(0).get(i).equals(e)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public TreeNode getTreeRoot(int treeID) {
		TreeNode root = new TreeNode();
		// Current state
		int i1 = N.size() - 1;
		int i2 = 0;
		int i3 = Utilities.indexOfAll(N.get(i1).get(i2), new CNFItem(Alphabet.idStart)).get(treeID);
		// Start recursive constructive
		root = updateTree(i1, i2, i3);
		// Return
		return root;
	}

	private void parse() {

		// Useful rename
		n = stringIDs.length;

		// Allocate memory
		N = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			N.add(new ArrayList<ArrayList<CNFItem>>());
			for (int j = 0; j <= i; j++) {
				N.get(i).add(new ArrayList<CNFItem>());
			}
		}

		// Initialize
		for (int i = 0; i < n; i++) {
			CNFItem e = new CNFItem(stringIDs[i], -1, -1, -1, -1);
			N.get(i).get(i).add(e);
		}

		for (int s = 1; s < n; s++) {
			for (int i = 0; i < n - s; i++) {
				for (int k = i; k <= i + s - 1; k++) {
					// For each rule
					for (int j = 0; j < cfg.getNoofRules(); j++) {
						// Get rule
						Rule r = cfg.getRule(j);

						// Create dummy entries to search with 'contains'
						CNFItem e0 = new CNFItem(r.getTo(0));
						CNFItem e1 = new CNFItem(r.getTo(1));

						// Create and handle new entry
						// Find indices
						ArrayList<Integer> idx0 = Utilities.indexOfAll(N.get(k).get(i), e0);
						ArrayList<Integer> idx1 = Utilities.indexOfAll(N.get(i + s).get(k + 1), e1);

						// Add a new entry for each index pair
						for (int idx0i = 0; idx0i < idx0.size(); idx0i++) {
							for (int idx1i = 0; idx1i < idx1.size(); idx1i++) {
								CNFItem e = new CNFItem(r.getFrom(), k, idx0.get(idx0i), idx1.get(idx1i), j);
								N.get(i + s).get(i).add(e);
							}
						}
					} // End for each rule
				} // end for k
			} // end for i
		} // end for s
	}

	private TreeNode updateTree(int i1, int i2, int i3) {
		// Get current entry
		TreeNode node = new TreeNode();
		node.o = N.get(i1).get(i2).get(i3);
		if (!A.isTerminal(((CNFItem) node.o).symbolID)) {
			node.children.add(updateTree(((CNFItem) node.o).k, i2, ((CNFItem) node.o).li3));
			node.children.add(updateTree(i1, ((CNFItem) node.o).k + 1, ((CNFItem) node.o).ri3));
		}
		return node;
	}

}
