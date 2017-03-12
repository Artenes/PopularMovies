package br.com.ericbraga.popularmovies.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ericbraga25.
 */

public class NetworkConnection {

    private static final String TAG = NetworkConnection.class.getSimpleName();

    private Context mContext;

    private Uri mUri;

    public NetworkConnection(Context ctx, Uri mUri) {
        mContext = ctx;
        this.mUri = mUri;
    }

    public String getResponseFromUri() throws NetWorkConnectionException {

        if (! isDeviceConnectedToInternet() ) {
            throw new NetWorkConnectionException("The device is not connected to internet, please enable it for continue");
        }

        URL url = makeUrlBasedOnUri();
        return getResponse(url);
    }

    private String getResponse(URL url) throws NetWorkConnectionException {
        StringBuilder response = new StringBuilder();

        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader reader = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
            is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ( (line = reader.readLine()) != null ) {
                response.append(line);
            }

        } catch (IOException e){
            String exceptionMessage = String.format("Could not connect with %s", url.toString());
            throw new NetWorkConnectionException(exceptionMessage);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
            }

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
            }
        }

        return response.toString();
    }

    @NonNull
    private URL makeUrlBasedOnUri() throws NetWorkConnectionException {
        URL url;
        try {
            url = new URL(mUri.toString());
        } catch (MalformedURLException e) {
            String exceptionMessage = String.format("%s not found", mUri.toString());
            throw new NetWorkConnectionException(exceptionMessage);
        }
        return url;
    }


    private boolean isDeviceConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}