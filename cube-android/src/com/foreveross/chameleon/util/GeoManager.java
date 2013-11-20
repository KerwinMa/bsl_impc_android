package com.foreveross.chameleon.util;

import android.app.PendingIntent;
import android.content.*;
import android.location.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.push.mina.library.util.ThreadPool;
import com.foreveross.chameleon.service.GeoService;
import com.foreveross.chameleon.service.ModuleOperationService;
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
    private boolean runFlag  = false;
    private GeoService geoService;
    private ServiceConnection geoServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            geoServiceConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            geoService = ((GeoService.GeoServiceBinder) service)
                    .getService();

        }
    };
    public GeoManager(final Context mContext) {
        this.mContext = mContext;
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.foss.geoReload");
        BroadcastReceiver receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.foss.geoReload"))
                {
                    final Context ctx = context;
                    Intent newIntent = GeoService.getIntent(ctx);
                    ctx.bindService(newIntent,geoServiceConnection,Context.BIND_AUTO_CREATE);
                }
            }


        };
        this.mContext.registerReceiver(receiver,filter);
    }

    public GeoService getGeoService() {
        return geoService;
    }

    public void setGeoService(GeoService geoService) {
        this.geoService = geoService;
    }
}

