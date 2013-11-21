package com.foreveross.chameleon.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class GeolocationUtil {

    public  static boolean isGPSON = false;
	/**
	 * 是否开启gps定位
	 * @param context
	 * @return
	 */
	public static boolean isOpenGPSSettings(Context context) {
		LocationManager alm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

			return true;
		}
		return false;

	}
	/**
	 * 获取位置
	 * @param context
	 * @return
	 */
	public static Location getLocation(Context context)
    {
        // 获取位置管理服务
        String serviceName = Context.LOCATION_SERVICE;
        final LocationManager locationManager = (LocationManager) context.getSystemService(serviceName);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        return location;
    }
	
	private static List<CellInfo> getCellInfo(Context context)
	{
		/** 调用API获取基站信息 */
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//获取网络类型
		int type=manager.getPhoneType();
		int countryCode;  
        int networkCode;  
        int areaCode;
        List<CellInfo> cells = new ArrayList<CellInfo>();
		if(type == TelephonyManager.PHONE_TYPE_GSM)
		{
			GsmCellLocation gsm = (GsmCellLocation) manager.getCellLocation();
			if (gsm == null) {  
                return null;  
            }  
            if (manager.getNetworkOperator() == null  
                    || manager.getNetworkOperator().length() == 0) {  
                return null;  
            } 
            countryCode = Integer.parseInt(manager.getNetworkOperator()  
                    .substring(0, 3));  
            networkCode = Integer.parseInt(manager.getNetworkOperator()  
                    .substring(3, 5));  
            areaCode = gsm.getLac(); 
            GeolocationUtil.CellInfo info = new CellInfo(); 
            info.cellId = gsm.getCid();  
            info.mobileCountryCode = countryCode;  
            info.mobileNetworkCode = networkCode;  
            info.locationAreaCode = areaCode;  
            info.radio_type = "gsm";
            cells.add(info);
            List<NeighboringCellInfo> list = manager.getNeighboringCellInfo();  
            for (NeighboringCellInfo i : list) {  
                CellInfo ci = new CellInfo();  
                ci.cellId = i.getCid();  
                ci.mobileCountryCode = countryCode;  
                ci.mobileNetworkCode = networkCode;  
                ci.locationAreaCode = areaCode;  
                ci.radio_type = "gsm";  
                cells.add(ci);  
            }  
		}
		else if(type == TelephonyManager.PHONE_TYPE_CDMA)
		{
			CdmaCellLocation cdma = (CdmaCellLocation) manager  
                    .getCellLocation();  
            if (cdma == null) {  
                return null;  
            }  
            if (manager.getNetworkOperator() == null  
                    || manager.getNetworkOperator().length() == 0) {  
                return null;  
            }  
            Log.v("TAG", "CDMA");
            CellInfo info = new CellInfo(); 
            info.cellId = cdma.getBaseStationId();  
            info.mobileCountryCode = Integer.parseInt(manager  
                    .getNetworkOperator());  
            info.mobileNetworkCode = cdma.getSystemId();  
            info.locationAreaCode = cdma.getNetworkId();  
            info.radio_type = "cdma";  
            cells.add(info);
		}
		return cells;
		
	}
	
	public static Location getBaseStationLocation(Context context) { 
		List<CellInfo> cellID = getCellInfo(context);
        if (cellID == null) {  
            Log.i("TAG", "cellId is null.");  
            return null;  
        }  
        DefaultHttpClient client = new DefaultHttpClient();  
        HttpPost post = new HttpPost("http://www.minigps.net/minigps/map/google/location");  
        JSONObject holder = new JSONObject();  
        try {  
            CellInfo info = cellID.get(0);  
            holder.put("version", "1.1.0");  
            holder.put("host", "www.minigps.net");  
            holder.put("home_mobile_country_code", info.mobileCountryCode);  
            holder.put("home_mobile_network_code", info.mobileNetworkCode);  
            holder.put("request_address", true);  
            holder.put("radio_type", info.radio_type);  
            if (460 == info.mobileCountryCode) {
                holder.put("address_language", "zh_CN");  
            } else {  
                holder.put("address_language", "en_US");  
            }  
  
            JSONObject data, current_data;  
            JSONArray array = new JSONArray();  
  
            current_data = new JSONObject();  
            current_data.put("cell_id", info.cellId);  
            current_data.put("location_area_code", info.locationAreaCode);  
            current_data.put("mobile_country_code", info.mobileCountryCode);  
            current_data.put("mobile_network_code", info.mobileNetworkCode);  
            current_data.put("age", 0);  
            array.put(current_data);  
  
            if (cellID.size() > 2) {  
                for (int i = 1; i < cellID.size(); i++) {  
                    data = new JSONObject();  
                    data.put("cell_id", info.cellId);  
                    data.put("location_area_code", info.locationAreaCode);  
                    data.put("mobile_country_code", info.mobileCountryCode);  
                    data.put("mobile_network_code", info.mobileNetworkCode);  
                    data.put("age", 0);  
                    array.put(data);  
                }  
            }  
            holder.put("cell_towers", array);  
  
            StringEntity se = new StringEntity(holder.toString());  
            post.setEntity(se);  
            HttpResponse resp = client.execute(post);  
            int state = resp.getStatusLine().getStatusCode();  
            if (state == HttpStatus.SC_OK) {  
                HttpEntity entity = resp.getEntity();  
                if (entity != null) {  
                    BufferedReader br = new BufferedReader(  
                            new InputStreamReader(entity.getContent()));  
                    StringBuffer sb = new StringBuffer();  
                    String resute = "";  
                    while ((resute = br.readLine()) != null) {  
                        sb.append(resute);  
                    }  
                    br.close();  
  
                    data = new JSONObject(sb.toString());  
                    data = (JSONObject) data.get("location");  
  
                    Location loc = new Location(  
                            android.location.LocationManager.NETWORK_PROVIDER);  
                    loc.setLatitude((Double) data.get("latitude"));  
                    loc.setLongitude((Double) data.get("longitude"));  
                    loc.setAccuracy(Float.parseFloat(data.get("accuracy")  
                            .toString()));  
                    loc.setTime(System.currentTimeMillis());  
                    return loc;  
                } else {  
                    return null;  
                }  
            } else {  
                Log.v("TAG", state + "");  
                return null;  
            }  
  
        } catch (Exception e) {  
            Log.e("TAG", e.getMessage());  
            return null;  
        }  
    }  
	
	public static Location getNewLocation(Context context)
	{
		if(isOpenGPSSettings(context))
		{
			return getLocation(context);
		}
		return getBaseStationLocation(context);
	}
	
	
	static class CellInfo {  
		  
        // 基站编号  
        public int cellId;  
        // 国家代码  
        public int mobileCountryCode;  
        // 网络代码  
        public int mobileNetworkCode;  
        // 区域代码  
        public int locationAreaCode;  
  
        public String radio_type;  
  
        public CellInfo() {  
            super();  
        }
         
    }  
   
}
