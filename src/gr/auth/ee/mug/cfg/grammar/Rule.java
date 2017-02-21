package gr.auth.ee.mug.cfg.grammar;

/**
 * Class that defines a rule for a grammar.
 * 
 * @author Vasileios Papapanagiotou
 */
public class Rule {

	private final int from;
	private final int[] to;
	private final double pr;

	/**
	 * Creates a new rule of the form A -> B1 B2 ... Bk. Equivalent to
	 * {@code Rule(from, to, 1)}.
	 * 
	 * @param from
	 *            The id of 'A' in the grammar's alphabet.
	 * @param to
	 *            An array of length k, where the i-th element is the id of 'Bi'
	 *            in the grammar's alphabet.
	 */
	public Rule(int from, int[] to) {
		this.from = from;
		this.to = to.clone();
		pr = 1;
	}

	/**
	 * Creates a new rule of the form A -> B1 B2 ... Bk.
	 * 
	 * @param from
	 *            The id of 'A' in the grammar's alphabet.
	 * @param to
	 *            An array of length k, where the i-th element is the id of 'Bi'
	 *            in the grammar's alphabet.
	 * @param pr
	 *            The probability of the rule
	 */
	public Rule(int from, int[] to, double pr) {
		this.from = from;
		this.to = to.clone();
		this.pr = pr;
	}

	@Override
	public Rule clone() {
		return new Rule(from, to.clone(), pr);
	}

	@Override
	public boolean equals(Object o) {

		// Fail if different class
		if (!o.getClass().equals(Rule.class)) {
			return false;
		}

		Rule r = (Rule) o;

		// Check same from
		if (from != r.from) {
			return false;
		}
		
		// Check same to length
		if (to.length != r.to.length) {
			return false;
		}
		
		// Check same to values
		for (int i = 0; i < to.length; i++) {
			if (to[i] != r.to[i]) {
				return false;
			}
		}

		// Congrats!
		return true;
	}

	/**
	 * @return The id of symbol A
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * @return The probability of this rule
	 */
	public double getProbability() {
		return pr;
	}

	/**
	 * @param i
	 *            An index greater than or equal to 0 and less than
	 *            {@code getToLength()}
	 * @return The id of symbol Bi
	 */
	public int getTo(int i) {
		return to[i];
	}

	/**
	 * @return The number of symbols that the rule produces (a.k.a. k)
	 */
	public int getToLength() {
		return to.length;
	}

	@Override
	public String toString() {
		String s;
		s = String.valueOf(from) + " -> " + String.valueOf(to[0]);
		for (int i = 1; i < to.length; i++) {
			s += " " + String.valueOf(to[i]);
		}
		return s + "\n";
	}

}
