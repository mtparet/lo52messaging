package lo52.messaging.util;

import java.io.IOException;
import java.net.InetAddress;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.text.format.Formatter;

public class Network {
	
	public static InetAddress getBroadcastAddress(Context mContext, boolean isLocalhost) throws IOException {

		if(isLocalhost){
			return InetAddress.getByName("127.0.0.1");
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
	
	public static InetAddress getWifiAddress(Context mContext, boolean isLocalhost) throws IOException {
		if(isLocalhost){
			return InetAddress.getByName("127.0.0.1");
		}else{
			WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    // handle null somehow
	
		    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	
		    return  InetAddress.getByName(Formatter.formatIpAddress(dhcp.ipAddress));
		}
	}

}
