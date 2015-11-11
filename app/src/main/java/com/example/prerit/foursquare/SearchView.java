package com.example.prerit.foursquare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Prerit on 07-11-2015.
 */
public class SearchView extends Activity {
    TextView name, category, address, title, usercount, tipcount, checkins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        name = (TextView) findViewById(R.id.name);
        category = (TextView) findViewById(R.id.category);
        address = (TextView) findViewById(R.id.address);
        tipcount = (TextView) findViewById(R.id.tipcount);
        usercount = (TextView) findViewById(R.id.usercount);
        checkins = (TextView) findViewById(R.id.checkins);
        title = (TextView) findViewById(R.id.title);

        Intent intent = getIntent();
        title.setText(intent.getStringExtra("name"));
        name.setText(intent.getStringExtra("name"));
        category.setText(intent.getStringExtra("category"));
        address.setText(intent.getStringExtra("address"));
        tipcount.setText("Tips : "+ intent.getStringExtra("tipCount"));
        usercount.setText("Users : " + intent.getStringExtra("usersCount"));
        checkins.setText("Checkins : " + intent.getStringExtra("checkinsCount"));
    }
}
