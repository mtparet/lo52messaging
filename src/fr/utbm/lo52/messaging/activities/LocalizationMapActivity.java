package fr.utbm.lo52.messaging.activities;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import fr.utbm.lo52.messaging.R;
import fr.utbm.lo52.messaging.model.Localisation;
import fr.utbm.lo52.messaging.model.User;
import fr.utbm.lo52.messaging.model.UserItemizedOverlay;
import fr.utbm.lo52.messaging.services.NetworkService;

/**
 * Activité permettant de visualiser la position géographique des utilisateurs sur une carte
 */
public class LocalizationMapActivity extends MapActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "LocalizationMapActivity";
	private MapController mc;
	private MapView mapView;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localization_map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		// Augmente le zoom
		mc = mapView.getController();
		mc.setZoom(16);

		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		//en premier on efface tout les anciens overlay
		mapView.getOverlays().clear();
		
		// get user location
				User currentUser = NetworkService.getUser_me();
				Localisation lastPosition = currentUser.getLocalisation();

				// get User list
				Hashtable<Integer, User> userList =  NetworkService.getListUsers();

				List<Overlay> mapOverlays = mapView.getOverlays();
				Drawable drawableMyself		= this.getResources().getDrawable(R.drawable.map_blue_dot);
				Drawable drawableOthers 	= this.getResources().getDrawable(R.drawable.map_red_dot);

				// Add current user on the map
				if (lastPosition != null) { 
					UserItemizedOverlay itemizedoverlay = new UserItemizedOverlay(drawableMyself, this);
					GeoPoint point = new GeoPoint((int)lastPosition.getLat(),(int)lastPosition.getLon());
					OverlayItem overlayitem = new OverlayItem(point, currentUser.getName(), getString(R.string.maps_user_position));
					itemizedoverlay.addOverlay(overlayitem);
					mapOverlays.add(itemizedoverlay);

					// Centrage de la vue sur l'utilisateur
					mc.animateTo(new GeoPoint((int)(lastPosition.getLat()), (int)(lastPosition.getLon())));
				}

				// Add other users on the map
				Iterator<User> itValue = userList.values().iterator();
				while(itValue.hasNext()){

					User mUser = (User)itValue.next();
					if (mUser.getId() != currentUser.getId()) { // Si ce n'est pas l'utilisateur actuel alors on l'ajoute sur la carte
						Localisation lastUserPosition = mUser.getLocalisation();
						if (lastUserPosition != null && lastPosition != null) { 
							UserItemizedOverlay itemizedoverlay = new UserItemizedOverlay(drawableOthers, this);
							GeoPoint point = new GeoPoint((int)lastUserPosition.getLat(),(int)lastUserPosition.getLon());

							// Calcul de la distance avec l'utilisateur courant
							float results[] = {0,0,0};
							Location.distanceBetween(lastUserPosition.getLat()/1E6, lastUserPosition.getLon()/1E6, lastPosition.getLat()/1E6, lastPosition.getLon()/1E6, results);
							String unit = "m";

							// Adaptation mètre / kilomètre
							if (results[0] > 1000) {
								results[0] /= 1000;
								unit = "km";
							}
							String distance = String.format("%.2f", results[0]);

							// Affichage de l'item
							OverlayItem overlayitem = new OverlayItem(point, mUser.getName(), getString(R.string.maps_user_distance_from_user) + " " + distance + unit);
							itemizedoverlay.addOverlay(overlayitem);
							mapOverlays.add(itemizedoverlay);
						}
					}
				}
		
	}

	@Override
	public void onBackPressed () {
		this.getParent().onBackPressed();
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		this.getParent().onMenuItemSelected(featureId, item);
		return super.onMenuItemSelected(featureId, item);
	}
}