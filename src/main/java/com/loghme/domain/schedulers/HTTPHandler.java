package com.loghme.domain.schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class HTTPHandler {
    public static String getUrlBody(String url) throws Exception {
        URL urlObj = new URL(url);
        Charset charset = Charset.forName("UTF8");
        URLConnection urlConnection = urlObj.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        urlConnection.getInputStream(), charset));
        String body = "", inputLine = "";

        while ((inputLine = in.readLine()) != null)
            body += inputLine;
        in.close();
        return body;
    }
}
