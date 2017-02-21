package gr.auth.ee.mug.cfg.grammar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import gr.auth.ee.mug.cfg.grammar.solvers.EmptyRulesCost;
import gr.auth.ee.mug.cfg.grammar.solvers.EmptyRulesJacobian;

/**
 * Class that represents a context-free grammar.<br>
 * <br>
 * The class consists mainly of an al
 * ({@code gr.auth.ee.mug.cfg.grammar.Alphabet}) and an array of rules
 * ({@code gr.auth.ee.mug.cfg.grammar.Rule}).
 * 
 * @author Vasileios Papapanagiotou
 */
public class ContextFreeGrammar {

	private final Alphabet al;
	private final ArrayList<Rule> rules = new ArrayList<>();

	/**
	 * Creates a new context-free grammar based on alphabet {@code al}.<br>
	 * Initially, no rules are added to the grammar.
	 * 
	 * @param al
	 *            The alphabet upon which the grammar is based ({@code al} is
	 *            cloned and the clone is stored internally).
	 */
	public ContextFreeGrammar(Alphabet al) {
		this.al = al.clone();
	}

	/**
	 * Adds a new rule to the grammar.
	 * 
	 * @param rule
	 *            The rule to be added
	 */
	public void addRule(Rule rule) {
		rules.add(rule);
	}

	@Override
	public ContextFreeGrammar clone() {
		ContextFreeGrammar cfg = new ContextFreeGrammar(al);
		for (int i = 0; i < rules.size(); i++) {
			cfg.addRule(rules.get(i).clone());
		}
		return cfg;
	}

	/**
	 * Provides access to the alphabet used by the grammar.
	 * 
	 * @return A clone of the alphabet that was pass to the constructor
	 */
	public Alphabet getAlphabet() {
		return al.clone();
	}

	/**
	 * @return The number of rules currently in the grammar
	 */
	public int getNoofRules() {
		return rules.size();
	}

	/**
	 * @param id
	 *            A rule's id (it is in fact the rule's index)
	 * @return A {@code Rule} containing the corresponding rule
	 */
	public Rule getRule(int id) {
		return rules.get(id);
	}

	/**
	 * Exposes the {@code printAlphabet} method.
	 * 
	 * @return A {@code String} containing a textual summary of the alphabet,
	 *         usually to be printed to {@code System.out}
	 */
	public String printAlphabet() {
		return al.printSymbols();
	}

	/**
	 * @return A {@code String} containing a textual representation of the
	 *         rules, usually to be printed to {@code System.out}
	 */
	public String printRules() {
		String[] s = new String[rules.size()];
		for (int i = 0; i < rules.size(); i++) {
			s[i] = al.getSymbol(rules.get(i).getFrom()) + " -> ";
			for (int j = 0; j < rules.get(i).getToLength(); j++) {
				s[i] += al.getSymbol(rules.get(i).getTo(j)) + " ";
			}
			s[i] += "(pr = " + String.valueOf(rules.get(i).getProbability()) + ")\n";
		}

		Arrays.sort(s);

		String ss = "";
		for (int i = 0; i < s.length; i++) {
			ss += s[i];
		}
		return ss;
	}

