package com.example.michaelreda.myfirstapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.michaelreda.myfirstapp.MainActivity;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;


import okio.Buffer;

public class Get_Token {
    public String token;
    private static Context context;

    protected Get_Token(Context c) {
        context=c;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://accounts.spotify.com/api/token").newBuilder();
//        urlBuilder.addQueryParameter("v", "1.0");
//        urlBuilder.addQueryParameter("user", "vogella");
                    String url = urlBuilder.build().toString();
                    String encoded_client_ID = Base64.encodeToString("d7474c1848e441f3ab9020d2736916da:cc8557a14fad43b59b028079ba7e36b7".getBytes(),Base64.DEFAULT);

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    MediaType urlencoded = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(urlencoded, "grant_type=client_credentials");
                    Request request = new Request.Builder()
//                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .header("Authorization", "Basic " + encoded_client_ID)
                            .url(url)
                            .post(body)
                            .build();
                    try {
                        final Request copy = request.newBuilder().build();
                        final Buffer buffer = new Buffer();
                        copy.body().writeTo(buffer);
                        Log.d("request_body",buffer.readUtf8());

                    } catch (final IOException e) {
                    }
                    Log.d("request",request.toString());
                    Log.d("request_headers",request.headers().toString());
//                    Log.d("request_body",body.contentType().toString());
//                    Log.d("request_body",body.toString());
                    try {
                        Response response = client.newCall(request).execute();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if (!response.isSuccessful()) {
                                    Log.d("response",response.toString());
//                                    Toast.makeText(context,"error",Toast.LENGTH_LONG);
                                    throw new IOException("Unexpected code " + response);
                                } else {
                                    token=response.body().toString();
                                    Toast.makeText(context,token,Toast.LENGTH_LONG);
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    token = "null";
                }
            }
        });
       thread.start();
    }

}