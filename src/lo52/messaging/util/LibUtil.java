package lo52.messaging.util;

import java.util.ArrayList;


/**
 * Libraire de méthodes utilitaires, pas forcément liées à Android
 */
public class LibUtil {

	/**
	 * Permet de comparer deux listes sans tenir compte de l'ordre des éléments, contrairement à .equals()
	 * @param <E>
	 * @param list
	 * @return
	 */
	public static <E> boolean equalsListsOrderInsensitive(ArrayList<E> list1, ArrayList<E> list2) {
		boolean equal = true;
		
		if (list1.size() == list2.size()) {
			for (E elem : list1) {
				if (!list2.contains(elem)) equal = false;
			}
		} else return false;
		
		return equal;
	}
}
