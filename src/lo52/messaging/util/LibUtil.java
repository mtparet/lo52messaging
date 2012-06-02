package lo52.messaging.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Libraire de méthodes utilitaires, pas forcément liées à Android
 */
public class LibUtil {

	/********************************************************
	 *
	 * 	Constantes globales à l'appli
	 * 
	 ********************************************************/


	/**
	 * Extensions autorisées pour les fichiers envoyés dans les conversations
	 */
	public static final Set<String> MEDIA_ALLOWED_EXTENSIONS = new HashSet<String>(Arrays.asList(
			new String[] {"jpg", "jpeg", "png", "avi"}
	));



	/********************************************************
	 *
	 * 	Méthodes
	 * 
	 ********************************************************/

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
