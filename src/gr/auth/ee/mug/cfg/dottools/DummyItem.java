package gr.auth.ee.mug.cfg.dottools;

import gr.auth.ee.mug.cfg.parsers.ParserItemInterface;

/**
 * Dummy implementation
 * 
 * @author Vasileios Papapanagiotou
 */
public class DummyItem implements ParserItemInterface {

	private final long id;
	private final int ruleId;
	private final int symbolId;

	public DummyItem(long id, int ruleId, int symbolId) {
		this.id = id;
		this.ruleId = ruleId;
		this.symbolId = symbolId;
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

}
