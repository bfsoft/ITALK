package staff.iprofe.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Common {
	
	public static void showMsg(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static final String host = "http://www.zhangyaqiang.tc00.info/";
	
	public static String friendDataSource() {
		return host + "index.php?r=mobi/ajax/friendList";
	}

	public static String checkNetworkInfo(Context context){    
        try {
        	String NT = null;
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    		if (connectivity != null) {
    		    NetworkInfo info = connectivity.getActiveNetworkInfo();
    		    if (info == null || !info.isAvailable() || info.getState() == NetworkInfo.State.CONNECTED) {
    		        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
    		        	
    		            switch (info.getSubtype()) {
    		                case TelephonyManager.NETWORK_TYPE_1xRTT:  // 2G ~ 50-100 kbps
    		                case TelephonyManager.NETWORK_TYPE_CDMA:   // 2G ~ 14-64 kbps  
    		                case TelephonyManager.NETWORK_TYPE_EDGE:   // 2G ~ 50-100 kbps
    		                case TelephonyManager.NETWORK_TYPE_GPRS:   // 2G ~ 100 kbps
    		                	NT = "2G";  
    		                	break;
    		                case TelephonyManager.NETWORK_TYPE_EVDO_0: // 3G ~ 400-1000 kbps  
    		                case TelephonyManager.NETWORK_TYPE_EVDO_A: // 3G ~ 600-1400 kbps  
    		                case TelephonyManager.NETWORK_TYPE_EVDO_B: // 3G ~ 600-1400 kbps 
    		                case TelephonyManager.NETWORK_TYPE_HSDPA:  // 3G ~ 2-14 Mbps  
    		                case TelephonyManager.NETWORK_TYPE_HSPA:   // 3G ~ 700-1700 kbps 
    		                case TelephonyManager.NETWORK_TYPE_HSUPA:  // 3G ~ 1-23 Mbps 
    		                case TelephonyManager.NETWORK_TYPE_UMTS:   // 3G ~ 400-7000 kbps
    		                	NT = "3G";
    		                	break;
    		                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
    		                	NT = "NETWORK_TYPE_UNKNOWN";
    		            }
    		        } else {
    		        	switch (info.getType()) {
    		        		case ConnectivityManager.TYPE_WIFI:
    		        			NT = "WIFI";
    		        			break;
    		        		case ConnectivityManager.TYPE_MOBILE_DUN:   //��Ҫʹ����Ӫ�������ȵ�ģ�CMCC��ChinaNet��
    		        			NT = "DUN";
    		        			break;
    		        		case ConnectivityManager.TYPE_MOBILE_HIPRI:  
    		        			NT = "HIPRI";
    		        			break;
    		        		case ConnectivityManager.TYPE_MOBILE_MMS:  //����ר��
    		        			NT = "MMS";
    		        			break;
    		        		case ConnectivityManager.TYPE_MOBILE_SUPL:  //���ó���:��Ҫ�Զ��л�wap��net�����ġ���Ҫ���ֻ�����ʱAP��
    		        			NT = "SUPL";
    		        			break;
    		        		case ConnectivityManager.TYPE_WIMAX:
    		        			NT = "WIMAX";
    		        			break;
    		        	}
    		        }
    		    }
    		    
    		    if (null != NT)
    		        return NT;
    		    
    		 }
        } catch (Exception e) {
        	return "";
            //e.printStackTrace();
        }
        
		return "";   
    }
}
