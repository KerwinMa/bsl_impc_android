package com.foreveross.chameleon.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.push.mina.library.util.ThreadPool;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by apple on 13-11-7.
 */
public class GeoManager {

    private Context mContext;

    public GeoManager(Context mContext) {
        this.mContext = mContext;
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.foss.geoReload");
        BroadcastReceiver receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.foss.geoReload"))
                {
                    final Context ctx = context;
                    ThreadPool.run(new Runnable() {
                        @Override
                        public void run() {
                            // 获取位置管理服务

                            String serviceName = Context.LOCATION_SERVICE;
                            final LocationManager locationManager = (LocationManager) ctx.getSystemService(serviceName);
                            // 查找到服务信息
                            Criteria criteria = new Criteria();
                            criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
                            criteria.setAltitudeRequired(false);
                            criteria.setBearingRequired(false);
                            criteria.setCostAllowed(true);
                            criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
                            String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
                            Looper.prepare();
                            locationManager.requestLocationUpdates(provider, 30 * 1000, 0,
                                    new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            if(location != null)
                                            {
                                                double longitude = location.getLongitude();
                                                double latitude = location.getLatitude();
                                                Application application = Application.class.cast(ctx);
                                                String sessionKey = Preferences.getSESSION(Application.sharePref);
                                                String deviceId = DeviceInfoUtil.getDeviceId(application);
                                                JSONObject json = new JSONObject();
                                                try {
                                                    json.put("deviceId",deviceId);
                                                    JSONArray tmpArray = new JSONArray();
                                                    tmpArray.put(0,longitude);
                                                    tmpArray.put(1,latitude);
                                                    json.put("position",tmpArray);
                                                    HttpPost post = new HttpPost(URL.GEOPOSITION_URL+"?sessionKey="+sessionKey);
                                                    post.addHeader("Accept", "application/json");
                                                    post.addHeader("Content-Type", "application/json");
                                                    post.setEntity(new StringEntity(json.toString(),"utf-8"));
                                                    HttpClient client = new DefaultHttpClient();
                                                    HttpResponse response = client.execute(post);
                                                    if(response.getStatusLine().getStatusCode() == 200)
                                                    {
                                                        Log.v("GEO_SUCCESS_TAG", json.toString());
                                                        if (!GeolocationUtil.isGPSON)
                                                        {
                                                            Intent GPSIntent = new Intent();
                                                            GPSIntent.setClassName("com.android.settings",
                                                                    "com.android.settings.widget.SettingsAppWidgetProvider");
                                                            GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
                                                            GPSIntent.setData(Uri.parse("custom:3"));
                                                            try {
                                                                PendingIntent.getBroadcast(ctx, 0, GPSIntent, 0).send();
                                                            } catch (PendingIntent.CanceledException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                    }
                                                    else
                                                    {
                                                        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                                                        String line="";
                                                        StringBuffer stringBuffer = new StringBuffer();
                                                        while((line= br.readLine())!= null)
                                                        {
                                                            stringBuffer.append(line);
                                                        }
                                                        Log.e("GEO_FAILD_PARAMS_TAG",json.toString());
                                                        Log.e("GEO_URL",URL.GEOPOSITION_URL+"?sessionKey="+sessionKey);
                                                        Log.e("GEO_FAILD_TAG",stringBuffer.toString());
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                } catch (ClientProtocolException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onStatusChanged(String s, int i, Bundle bundle) {

                                        }

                                        @Override
                                        public void onProviderEnabled(String s) {

                                        }

                                        @Override
                                        public void onProviderDisabled(String s) {

                                        }
                                    });
                            locationManager.getLastKnownLocation(provider); // 通过GPS获取位置


                        }
                    });
                }
            }
        };
        mContext.registerReceiver(receiver,filter);
    }


}

