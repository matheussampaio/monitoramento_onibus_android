package com.sony.monitoramento_onibus.asynctasks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;

import com.sony.monitoramento_onibus.utils.Utils;

public class DownloadXMLTask extends AsyncTask<String, Void, String> {
    private final Context context;

    public DownloadXMLTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {
            return Utils.downloadUrl(urls[0]);
        } catch (final IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        final XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(new ByteArrayInputStream(result.getBytes("UTF-8")),
                    null);

            parser.nextTag();
            while (parser.next() != XmlPullParser.END_TAG) {
                System.out.println(parser.getName());
            }

        } catch (final UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
