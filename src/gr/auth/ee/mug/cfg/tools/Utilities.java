package gr.auth.ee.mug.cfg.tools;

import java.util.ArrayList;

/**
 * 
 * @author Vasileios Papapanagiotou
 */
public class Utilities {

	public static ArrayList<Integer> indexOfAll(ArrayList l, Object o) {
		ArrayList<Integer> idx = new ArrayList<>();
		for (int i = 0; i < l.size(); i++) {
			if (o.equals(l.get(i))) {
				idx.add(i);
			}
		}
		return idx;
	}
}
