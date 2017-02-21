package gr.auth.ee.mug.cfg.parsers;

import gr.auth.ee.mug.cfg.dottools.TreeNode;

/**
 * 
 * @author Vasileios Papapanagiotou
 */
public interface ParserInterface {

	public boolean canGenerate();

	public int getNoofTrees();

	public TreeNode getTreeRoot(int i);

	public final int noRuleId = -1;

}
