package com.example.michaelreda.myfirstapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.FeaturedPlaylistsRequest;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.models.FeaturedPlaylists;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TracksActivity extends AppCompatActivity {
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        Intent intent = getIntent();
//        final Context context = this;
       final String id = intent.getStringExtra("Extra_ID");
       final String  access_token = intent.getStringExtra("access_token");
        final String  owner_id = intent.getStringExtra("Extra_owner_id");
        final String  title= intent.getStringExtra("Extra_title");
       final TextView title_textview = (TextView) findViewById(R.id.title_textView);
        title_textview.setText(title);
        final ProgressDialog loading_dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        final android.os.Handler handler = new android.os.Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Api api = Api.builder().accessToken(access_token).build();
                final PlaylistRequest request = api.getPlaylist(owner_id, id).build();

                try {
                    final Playlist playlist = request.get();
                    loading_dialog.dismiss();
                    Log.d("playlist","Retrieved playlist " + playlist.getName());
                    handler.post(new Runnable() {
                        public void run() {
                            ListView tracks_list_view= (ListView) findViewById(R.id.tracks_list);
                            tracks_list_view.setVisibility(View.VISIBLE);
                            ArrayList<String> tracks_list = new ArrayList<>();
                            for(PlaylistTrack track: playlist.getTracks().getItems()){
                                tracks_list.add(track.getTrack().getName());
                            }
                            ArrayAdapter adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, tracks_list);
                            tracks_list_view.setAdapter(adapter);

                            tracks_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                                                Toast.makeText(getBaseContext(),adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                                    String selected_item_name = adapterView.getItemAtPosition(position).toString();
                                    for(PlaylistTrack track: playlist.getTracks().getItems()){
                                        if(track.getTrack().getName().equals(selected_item_name)){
                                            get_track(track.getTrack().getId());
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    });


                } catch (Exception e) {
                    System.out.println("Something went wrong!" + e.getMessage());
                }


            }
        });
        thread.start();


    }

    public void get_track(final String id){
        Intent intent = getIntent();
        final ProgressDialog loading_dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        final String  access_token = intent.getStringExtra("access_token");
        final Api api = Api.builder().accessToken(access_token).build();
        final android.os.Handler handler = new android.os.Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final TrackRequest request = api.getTrack(id).build();

                try {
                    final Track requested_track = request.get();
                    loading_dialog.dismiss();
                    Log.d("requested track","Retrieved track " + requested_track.getName());
                    Log.d("requested track","Its popularity is " + requested_track.getPopularity());
                    handler.post(new Runnable() {
                        public void run() {
                            if (requested_track.isExplicit()) {
                                Toast.makeText(context,"This track is explicit!",Toast.LENGTH_SHORT);
                            } else {
                                Toast.makeText(context,"It's OK, this track isn't explicit.",Toast.LENGTH_SHORT);
                                play_track(requested_track);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }



    Boolean isPLAYING = false;
    MediaPlayer mp;
    public void play_track(Track track) {
        if (!isPLAYING) {
            isPLAYING = true;
            mp = new MediaPlayer();
            try {
                mp.setDataSource(track.getPreviewUrl());
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                Log.e("playing track", "prepare() failed "+e.getMessage());
            }
        } else {
            isPLAYING = false;
            stopPlaying();
        }
    }

    private void stopPlaying() {

        mp.release();
        mp = null;
    }
}
