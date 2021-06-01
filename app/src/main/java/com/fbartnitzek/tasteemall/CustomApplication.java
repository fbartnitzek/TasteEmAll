package com.fbartnitzek.tasteemall;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Copyright 2016.  Frank Bartnitzek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class CustomApplication extends Application implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // todo: use other googleApiClient

    private static GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = CustomApplication.class.getName();


    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base); //enable multidex on v4.3 devices - part 3
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        connectGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {
        Log.v(LOG_TAG, "buildGoogleApiClient, hashCode=" + this.hashCode() + ", " + "");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void connectGoogleApiClient() {
        Log.v(LOG_TAG, "connectGoogleApiClient, hashCode=" + this.hashCode() + ", " + "");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public static boolean isGoogleApiClientConnected() {
        if (mGoogleApiClient != null) {
            return mGoogleApiClient.isConnected();
        } else {
            return false;
        }
    }

    public static synchronized GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(LOG_TAG, "onConnected, hashCode=" + this.hashCode() + ", " + "bundle = [" + bundle + "]");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOG_TAG, "onConnectionSuspended, hashCode=" + this.hashCode() + ", " + "i = [" + i + "]");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(LOG_TAG, "onConnectionFailed, hashCode=" + this.hashCode() + ", " + "connectionResult = [" + connectionResult + "]");
    }
}
