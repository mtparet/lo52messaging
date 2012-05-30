package lo52.messaging.activities;

import java.util.List;

import lo52.messaging.R;
import android.graphics.drawable.Drawable;
import lo52.messaging.model.UserItemizedOverlay;
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
	    
	    /*MapController mc = mapView.getController();
	    mc.setZoom(17);*/
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.map_point);
	    UserItemizedOverlay itemizedoverlay = new UserItemizedOverlay(drawable, this);
	    GeoPoint point = new GeoPoint(19240000,-99120000);
	    OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	}
}