package lo52.messaging.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


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

	public static final Set<String> FILE_IMAGE_EXTENSIONS = new HashSet<String>(Arrays.asList(
			new String[] {"jpg", "jpeg", "png"}
			));
	
	public static final Set<String> FILE_AUDIO_EXTENSIONS = new HashSet<String>(Arrays.asList(
			new String[] {"mp3", "ogg"}
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


	/**
	 * Retourne le rang d'une valeur dans une liste
	 * @param list
	 * @param value
	 * @return	int >= 0 si trouvé, -1 sinon
	 */
	public static <E> int getValueRankInList(ArrayList<E> list, E value) {
		int rank = -1;
		boolean found = false;
		int k = 0;

		while (k < list.size() && !found) {

			if (list.get(k).equals(value)) {
				rank = k;
				found = true;
			}
			k++;
		}
		return rank;
	}

	public static byte[] getByte(File file){
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(file, "r");


			try {
				// Get and check length
				long longlength = 0;
				try {
					longlength = f.length();
				} catch (IOException e) {
					e.printStackTrace();
				}
				int length = (int) longlength;
				if (length != longlength);

				// Read file and return data
				byte[] data = new byte[length];
				try {
					f.readFully(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return data;
			}
			finally {
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static byte[] compress(String string) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(string.getBytes());
		gos.close();
		byte[] compressed = os.toByteArray();
		os.close();
		return compressed;
	}

	public static String decompress(byte[] compressed) throws IOException {
		final int BUFFER_SIZE = 32;
		ByteArrayInputStream is = new ByteArrayInputStream(compressed);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			string.append(new String(data, 0, bytesRead));
		}
		gis.close();
		is.close();
		return string.toString();
	}

	public static void writeFile(File yourFile,byte[] fileBytes){
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(yourFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			bos.write(fileBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isInFileList(String filename, File[] listFile){
		
		for(File file : listFile){
			if(filename == file.getName()){
				return true;
			}
		}
		
		return false;
	}


}
