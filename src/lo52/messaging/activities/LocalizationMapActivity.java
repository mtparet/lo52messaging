package lo52.messaging.activities;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import lo52.messaging.R;
import lo52.messaging.model.Localisation;
import lo52.messaging.model.User;
import lo52.messaging.model.UserItemizedOverlay;
import lo52.messaging.services.NetworkService;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Activité permettant de visualiser la position géographique des utilisateurs sur une carte
 * tutoriel : http://developer.android.com/resources/tutorials/views/hello-mapview.html
 */
public class LocalizationMapActivity extends MapActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "LocalizationMapActivity";
	private MapController mc;
	
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.localization_map);
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    // Augmente le zoom
	    mc = mapView.getController();
	    mc.setZoom(17);
	    
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
	    	OverlayItem overlayitem = new OverlayItem(point, currentUser.getName(), "");
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
	    	  if (lastUserPosition != null) { 
	    		  UserItemizedOverlay itemizedoverlay = new UserItemizedOverlay(drawableOthers, this);
	    		  GeoPoint point = new GeoPoint((int)lastUserPosition.getLat(),(int)lastUserPosition.getLon());
	    		  OverlayItem overlayitem = new OverlayItem(point, currentUser.getName(), "");
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
}