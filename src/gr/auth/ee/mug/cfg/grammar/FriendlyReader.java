package gr.auth.ee.mug.cfg.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FriendlyReader {

	/**
	 * Adds to grammar a set of rules described by strings. Each string should
	 * be of the form {@code %f %s %s ...}, where the first double is the rule
	 * probability, the first string corresponds to the non-terminal symbol that
	 * is replaced by the rule, and the subsequent strings correspond to the
	 * terminals and non-terminals that replace the first symbol.
	 * 
	 * Note that the symbols in the alphabet must have unique string identifiers
	 * ({@code symbol} attribute).
	 * 
	 * @param cfg
	 * @param input
	 */
	public static void addRules(ContextFreeGrammar G, String[] input) throws Exception {

		Alphabet A = G.getAlphabet();
		String[] s;
		double p;
		int from;
		int[] to;

		for (int i = 0; i < input.length; i++) {
			s = input[i].split(" ");
			p = Double.parseDouble(s[0]);
			from = A.findSymbolId(s[1]);
			to = new int[s.length - 2];
			for (int j = 0; j < to.length; j++) {
				to[j] = A.findSymbolId(s[2 + j]);
			}
			G.addRule(new Rule(from, to, p));
		}
	}

	/**
	 * Adds to alphabet a set of symbols described by strings. Each string
	 * should be of the form: {@code %i %s %}, where the first integer is 0 for
	 * non-terminals and 1 for terminals, the first $s is the string of the
	 * symbol (no spaces are allowed) and the second string is an optional
	 * description of the symbol.
	 * 
	 * Note that since A is created with {@code new}, it already includes the
	 * start symbol S and the empty string symbol e.
	 * 
	 * @param A
	 * @param areTerminal
	 * @param input
	 */
	public static void addSymbols(Alphabet A, String[] input) throws Exception {

		String[] s;
		boolean isTerminal;
		String description;

		for (int i = 0; i < input.length; i++) {
			s = input[i].split(" ", 3);
			isTerminal = Integer.parseInt(s[0]) == 1;
			if (s.length == 2) {
				description = null;
			} else {
				description = s[2];
			}
			A.addSymbol(s[1], isTerminal, description);
		}

	}

	public static ContextFreeGrammar readGrammar(String filename) throws Exception {

		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String s;

		s = bufferedReader.readLine();
		int len1 = Integer.parseInt(s);
		String[] input1 = new String[len1];

		for (int i = 0; i < len1; i++) {
			input1[i] = bufferedReader.readLine();
		}

		s = bufferedReader.readLine();
		int len2 = Integer.parseInt(s);
		String[] input2 = new String[len2];

		for (int i = 0; i < len2; i++) {
			input2[i] = bufferedReader.readLine();
		}

		bufferedReader.close();
		fileReader.close();

		Alphabet A = new Alphabet();
		addSymbols(A, input1);
		ContextFreeGrammar G = new ContextFreeGrammar(A);
		addRules(G, input2);

		return G;
	}

}
