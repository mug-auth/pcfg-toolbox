package gr.auth.ee.mug.cfg.examples;

import gr.auth.ee.mug.cfg.dottools.Tree2DOT;
import gr.auth.ee.mug.cfg.dottools.TreeNode;
import gr.auth.ee.mug.cfg.grammar.Alphabet;
import gr.auth.ee.mug.cfg.grammar.ContextFreeGrammar;
import gr.auth.ee.mug.cfg.grammar.FriendlyReader;
import gr.auth.ee.mug.cfg.parsers.ParserInterface;
import gr.auth.ee.mug.cfg.parsers.cnfparser.CNFParser;
import gr.auth.ee.mug.cfg.tools.LoggerText;

public class TestCNFParser {

	public static void main(String[] args) throws Exception {

		ContextFreeGrammar cfg = FriendlyReader.readGrammar("friendlyG.txt");
		ContextFreeGrammar cnfcfg = ContextFreeGrammar.chomskyNormal(cfg);
		Alphabet al = cnfcfg.getAlphabet();

		String string2parse;
		if (args.length == 0) {
			string2parse = "a a a";
		} else {
			string2parse = args[0];
		}
		int[] stringIds = al.stringIDs(string2parse);

		CNFParser parser = new CNFParser(cnfcfg, stringIds);
		ParserInterface parserInterface = parser;

		System.out.println("--- This is grammar cfg ---\n");
		System.out.println("This is the alphabet");
		System.out.println(cfg.printAlphabet());
		System.out.println("These are the rules");
		System.out.println(cfg.printRules());
		System.out.println("--- This is grammar cnfcfg ---\n");
		System.out.println("These are the rules");
		System.out.println(cnfcfg.printRules());
		System.out.println("--- This is the parsed string ---\n");
		System.out.println(al.printString(stringIds) + "\n");

		System.out.println("--- This is the parser interface ---\n");
		System.out.println("canGenerate: " + String.valueOf(parserInterface.canGenerate()));
		System.out.println("Number of trees: " + String.valueOf(parserInterface.getNoofTrees()));

		LoggerText logger = new LoggerText("dots/autogen-cnf.dot");
		logger.open();
		for (int i = 0; i < parserInterface.getNoofTrees(); i++) {
			TreeNode root = parserInterface.getTreeRoot(i);
			double pr = root.reduceProduct(cnfcfg);
			System.out.println("Tree " + String.valueOf(i) + " (" + String.valueOf(pr) + "): " + root.toString(al));
			Tree2DOT tree2DOT = new Tree2DOT(root, cnfcfg, "tree_" + String.valueOf(i));
			logger.append(tree2DOT.dotCode);
		}
		logger.close();
	}

}