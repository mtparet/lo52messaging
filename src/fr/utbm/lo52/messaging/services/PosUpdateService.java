/**
 * Service qui fait la mise à jour de la position de l'utilisateur dans la base de donnée locale et l'envoi au serveur.
 * en tâche de fond
 */

package fr.utbm.lo52.messaging.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.google.android.maps.GeoPoint;

import fr.utbm.lo52.messaging.model.Localisation;
import fr.utbm.lo52.messaging.model.User;

/**
 * Service de mise à jour de la position. Permet de détecter les changements
 * de position de l'utilisateur et d'en notifier les autres.
 */
public class PosUpdateService extends Service {

	static final String TAG = "PosUpdateService";

	// intervalle entre les maj = 2 minute 
	static final int DELAY = 10000;

	// est-ce que le service 
	// est en train de s'exécuter ?
	private boolean runFlag = false;

	// thread séparé qui effectue la MAJ
	private Updater updater;
	
	private GeoPoint previousLoca = new GeoPoint(0,0);;

	public IBinder onBind(Intent intent) { // service lié
		return null;                         // rendre un Binder sur le service
	}

	// invoquée une fois à la création du service
	public void onCreate() {  
		super.onCreate();

		// créer le fil de MAJ 
		// à la création du service
		this.updater = new Updater(); 


		Log.d(TAG, "onCreated");
	}
	// invoquée à chaque fois que le service recoit une intention lancée via startService()
	// meme si le service est déjà démarré, 
	// cette méthode sera de novueau invoquée pour un nouveau lancement déintention
	public int onStartCommand(Intent intent, int flags, int startId) { // 
		super.onStartCommand(intent, flags, startId);

		// démarrer le fil de MAJ 
		// au démarrage du service

		this.runFlag = true; 
		if (!this.updater.isAlive())
			this.updater.start();

		Log.d(TAG, "onStarted");
		return START_STICKY;
	}

	@Override
	// invoquée juste avant la destruction du service via la requête stopService()
	public void onDestroy() {
		super.onDestroy();

		// arrêter et détruire le fil de MAJ 
		// à la destruction du service 
		// mettre updater à null pour le GC

		this.runFlag = false; 
		this.updater.interrupt(); 
		this.updater = null;

		Log.d(TAG, "onDestroyed");
	}

	/**
	 * Thread that performs the actual update from the online service
	 */
	private class Updater extends Thread {  // note : AsynchTask pour les threads UI

		private LocationManager lm;

		public Updater() {
			super("UpdaterService-Updater");  // donner un nom au thread à des fins de debug
		}

		@Override
		public void run() {                 // méthode invoquée pour démarrer le fil
			PosUpdateService updaterService = PosUpdateService.this;  // réf. Sur le service
			while (updaterService.runFlag) {  // MAJ via les méthode onStartCOmmand et onDestroy
				Log.i(TAG, "Updater running");
				try {
					// Service work
					// Recupere la position de l'utilisateur selon le service disponnible
					lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					GeoPoint point;
					if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
						point = new GeoPoint(
								(int) (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude() * 1E6),
								(int) (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude() * 1E6));
						Log.d(TAG, "Récupéré nouvelle position GPS OK");
					}
					else if (lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
						point = new GeoPoint(
								(int) (lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude() * 1E6),
								(int) (lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude() * 1E6));
						Log.d(TAG, "Récupéré nouvelle position network OK");
					}
					else {
						Log.d(TAG, "Nouvelle position par defaut");
						point = new GeoPoint(47641426,6845669);
						
					}

					// Envoyer les données au network service pour que les autres utilisateurs mettents à jour ma position seulement si elles ont changées
					if(point.getLatitudeE6() != previousLoca.getLatitudeE6() || point.getLongitudeE6() != previousLoca.getLongitudeE6()){
						previousLoca = point;
						User currentUser = NetworkService.getUser_me();
						Localisation currentPosn = new Localisation(point.getLatitudeE6(), point.getLongitudeE6());
						currentUser.setLocalisation(currentPosn);
						currentPosn.sendToNetworkService(getApplicationContext());
					}

					Log.d(TAG, "Updater ran");
					Thread.sleep(DELAY);	// s'endormir entre chaque mise à jour
				} catch (InterruptedException e) {  
					// exception est déclenchée lorsqu'on signale interrupt()
					updaterService.runFlag = false;
				}
			}
		}
	} // Updater

}