	/**
	 * Save grammar (alphabet and rules) in a text file. The text file can be
	 * used to restore the grammar at a different execution of the program.
	 * 
	 * @param filename
	 *            The name of the text file to write into
	 * @throws IOException
	 */
	public void saveToFile(String filename) throws IOException {

		final String nl = "\r\n"; // new line

		File file = new File(filename);
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		String s;

		// Number of non-terminals
		s = String.valueOf(al.getNoofNonTerminals());
		bufferedWriter.write(s + nl);

		// Non-terminals
		for (int i = 0; i < al.getNoofNonTerminals(); i++) {
			int id = al.getIdNonTerminal(i);
			s = "";
			s += String.valueOf(id) + " "; // id
			s += al.getSymbol(id) + " "; // symbol
			s += al.getDescription(id); // description
			bufferedWriter.write(s + nl);
		}

		// Number of terminals
		s = String.valueOf(al.getNoofTerminals());
		bufferedWriter.write(s + nl);

		// Terminals
		for (int i = 0; i < al.getNoofTerminals(); i++) {
			int id = al.getIdTerminal(i);
			s = "";
			s += String.valueOf(id) + " "; // id
			s += al.getSymbol(id) + " "; // symbol
			s += al.getDescription(id) + " "; // description
			bufferedWriter.write(s + nl);
		}

		// Number of rules
		s = String.valueOf(rules.size());
		bufferedWriter.write(s + nl);

		// Rules
		for (int i = 0; i < rules.size(); i++) {
			Rule r = rules.get(i);
			s = "";
			s += String.valueOf(r.getProbability()) + " ";
			s += String.valueOf(r.getFrom()) + " ";
			for (int j = 0; j < r.getToLength(); j++) {
				s += String.valueOf(r.getTo(j)) + " ";
			}
			bufferedWriter.write(s + nl);
		}

		bufferedWriter.close();
		fileWriter.close();
	}

