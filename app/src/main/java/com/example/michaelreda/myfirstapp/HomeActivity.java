package com.example.michaelreda.myfirstapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.EXTRA_USERNAME);

        // Capture the layout's TextView and set the string as its text
//        TextView textView = (TextView) findViewById(R.id.username);
//        textView.setText("hello "+username);

        ListView listview= (ListView) findViewById(R.id.tracks_list);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mobileArray);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getBaseContext(),adapterView.getItemAtPosition(position)+" is selected",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
