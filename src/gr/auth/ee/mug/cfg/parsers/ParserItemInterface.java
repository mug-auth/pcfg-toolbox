/**
 * 
 */
package gr.auth.ee.mug.cfg.parsers;

/**
 * An interface for the payload of trees (implemented via {@code TreeNode}.
 * 
 * @author Vasileios Papapanagiotou
 */
public interface ParserItemInterface {

	/**
	 * 
	 * @return The unique id of the object. This is used to determine the
	 *         multiple instantiations of symbols in a parse tree
	 */
	public long getId();

	/**
	 * @return The id of the rule that is used to expand the node
	 */
	public int getRuleId();

	/**
	 * @return The id of the symbol that is displayed on the node
	 */
	public int getSymbolId();

}
