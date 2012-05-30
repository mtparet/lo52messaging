package lo52.messaging.model;

import lo52.messaging.services.NetworkService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Localisation implements Parcelable {
	private float lat;
	private float lon;

	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLon() {
		return lon;
	}
	public Localisation(float lat, float lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}
	public Localisation(Parcel in) {
		this.lat = in.readFloat();
		this.lon = in.readFloat();
		
	}
	public void setLon(float lon) {
		this.lon = lon;
	}

	public void sendToNetworkService(Context ctx){
		Intent broadcastIntent = new Intent(NetworkService.Receivelocalisation);
		Bundle bundle = new Bundle();

		bundle.putParcelable("localisation", this);
		broadcastIntent.putExtra("localisation", bundle);

		ctx.sendBroadcast(broadcastIntent);

		// Indique à l'activité ConversationPagerActivity qu'il devra créer un fragment correspondant
		Log.d("TAG", "info de localisation envoyé ");

	}
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(lat);
		dest.writeFloat(lon);
		
	}
	
	public static final Parcelable.Creator<Localisation> CREATOR= new Parcelable.Creator<Localisation>() {
		public Localisation createFromParcel(Parcel in) {
			return new Localisation(in);
		}

		public Localisation[] newArray(int size) {
			return new Localisation[size];
		}
	};
}
