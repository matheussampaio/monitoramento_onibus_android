package com.sony.monitoramento_onibus.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.sony.monitoramento_onibus.R;

public class Utils {
    static final String URL_WEB = "192.168.1.244:4567";
    static final String URL_GEOSERVER = "192.168.1.244:4568";

    private static int[] COLORS = { 0xff0000ff, 0xff00ff00, 0xffff0000,
            0xff00ffff, 0xffffff00, 0xffff00ff };

    private static ArrayList<String> rotasColors = new ArrayList<String>();

    // private static int nextColor = 0;

    public static String getURL() {
        return URL_WEB;
    }

    public static String getURLGeoserver() {
        return URL_GEOSERVER;
    }

    public static boolean isConnected(Activity act) {
        final ConnectivityManager connMgr = (ConnectivityManager) act
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if ((networkInfo != null) && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }

    public static int getColor(String nome) {

        if (!rotasColors.contains(nome)) {
            rotasColors.add(nome);
        }

        final int nextColor = rotasColors.indexOf(nome);

        // nextColor++;
        //
        // if (nextColor >= COLORS.length) {
        // nextColor = 0;
        // }

        return COLORS[nextColor];

    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    public static String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            final URL url = new URL(myurl);
            final HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();

            final BufferedReader br = new BufferedReader(new InputStreamReader(
                    is));

            String contentAsString = "";
            String line;

            while ((line = br.readLine()) != null) {
                contentAsString += line;
            }

            return contentAsString;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static String sendPOST(String myurl, String paramns)
            throws IOException {

        final URL url = new URL(myurl);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        final OutputStreamWriter osw = new OutputStreamWriter(
                conn.getOutputStream());
        osw.write(paramns);
        osw.flush();

        final BufferedReader br = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));

        String contentAsString = "";
        String line;

        while ((line = br.readLine()) != null) {
            contentAsString += line;
        }

        osw.close();
        br.close();

        return contentAsString;
    }

    public static void showNotConnected(Context context) {
        Toast.makeText(context,
                context.getString(R.string.conexao_indisponivel),
                Toast.LENGTH_LONG).show();
    }

    public static void showCantWithoutConnection(Context context) {
        Toast.makeText(context,
                context.getString(R.string.cant_without_connection),
                Toast.LENGTH_SHORT).show();
    }
}
