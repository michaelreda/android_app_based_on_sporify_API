package com.example.michaelreda.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.FeaturedPlaylistsRequest;
import com.wrapper.spotify.models.FeaturedPlaylists;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimplePlaylist;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private static final String CLIENT_ID = "d7474c1848e441f3ab9020d2736916da";
    private static final String REDIRECT_URI = "yourcustomprotocol://callback";
    // Request code that will be used to verify if the result comes from correct activity
    //  Can be any integer
    private static final int REQUEST_CODE = 1337;


    public static final String EXTRA_USERNAME = "com.example.myfirstapp.USERNAME";
    public static final String EXTRA_PASSWORD = "com.example.myfirstapp.PASSWORD";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView playlists_list= (ListView) findViewById(R.id.playlists_list);
        playlists_list.setVisibility(View.INVISIBLE);
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{ "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

//    public void login(View view){
//        Intent intent = new Intent(this, HomeActivity.class);
//        EditText editText = (EditText) findViewById(R.id.username);
//        String username = editText.getText().toString();
//        intent.putExtra(EXTRA_USERNAME, username);
//        editText = (EditText) findViewById(R.id.password);
//        String password = editText.getText().toString();
//        if(password.equals("123")) {
//            intent.putExtra(EXTRA_PASSWORD, password);
//            startActivity(intent);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Toast.makeText(this,"Logged in successfully",Toast.LENGTH_LONG).show();
                    final String access_token = response.getAccessToken();
                    final Handler handler = new Handler();
                    final Context context = this;
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Api api = Api.builder().accessToken(access_token).build();

                            final FeaturedPlaylistsRequest request = api.getFeaturedPlaylists()
                                    .limit(20)
                                    .build();
                            Log.d("featured_playlists", request.toString());

                            try {
                                final FeaturedPlaylists featuredPlaylists = request.get();
                                handler.post(new Runnable(){
                                    public void run() {
                                        TextView title_textview= (TextView)findViewById(R.id.title) ;
                                        title_textview.setText(featuredPlaylists.getMessage());
                                        ListView playlists_list_view= (ListView) findViewById(R.id.playlists_list);
                                        playlists_list_view.setVisibility(View.VISIBLE);
                                        final Page<SimplePlaylist> playlists = featuredPlaylists.getPlaylists();
                                        ArrayList<String> playlists_array = new ArrayList<>();
                                        for(SimplePlaylist playlist:playlists.getItems()){
                                            playlists_array.add(playlist.getName());
//                                            Log.d("images", playlist.getImages().get(0).getUrl());
                                        }


                                        ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, playlists_array);
                                        playlists_list_view.setAdapter(adapter);

                                        playlists_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                                                Toast.makeText(getBaseContext(),adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                                                  String selected_item_name = adapterView.getItemAtPosition(position).toString();
                                                for(SimplePlaylist playlist:playlists.getItems()){
                                                    if(playlist.getName().equals(selected_item_name)){
                                                        Intent intent = new Intent(context, TracksActivity.class);
                                                        intent.putExtra("Extra_ID", playlist.getId());
                                                        intent.putExtra("Extra_owner_id", playlist.getOwner().getId());
                                                        intent.putExtra("Extra_title", playlist.getName());
                                                        intent.putExtra("access_token", access_token);
                                                        startActivity(intent);
                                                        break;
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });

                                Log.d("featured_playlists", "Message for this set of playlists: " + featuredPlaylists.getMessage());
//                                Log.d("featured_playlists", "featured playlists: " + featuredPlaylists.getPlaylists());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (WebApiException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    thread.start();

                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
}
