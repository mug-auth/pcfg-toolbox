package gr.auth.ee.mug.cfg.parsers.cnfparser;

import gr.auth.ee.mug.cfg.parsers.ParserItemInterface;

/**
 * An item of the dynamic programming matrix that is populated when parsing a
 * string.<br>
 * <br>
 * The dynamic programming matrix is a 3-dimensional matrix; for all notation
 * assume that the current {@code CNFItem} object coordinates are (i1, i2, i3).
 * 
 * @author Vasileios Papapanagiotou
 */
public class CNFItem implements ParserItemInterface {

	/**
	 * A unique id for each {@code CNFItem} object (for the java process). It is
	 * used to ease the effort of creating a DOT plot.
	 */
	public final long id;

	/**
	 * The symbol's unique id that is stored in this {@code CNFItem}.
	 */
	public final int symbolID;

	/**
	 * Split index.
	 */
	public final int k;

	/**
	 * Third-dimension index for left child.
	 */
	public final int li3;

	/**
	 * Third-dimension index for right child.
	 */
	public final int ri3;

	/**
	 * The {@code Rule} id that is used from this {@code CNFItem}.
	 */
	public final int ruleID;

	/**
	 * Create a new entry. All other parameters are set to defaults, however
	 * this is NOT equivalent to {@code new CNFItem(symbolID, -1, -1, -1, -1)}, as
	 * the {@code id} attribute is set to {@code -1}.
	 * 
	 * @param symbolID
	 *            The symbol's is of the {@code CNFItem}
	 */
	public CNFItem(int symbolID) {
		this.symbolID = symbolID;
		k = -1;
		li3 = -1;
		ri3 = -1;
		ruleID = -1;
		id = -1; // maxID++;
	}

	/**
	 * Create a new entry.
	 * 
	 * @param symbolID
	 *            The symbol's id of the {@code CNFItem}
	 * @param k
	 *            The split index
	 * @param li3
	 *            Third-dimension index for left child
	 * @param ri3
	 *            Third-dimension index for right child
	 * @param ruleID
	 *            The index of the rule in the rules of the grammar
	 */
	public CNFItem(int symbolID, int k, int li3, int ri3, int ruleID) {
		this.symbolID = symbolID;
		this.k = k;
		this.li3 = li3;
		this.ri3 = ri3;
		this.ruleID = ruleID;
		id = maxID++;
	}

	@Override
	public boolean equals(Object o) {
		// Fail if different class
		if (!o.getClass().equals(CNFItem.class)) {
			return false;
		}
		return ((CNFItem) o).symbolID == symbolID;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public int getRuleId() {
		return ruleID;
	}

	@Override
	public int getSymbolId() {
		return symbolID;
	}

	/**
	 * @return The symbol's id
	 */
	public String getSymbolIdToString() {
		return String.valueOf(symbolID);
	}

	@Override
	public String toString() {
		return String.valueOf(symbolID);
	}

	private static long maxID = 0;

}
