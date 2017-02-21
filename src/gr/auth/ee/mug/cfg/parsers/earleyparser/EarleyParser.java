package gr.auth.ee.mug.cfg.parsers.earleyparser;

import java.util.ArrayList;

import gr.auth.ee.mug.cfg.dottools.TreeNode;
import gr.auth.ee.mug.cfg.grammar.Alphabet;
import gr.auth.ee.mug.cfg.grammar.ContextFreeGrammar;
import gr.auth.ee.mug.cfg.grammar.Rule;
import gr.auth.ee.mug.cfg.parsers.ParserInterface;
import gr.auth.ee.mug.cfg.tools.Utilities;

/**
 * An Earley recognizer for a given {@code ContextFreeGrammar} and a string.
 * 
 * @author Vasileios Papapanagiotou
 */
public class EarleyParser implements ParserInterface {

	private final ContextFreeGrammar cfg;
	private final Alphabet al;
	private final int[] stringIds;
	private final ArrayList<ArrayList<EarleyItem>> states;
	private final ArrayList<Integer> validParses;

	/**
	 * Initializes a recognizer for a grammar and a string, and performs the
	 * parsing.
	 * 
	 * @param cfg
	 *            A context-free grammar in Chomsky normal form. The cfg object
	 *            is cloned and the erasing rules are modified (symbol 'e' is
	 *            dropped).
	 * @param stringIDs
	 *            An array of integers, where the {@code stringIDs[i]}
	 *            corresponds to the i-th symbols id. It is required that
	 *            {@code stringIDs[i] > 1} as only terminals excluding the empty
	 *            string symbols can be used, and also
	 *            {@code max stringIDs[i] <= al.getNoofTerminals()}
	 */
	public EarleyParser(ContextFreeGrammar cfg, int[] stringIds) {

		// Set finals
		this.cfg = modifyErasingRules(cfg);
		this.al = cfg.getAlphabet();
		this.stringIds = stringIds;
		this.states = new ArrayList<>();
		this.validParses = new ArrayList<>();

		parse();
	}

	private ContextFreeGrammar modifyErasingRules(ContextFreeGrammar cfg) {
		ContextFreeGrammar mcfg = new ContextFreeGrammar(cfg.getAlphabet());
		for (int i = 0; i < cfg.getNoofRules(); i++) {
			Rule r = cfg.getRule(i);
			if (r.getToLength() == 1) {
				if (r.getTo(0) == Alphabet.idEmptyString) {
					r = new Rule(r.getFrom(), new int[] {}, r.getProbability());
				}
			}
			mcfg.addRule(r);
		}
		return mcfg;
	}

	@Override
	public boolean canGenerate() {
		return validParses.size() > 0;
	}

	@Override
	public int getNoofTrees() {
		return validParses.size();
	}

	@Override
	public TreeNode getTreeRoot(int i) {
		TreeNode root = new TreeNode();
		EarleyItem e = states.get(states.size() - 1).get(validParses.get(i));
		root.o = e;
		expandTreeNode(root, e);
		return root;
	}

	/**
	 * Create a human understandable view of an {@code EarleyItem}.<br>
	 * <br>
	 * 'id': 'dotted rule' ('startIdx', 'stateIdx') [ 'backId array' ]
	 * ('comment')
	 * 
	 * @param e
	 *            The {@code EarleyItem} to create the string from
	 * @return The string representation
	 */
	public String printEarleyItem(EarleyItem e) {
		Rule r = cfg.getRule(e.ruleId);
		String s = String.valueOf(e.id) + ": " + al.getSymbol(r.getFrom()) + " -> ";
		for (int i = 0; i < e.nextIdx; i++) {
			s += al.getSymbol(r.getTo(i)) + " ";
		}
		s += ". ";
		for (int i = e.nextIdx; i < r.getToLength(); i++) {
			s += al.getSymbol(r.getTo(i)) + " ";
		}
		s += "(" + String.valueOf(e.startIdx) + ", " + String.valueOf(e.stateIdx) + ")";
		s += " [ ";
		for (int i = 0; i < e.backId.size(); i++) {
			s += String.valueOf(e.backId.get(i)) + " ";
		}
		s += "]";
		s += " [" + e.comment + "]";
		return s;
	}

