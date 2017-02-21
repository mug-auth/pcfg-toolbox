package gr.auth.ee.mug.cfg.examples;

import gr.auth.ee.mug.cfg.dottools.Tree2DOT;
import gr.auth.ee.mug.cfg.dottools.TreeNode;
import gr.auth.ee.mug.cfg.grammar.Alphabet;
import gr.auth.ee.mug.cfg.grammar.ContextFreeGrammar;
import gr.auth.ee.mug.cfg.grammar.FriendlyReader;
import gr.auth.ee.mug.cfg.parsers.ParserInterface;
import gr.auth.ee.mug.cfg.parsers.earleyparser.EarleyParser;
import gr.auth.ee.mug.cfg.tools.LoggerText;

public class TestEarleyParser {

	public static void main(String[] args) throws Exception {

		ContextFreeGrammar cfg = FriendlyReader.readGrammar("friendlyG.txt");
		Alphabet al = cfg.getAlphabet();

		String string2parse;
		if (args.length == 0) {
			string2parse = "a a a a";
		} else {
			string2parse = args[0];
		}
		int[] stringIds = al.stringIDs(string2parse);

		EarleyParser recognizer = new EarleyParser(cfg, stringIds);

		ParserInterface parserInterface = recognizer;

		System.out.println("--- This is grammar cfg ---\n");
		System.out.println("This is the alphabet");
		System.out.println(cfg.printAlphabet());
		System.out.println("These are the rules");
		System.out.println(cfg.printRules());
		System.out.println("--- This is the parsed string ---\n");
		System.out.println(al.printString(stringIds) + "\n");
		System.out.println("--- This is the Earley EarleyParser ---\n");
		System.out.println(recognizer.printEarleyStates(true));

		System.out.println("--- This is the parser interface ---\n");
		System.out.println("canGenerate: " + String.valueOf(parserInterface.canGenerate()));
		System.out.println("Number of trees: " + String.valueOf(parserInterface.getNoofTrees()));

		LoggerText logger = new LoggerText("dots/autogen-earley.dot");
		logger.open();
		for (int i = 0; i < parserInterface.getNoofTrees(); i++) {
			TreeNode root = parserInterface.getTreeRoot(i);
			double pr = root.reduceProduct(cfg);
			System.out.println("Tree " + String.valueOf(i) + " (" + String.valueOf(pr) + "): " + root.toString(al));
			Tree2DOT tree2DOT = new Tree2DOT(root, cfg, "tree_" + String.valueOf(i));
			logger.append(tree2DOT.dotCode);
			System.out.println(tree2DOT.dotCode);
		}
		logger.close();
	}

}
