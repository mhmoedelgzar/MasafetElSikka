
package com.masafalsikka.utils;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Translator {

    public interface TranslationCallback {
        void onTranslated(String translatedText);
    }

    public static void translateMessage(String text, String sourceLang, String targetLang, TranslationCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://libretranslate.com/translate");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String data = "q=" + URLEncoder.encode(text, "UTF-8") +
                              "&source=" + sourceLang +
                              "&target=" + targetLang;

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                InputStream responseStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                String translated = jsonResponse.getString("translatedText");

                callback.onTranslated(translated);
            } catch (Exception e) {
                callback.onTranslated("خطأ في الترجمة");
            }
        }).start();
    }
}