	/**
	 * Create a human understandable view of a state.
	 * 
	 * @param state
	 *            A list of {@code EarleyItem} to create the string from
	 * @param onlyCompleted
	 *            If {@code true}, only show completed states
	 * @return The string representation
	 */
	public String printEarleyState(ArrayList<EarleyItem> state, boolean onlyCompleted) {
		String s = "";
		for (int i = 0; i < state.size(); i++) {
			EarleyItem e = state.get(i);
			Rule r = cfg.getRule(e.ruleId);
			boolean b = r.getToLength() == e.nextIdx;
			if (onlyCompleted & b | !onlyCompleted) {
				s += printEarleyItem(state.get(i)) + "\n";
			}
		}
		return s;
	}

	/**
	 * Create a human understandable view of an array of states.
	 * 
	 * @param states
	 *            The array of states
	 * @param onlyCompleted
	 *            If {@code true}, only show completed states
	 * @return The string representation
	 */
	public String printEarleyStates(ArrayList<ArrayList<EarleyItem>> states, boolean onlyCompleted) {
		String s = "";
		for (int i = 0; i < states.size(); i++) {
			s += "Group " + String.valueOf(i) + "\n";
			s += "================\n";
			s += printEarleyState(states.get(i), onlyCompleted) + "\n";
		}
		return s;
	}

	/**
	 * Create a human understandable view of the recognizer's states.
	 * 
	 * @param If
	 *            {@code true}, only show completed states
	 * @return The string representation
	 */
	public String printEarleyStates(boolean onlyCompleted) {
		return printEarleyStates(states, onlyCompleted);
	}

	private void expandTreeNode(TreeNode node, EarleyItem e) {

		int iBackId = 0;

		Rule r = cfg.getRule(e.ruleId);
		for (int i = 0; i < r.getToLength(); i++) {
			if (al.isTerminal(r.getTo(i))) {
				// The i-th symbol substituted by the rule is a terminal; it has
				// no back pointer, and we need to manually create a new leaf in
				// the tree
				TreeNode leaf = new TreeNode();
				leaf.o = new EarleyItem(noRuleId, r.getTo(i), e.startIdx + i, 0, e.stateIdx, "leaf");
				node.children.add(leaf);

			} else {
				// The i-th symbol substituted by the rule is a non-terminal; we
				// need to expand it
				TreeNode child = new TreeNode();
				EarleyItem o = getEarleyItemById(e.backId.get(iBackId));
				child.o = o;
				iBackId++;
				expandTreeNode(child, o);
				node.children.add(child);

			}
		}
	}

	private EarleyItem getEarleyItemById(long id) {
		for (int i = 0; i < states.size(); i++) {
			ArrayList<EarleyItem> state = states.get(i);
			for (int j = 0; j < state.size(); j++) {
				EarleyItem e = state.get(j);
				if (e.id == id) {
					return e;
				}
			}
		}
		return null;
	}

	private void parse() {

		// Initialize states array (memory allocation)
		for (int i = 0; i < stringIds.length + 1; i++) {
			states.add(new ArrayList<EarleyItem>());
		}

		// Initialize state-0 (seeding [ -> S])
		for (int i = 0; i < cfg.getNoofRules(); i++) {
			Rule r = cfg.getRule(i);
			if (r.getFrom() == Alphabet.idStart) {
				EarleyItem e = new EarleyItem(i, r.getFrom(), 0, 0, 0, "init");
				states.get(0).add(e);
			}
		}

		// Main loop
		for (int i = 0; i < states.size(); i++) {
			ArrayList<EarleyItem> state = states.get(i);

			int j = 0; // Current item in state-i
			while (j < state.size()) {

				EarleyItem e = state.get(j);
				Rule r = cfg.getRule(e.ruleId);

				if (e.nextIdx == r.getToLength()) {
					parseComplete(e);

				} else {
					if (al.isTerminal(r.getTo(e.nextIdx))) {
						if (i < states.size() - 1) {
							parseScan(e);
						}

					} else {
						parsePredict(e);
					}

				}
				j++;
			} // j
		} // i

		// Post processing
		ArrayList<EarleyItem> lastState = states.get(states.size() - 1);
		for (int i = 0; i < lastState.size(); i++) {
			EarleyItem e = lastState.get(i);
			Rule r = cfg.getRule(e.ruleId);
			boolean b1 = r.getFrom() == Alphabet.idStart;
			boolean b2 = r.getToLength() == e.nextIdx;
			boolean b3 = e.startIdx == 0;
			if (b1 & b2 & b3) {
				validParses.add(i);
			}
		}
	}

