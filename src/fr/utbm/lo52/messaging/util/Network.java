package fr.utbm.lo52.messaging.util;

import java.io.IOException;
import java.net.InetAddress;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.text.format.Formatter;

/**
 *	Classe utilitaire de fonctions liées au réseau. 
 */
public class Network {
	
	public static InetAddress getBroadcastAddress(Context mContext) throws IOException {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean isDev = preferences.getBoolean("dev_prefs_emulateur", false);
		
		if(isDev){
			return InetAddress.getByName("10.0.2.2");
		}else{
		    WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    // handle null somehow

		    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		    return InetAddress.getByAddress(quads);
		}

	}
	
	public static InetAddress getWifiAddress(Context mContext) throws IOException {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean isDev = preferences.getBoolean("dev_prefs_emulateur", false);
		
		if(isDev){
			return InetAddress.getByName("10.0.2.2");
		}else{
			WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    // handle null somehow
	

	
		    return  InetAddress.getByName(Formatter.formatIpAddress(dhcp.ipAddress));
		}
	}

}
