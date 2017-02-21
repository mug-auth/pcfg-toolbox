package gr.auth.ee.mug.cfg.parsers.earleyparser;

import java.util.ArrayList;

import gr.auth.ee.mug.cfg.parsers.ParserItemInterface;

/**
 * 
 * 
 * @author Vasileios Papapanagiotou
 */
public class EarleyItem implements ParserItemInterface {

	public final long id;

	public final int ruleId;
	public final int symbolId;
	public final int startIdx;
	public final int nextIdx;
	public final int stateIdx;
	public final ArrayList<Long> backId = new ArrayList<>();
	public final String comment;
	public EarleyItem(int ruleId, int symbolId, int startIdx, int nextIdx, int stateIdx, String comment) {

		this.id = noof++;
		this.ruleId = ruleId;
		this.symbolId = symbolId;
		this.startIdx = startIdx;
		this.nextIdx = nextIdx;
		this.stateIdx = stateIdx;
		this.comment = comment;
	}
	
	public EarleyItem(long id, int ruleId, int symbolId, int startIdx, int nextIdx, int stateIdx, String comment) {

		this.id = id;
		this.ruleId = ruleId;
		this.symbolId = symbolId;
		this.startIdx = startIdx;
		this.nextIdx = nextIdx;
		this.stateIdx = stateIdx;
		this.comment = comment;
	}

	@Override
	public EarleyItem clone() {

		EarleyItem e;
		e = new EarleyItem(id, ruleId, symbolId, startIdx, nextIdx, stateIdx, comment);
		return e;
	}

	@Override
	public boolean equals(Object o) {

		if (!o.getClass().equals(EarleyItem.class)) {
			return false;
		}

		EarleyItem e = (EarleyItem) o;
		boolean b0 = stateIdx == e.stateIdx;
		boolean b1 = ruleId == e.ruleId;
		boolean b2 = symbolId == e.symbolId;
		boolean b3 = startIdx == e.startIdx;
		boolean b4 = nextIdx == e.nextIdx;
		boolean b5 = backId.equals(e.backId);

		return b0 & b1 & b2 & b3 & b4 & b5;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public int getRuleId() {
		return ruleId;
	}

	@Override
	public int getSymbolId() {
		return symbolId;
	}

	@Override
	public String toString() {
		
		String s = String.valueOf(stateIdx) + ": (";
		s += String.valueOf(ruleId) + ", ";
		s += String.valueOf(nextIdx) + ", ";
		s += String.valueOf(startIdx) + ")";
		return s;
	}

	private static long noof = 0;

}