	/**
	 * Perform the complete step for a single {@code EarleyItem}.
	 * 
	 * @param e
	 *            The {@code EarleyItem} to process
	 */
	private void parseComplete(EarleyItem e) {

		// The state to look for (past state); it is the state that corresponds
		// to where item e starts
		ArrayList<EarleyItem> pstate = states.get(e.startIdx);

		// The current state
		ArrayList<EarleyItem> cstate = states.get(e.stateIdx);

		// The id of the non-terminal symbol that is up for completion
		int id = cfg.getRule(e.ruleId).getFrom();

		// Loop all items of past state
		for (int j = 0; j < pstate.size(); j++) {
			// Past EarleyItem of this iteration
			EarleyItem pe = pstate.get(j);
			// and its rule
			Rule r = cfg.getRule(pe.ruleId);

			// Check if pe is not completed (if it is we can't complete it)
			if (pe.nextIdx < r.getToLength()) {

				// Check if the next symbol is the one we are completing
				if (id == r.getTo(pe.nextIdx)) {

					// Create the new item to add
					int symbolId = cfg.getRule(pe.ruleId).getFrom();
					EarleyItem ce = new EarleyItem(pe.ruleId, symbolId, pe.startIdx, pe.nextIdx + 1, e.stateIdx,
							"complete");
					// Copy from pe to ce all of the back pointers
					for (int k = 0; k < pe.backId.size(); k++) {
						ce.backId.add(pe.backId.get(k));
					}
					// and append the new back pointer to e
					ce.backId.add(e.id);

					// Ready to add ce to cstate
					safeAdd(cstate, ce);
				}
			}
		}
	}

	/**
	 * Perform the prediction step for a single {@code EarleyItem}.
	 * 
	 * @param e
	 *            The {@code EarleyItem} to process
	 */
	private void parsePredict(EarleyItem e) {

		// Id of symbol that is expanded in the prediction step
		int id = cfg.getRule(e.ruleId).getTo(e.nextIdx);

		// Loop all rules
		for (int i = 0; i < cfg.getNoofRules(); i++) {
			Rule r = cfg.getRule(i);

			// Check if the rule replaces the symbol with this id
			if (id == r.getFrom()) {
				EarleyItem toAdd = new EarleyItem(i, id, e.stateIdx, 0, e.stateIdx, "predict");
				safeAdd(states.get(e.stateIdx), toAdd);
			}
		}
	}

	/**
	 * Perform the scan step for a single {@code EarleyItem}.
	 * 
	 * @param e
	 *            The {@code EarleyItem} to process
	 */
	private void parseScan(EarleyItem e) {

		// Id of symbol to scan for
		int id = cfg.getRule(e.ruleId).getTo(e.nextIdx);

		// Check if this is indeed the symbol of the string
		if (id == stringIds[e.stateIdx]) {

			// This item will be added to the next state
			int stateIdx = e.stateIdx + 1;
			int symbolId = cfg.getRule(e.ruleId).getFrom();
			EarleyItem toAdd = new EarleyItem(e.ruleId, symbolId, e.startIdx, e.nextIdx + 1, stateIdx, "scan");
			for (int i = 0; i < e.backId.size(); i++) {
				toAdd.backId.add(e.backId.get(i));
			}
			safeAdd(states.get(stateIdx), toAdd);
		}
	}

	/**
	 * Add an {@code EarleyItem} in a state. If the state already contains the
	 * item, don't add it again, however append its back pointers (no
	 * duplicates).
	 * 
	 * @param state
	 *            A list of {@code EarleyItem}s
	 * @param e
	 *            An {@code EarleyItem}
	 */
	private static void safeAdd(ArrayList<EarleyItem> state, EarleyItem e) {

		if (!state.contains(e)) {
			// If the state does not contain e, simply add it
			state.add(e);

		} else {
			// Find the index of the state that e is equal to
			ArrayList<Integer> idx = Utilities.indexOfAll(state, e);

			if (idx.size() == 1) {
				// No need to add e, just append back pointers of e
				for (int i = 0; i < e.backId.size(); i++) {
					if (!state.get(idx.get(0)).backId.contains(e.backId.get(i))) {
						state.get(idx.get(0)).backId.add(e.backId.get(i));
					}
				}

			} else {
				// This is a problematic situation that should never be reached
				System.out.println("There is more than one item in state equal to e; this is bad");
			}
		}
	}

}