	/**
	 * Creates a new {@code ContextFreeGrammar} in Chomsky normal form.
	 * 
	 * @param cfg
	 *            A context-free grammar
	 * @return The context-free grammar in Chomsky normal-form.
	 */
	public static ContextFreeGrammar chomskyNormal(ContextFreeGrammar cfg) {
		ContextFreeGrammar h = cfg.clone();

		// Step 1: handle long rules
		for (int i = h.rules.size() - 1; i >= 0; i--) {

			// Escape if rule is not long
			if (h.rules.get(i).getToLength() <= 2) {
				continue;
			}

			// Useful renames
			Rule r = h.rules.get(i);
			int len = r.getToLength();
			String sFrom = h.al.getSymbol(r.getFrom());

			// Remove the long rule
			h.rules.remove(i);

			// Create and add new non-terminal symbols
			int[] ids = new int[len - 2];
			for (int j = 0; j < ids.length; j++) {
				ids[j] = h.al.addSymbol(sFrom + "_" + String.valueOf(j + 1), false, null);
			}

			// Add first rule
			h.addRule(new Rule(r.getFrom(), new int[] { r.getTo(0), ids[0] }, r.getProbability()));

			// Add next rules
			for (int j = 0; j < len - 3; j++) {
				h.addRule(new Rule(ids[j], new int[] { r.getTo(j + 1), ids[j + 1] }, 1));
			}

			// Add final rule
			h.addRule(new Rule(ids[len - 3], new int[] { r.getTo(len - 2), r.getTo(len - 1) }, 1));
		}

		// Step 2: handle empty string rules

		// Set E of erasable non-terminals
		ArrayList<Integer> E = getErasables(h.rules);

		// Probabilities of erasing
		double[] prE = getErasablesProbabilities(h.rules, E);
		// new double[E.size()];

		// Remove all empty string rules
		for (int i = h.rules.size() - 1; i >= 0; i--) {
			if (h.al.isEmptyStringSymbol(h.rules.get(i).getTo(0))) {
				h.rules.remove(i);
			}
		}

		// Add new short rules
		int curHRulesSize = h.rules.size();
		for (int i = 0; i < curHRulesSize; i++) {
			Rule r = h.rules.get(i);

			// Escape if not a rule of length 2
			if (r.getToLength() != 2) {
				continue;
			}

			if (E.contains(r.getTo(0))) {
				// TODO if exists "r.getFrom() -> r.getTo(1)" just update prob,
				// WARNING the TODO above is probably wrong, as this probability
				// (obtained directly by the rule) is already added to the total
				// else:
				double pr = r.getProbability() * prE[E.indexOf(r.getTo(0))];
				h.rules.add(new Rule(r.getFrom(), new int[] { r.getTo(1) }, pr));
			}
			if (E.contains(r.getTo(1))) {
				// TODO if exists "r.getFrom() -> r.getTo(0)" just update prob,
				// WARNING the TODO above is probably wrong, as this probability
				// (obtained directly by the rule) is already added to the total
				// else:
				double pr = r.getProbability() * prE[E.indexOf(r.getTo(1))];
				h.rules.add(new Rule(r.getFrom(), new int[] { r.getTo(0) }, pr));
			}
		}

		// Step 3: handle short rules

		// Create sets D(A) and Di(A) for non-terminals A
		ArrayList<ArrayList<Integer>> nonTerminalsD = new ArrayList<>();
		ArrayList<ArrayList<Integer>> nonTerminalsDi = new ArrayList<>();
		for (int i = 0; i < h.al.getNoofNonTerminals(); i++) {
			nonTerminalsD.add(getD(h.al.getIdNonTerminal(i), h.rules));
			// nonTerminalsDi.add(getDi(i, h.rules));
		}

		// Create sets D(a) and Di(A) for terminals
		ArrayList<ArrayList<Integer>> terminalsD = new ArrayList<>();
		ArrayList<ArrayList<Integer>> terminalsDi = new ArrayList<>();
		for (int i = 0; i < h.al.getNoofTerminals(); i++) {
			terminalsD.add(getD(h.al.getIdTerminal(i), h.rules));
			// terminalsDi.add(getDi(i, h.rules));
		}

		DecompositionSolver decompSolver = getDerivationsProbabilitiesMatA(h.al, h.rules);

		// Matrix of replacement probabilities
		double[][] prDerivNonTerm = new double[h.al.getNoofNonTerminals()][];
		double[][] prDerivTerm = new double[h.al.getNoofTerminals()][];

		for (int i = 0; i < h.al.getNoofNonTerminals(); i++) {
			int id = h.al.getIdNonTerminal(i);
			double[] vecB = getDerivationsProbabilitiesVecB(h, id);
			prDerivNonTerm[i] = decompSolver.solve(new ArrayRealVector(vecB)).toArray();
		}
		for (int i = 0; i < h.al.getNoofTerminals(); i++) {
			int id = h.al.getIdTerminal(i);
			double[] vecB = getDerivationsProbabilitiesVecB(h, id);
			prDerivTerm[i] = decompSolver.solve(new ArrayRealVector(vecB)).toArray();
		}

		// Generate rules
		curHRulesSize = h.rules.size();
		for (int i = 0; i < curHRulesSize; i++) {
			Rule r = h.rules.get(i);

			// Escape if length is not 2
			if (r.getToLength() != 2) {
				continue;
			}

			ArrayList<Integer> al0;
			ArrayList<Integer> al1;
			int id0;
			int id1;
			int idx0;
			int idx1;

			id0 = r.getTo(0);
			idx0 = h.al.getIdx(id0);
			if (h.al.isTerminal(id0)) {
				al0 = terminalsD.get(idx0);
			} else {
				al0 = nonTerminalsD.get(idx0);
			}

			id1 = r.getTo(1);
			idx1 = h.al.getIdx(id1);
			if (h.al.isTerminal(id1)) {
				al1 = terminalsD.get(idx1);
			} else {
				al1 = nonTerminalsD.get(idx1);
			}

			for (int j0 = 0; j0 < al0.size(); j0++) {
				for (int j1 = 0; j1 < al1.size(); j1++) {
					// TODO if exists "r.getFrom() -> al0.get(j0) al1.get(j1)"
					// just update prob, else

					// skip if no actual replacement
					if (id0 == al0.get(j0) & id1 == al1.get(j1)) {
						continue;
					}

					// idx0 is replaced by idx0r
					// idx1 is replaced by idx1r
					int idx0r = h.al.getIdx(al0.get(j0));
					int idx1r = h.al.getIdx(al1.get(j1));

					double p0;
					double p1;
					if (h.al.isTerminal(al0.get(j0))) {
						p0 = prDerivTerm[idx0r][idx0];
					} else {
						p0 = prDerivNonTerm[idx0r][idx0];
					}
					if (h.al.isTerminal(al1.get(j1))) {
						p1 = prDerivTerm[idx1r][idx1];
					} else {
						p1 = prDerivNonTerm[idx1r][idx1];
					}
					double p = r.getProbability() * p0 * p1;

					h.rules.add(new Rule(r.getFrom(), new int[] { al0.get(j0), al1.get(j1) }, p));
				}
			}
		}

		// Remove short rules
		for (int i = h.rules.size() - 1; i >= 0; i--) {
			if (h.rules.get(i).getToLength() == 1) {
				h.rules.remove(i);
			}
		}

		// Extra: remove duplicates
		for (int i = h.rules.size() - 1; i >= 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				if (h.rules.get(i).equals(h.rules.get(j)) & i != j) {
					h.rules.remove(i);
					break;
				}
			}
		}

		// Add few last rules (from step 3)
		int idxStart = h.al.getIdx(Alphabet.idStart);
		for (int i = 0; i < h.rules.size(); i++) {
			Rule r = h.rules.get(i);

			if (nonTerminalsD.get(idxStart).contains(r.getFrom())) {

				int[] rTo = new int[r.getToLength()];
				for (int j = 0; j < rTo.length; j++) {
					rTo[j] = r.getTo(j);
				}

				double p = r.getProbability();
				if (h.al.isTerminal(r.getFrom())) {
					p *= prDerivTerm[h.al.getIdx(r.getFrom())][h.al.getIdx(Alphabet.idStart)];
				} else {
					p *= prDerivNonTerm[h.al.getIdx(r.getFrom())][h.al.getIdx(Alphabet.idStart)];
				}

				Rule r1 = new Rule(Alphabet.idStart, rTo, p);

				if (!h.rules.contains(r1)) {
					h.rules.add(r1);
				}
			}
		}

		// Clean up
		boolean flag = true;
		while (flag) {
			flag = false;

			ArrayList<Integer> uselessIds = new ArrayList<>();

			for (int i = 0; i < h.al.getNoofNonTerminals(); i++) {

				int id = h.al.getIdNonTerminal(i);
				if (id == Alphabet.idStart) {
					continue;
				}

				boolean flagFound = false;

				for (int j = 0; j < h.rules.size(); j++) {
					Rule r = h.rules.get(j);

					for (int k = 0; k < r.getToLength(); k++) {
						if (r.getTo(k) == id) {
							flagFound = true;
							break;
						}
					}
				}

				if (!flagFound) {
					uselessIds.add(id);
				}
			}

			for (int j = h.rules.size() - 1; j >= 0; j--) {
				if (uselessIds.contains(h.rules.get(j).getFrom())) {
					h.rules.remove(j);
					flag = true;
				}
			}

		}

		return h;
	}

	/**
	 * Create a new ContextFreeGrammar (including its alphabet) from a text
	 * file.
	 * 
	 * @param filename
	 *            The name of the file that contains the description of the
	 *            grammar
	 * @return A new {@code ContextFreeGrammar}
	 * @throws IOException
	 */
	public static ContextFreeGrammar newFromFile(String filename) throws IOException {

		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String s;

		String[] input1; // non-terminals;
		String[] input2; // terminals;
		String[] input3; // rules

		s = bufferedReader.readLine();
		int len1 = Integer.parseInt(s);
		input1 = new String[len1];

		for (int i = 0; i < len1; i++) {
			input1[i] = bufferedReader.readLine();
		}

		s = bufferedReader.readLine();
		int len2 = Integer.parseInt(s);
		input2 = new String[len2];

		for (int i = 0; i < len2; i++) {
			input2[i] = bufferedReader.readLine();
		}

		s = bufferedReader.readLine();
		int len3 = Integer.parseInt(s);
		input3 = new String[len3];

		for (int i = 0; i < len3; i++) {
			input3[i] = bufferedReader.readLine();
		}

		bufferedReader.close();
		fileReader.close();

		Alphabet A = Alphabet.newFromStrings(input1, input2);
		ContextFreeGrammar cfg = new ContextFreeGrammar(A);

		for (int i = 0; i < len3; i++) {
			String[] ss = input3[i].split(" ");
			double pr = Double.parseDouble(ss[0]);
			int from = Integer.parseInt(ss[1]);
			int[] to = new int[ss.length - 2];
			for (int j = 0; j < to.length; j++) {
				to[j] = Integer.parseInt(ss[2 + j]);
			}
			cfg.addRule(new Rule(from, to, pr));
		}

		return cfg;
	}

	/**
	 * Create set D for a symbol A; this set contains all symbols (including A)
	 * that can be produced from A using only short rules.
	 * 
	 * @param n
	 *            The id of symbol A
	 * @param rules
	 *            The rules of the grammar
	 * @return D(A)
	 */
	private static ArrayList<Integer> getD(int n, ArrayList<Rule> rules) {
		ArrayList<Integer> D = new ArrayList<>();
		D.add(n);

		boolean flag = true;
		while (flag) {
			flag = false;

			for (int i = 0; i < rules.size(); i++) {
				Rule r = rules.get(i);

				if (r.getToLength() != 1) {
					continue;
				}

				boolean b1 = D.contains(r.getFrom());
				boolean b2 = !D.contains(r.getTo(0));
				if (b1 & b2) {
					D.add(r.getTo(0));
					flag = true;
				}
			}
		}

		return D;
	}

	/**
	 * Create set Di for a symbol A; this set contains all symbols (including A)
	 * that can produce A using only short rules.
	 * 
	 * @param n
	 *            The id of symbol A
	 * @param rules
	 *            The rules of the grammar
	 * @return Di(A)
	 */
	private static ArrayList<Integer> getDi(int n, ArrayList<Rule> rules) {
		ArrayList<Integer> Di = new ArrayList<>();
		Di.add(n);

		boolean flag = true;
		while (flag) {
			flag = false;

			for (int i = 0; i < rules.size(); i++) {
				Rule r = rules.get(i);

				if (r.getToLength() != 1) {
					continue;
				}

				boolean b1 = !Di.contains(r.getFrom());
				boolean b2 = Di.contains(r.getTo(0));
				if (b1 & b2) {
					Di.add(r.getFrom());
					flag = true;
				}
			}
		}

		return Di;
	}

	/**
	 * Create set E of erasable non-terminals.
	 * 
	 * @param rules
	 *            The rules of the grammar
	 * @return E An array that contains the indices of erasable non-terminals
	 */
	private static ArrayList<Integer> getErasables(ArrayList<Rule> rules) {
		ArrayList<Integer> E = new ArrayList<>();

		boolean flag = true;
		while (flag) {
			flag = false;

			for (int i = 0; i < rules.size(); i++) {
				Rule r = rules.get(i);

				// If the symbol getting replaced by the rule is already a known
				// erasable, there's no need to do anything but skip to the next
				// rule
				boolean b1 = !E.contains(r.getFrom());

				// Loop all right-hand-side symbols and check if each and every
				// one of them is already known to be erasable
				boolean b2 = true;
				for (int j = 0; j < r.getToLength(); j++) {
					b2 = b2 & (r.getTo(j) == Alphabet.idEmptyString | E.contains(r.getTo(j)));
				}

				// If both checks hold, we've found a new erasable; append it
				// and raise flag
				if (b1 & b2) {
					E.add(r.getFrom());
					flag = true;
				}
			}
		}

		return E;
	}

	private static DecompositionSolver getDerivationsProbabilitiesMatA(Alphabet al, ArrayList<Rule> rules) {

		int n = al.getNoofNonTerminals();

		// Allocate matrix a and populate it
		double[][] a = new double[n][n];
		for (int j = 0; j < n; j++) {
			for (int k = 0; k < n; k++) {

				// initialize to zero
				a[j][k] = 0;

				if (j == k) {
					// main diagonal is set to -1
					a[j][k] = -1;

				} else {
					// search for rule: j -> k
					for (int ri = 0; ri < rules.size(); ri++) {
						Rule r = rules.get(ri);

						// Escape if not short rule
						if (r.getToLength() != 1) {
							continue;
						}

						boolean b1 = r.getFrom() == al.getIdNonTerminal(j);
						boolean b2 = r.getTo(0) == al.getIdNonTerminal(k);
						if (b1 & b2) {
							a[j][k] = r.getProbability();
							break; // we assume/require each rule to be unique
						}
					} // ri

				}
			} // k
		} // j

		// Allocate vector b and populate it
		double[] b = new double[n];
		for (int j = 0; j < n; j++) {
			b[j] = 0;
		}

		RealMatrix matA = new Array2DRowRealMatrix(a, false);
		return new LUDecomposition(matA).getSolver();
	}

	private static double[] getDerivationsProbabilitiesVecB(ContextFreeGrammar cfg, int id) {

		Alphabet al = cfg.getAlphabet();

		// Allocate b and set to zero
		double[] b = new double[al.getNoofNonTerminals()];
		for (int i = 0; i < b.length; i++) {
			b[i] = 0;
		}

		// Loop rules
		for (int i = 0; i < cfg.rules.size(); i++) {
			Rule r = cfg.rules.get(i);

			if (r.getToLength() != 1) {
				continue;
			}

			if (r.getTo(0) == id) {
				b[al.getIdNonTerminal(r.getFrom())] = -r.getProbability();
			}
		}

		return b;
	}

	private static double[] getErasablesProbabilities(ArrayList<Rule> rules, ArrayList<Integer> E) {

		if (E.isEmpty()) {
			return new double[0];
		}

		final int n = E.size();
		final double[] C = new double[n];
		final double[][] B = new double[n][n];
		final double[][][] A = new double[n][n][n];

		// First fill vector C
		// C[i] contains the probability of erasing E.get(i) directly,
		// Pr{ E.get(i) => e }
		for (int i = 0; i < n; i++) {
			C[i] = 0;
			for (int j = 0; j < rules.size(); j++) {
				Rule r = rules.get(j);
				if (r.getFrom() == E.get(i) & r.getToLength() == 1 & r.getTo(0) == Alphabet.idEmptyString) {
					C[i] = r.getProbability();
					break;
				}
			}
		}

		// Now fill matrix B
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				// B[i][j] contains the probability Pr{ E.get(i) => E.get(j) }
				if (i == j) {
					B[i][j] = -1; // Set to -1 instead of 0 to save having to do
									// B = B - I at the end
				} else {
					for (int k = 0; k < rules.size(); k++) {
						Rule r = rules.get(k);
						if (r.getFrom() == E.get(i) & r.getToLength() == 1 & r.getTo(0) == E.get(j)) {
							B[i][j] = r.getProbability();
							break;
						}
					}
				}
			}
		}

		// Now fill matrices A
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					// A[i][j][k] contains the probability Pr{ E.get(i)
					// =>
					// E.get(j) E.get(k) }
					A[i][j][k] = 0;
					for (int l = 0; l < rules.size(); l++) {
						Rule r = rules.get(l);
						if (r.getFrom() == E.get(i) & r.getToLength() == 2
								&& r.getTo(0) == E.get(j) & r.getTo(1) == E.get(k)) {
							A[i][j][k] = r.getProbability();
							break;
						}
					}
				}
			}
		}

		// Start working on solving

		EmptyRulesCost erc = new EmptyRulesCost(n, A, B, C);
		EmptyRulesJacobian erj = new EmptyRulesJacobian(n, A, B, C);

		double[] zeros = new double[n];
		double[] halves = new double[n];
		for (int i = 0; i < n; i++) {
			zeros[i] = 0;
			halves[i] = 0.5;
		}

		LeastSquaresBuilder lsb = new LeastSquaresBuilder();
		lsb.model(erc, erj);
		lsb.start(halves);
		lsb.target(zeros);
		lsb.maxEvaluations(1000);
		lsb.maxIterations(1000);
		LeastSquaresProblem lsp = lsb.build();

		LevenbergMarquardtOptimizer lmOpt = new LevenbergMarquardtOptimizer();
		Optimum o = lmOpt.optimize(lsp);

		return o.getPoint().toArray();
	}

}
