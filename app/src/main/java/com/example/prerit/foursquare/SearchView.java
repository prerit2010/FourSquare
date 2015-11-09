package com.example.prerit.foursquare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Prerit on 07-11-2015.
 */
public class SearchView extends Activity {
    TextView name, category, address, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        name = (TextView) findViewById(R.id.name);
        category = (TextView) findViewById(R.id.category);
        address = (TextView) findViewById(R.id.address);
        title = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        String str_name, str_category, str_address;
        str_name = intent.getStringExtra("name");
        str_category = intent.getStringExtra("category");
        str_address = intent.getStringExtra("address");
        title.setText(str_name);
        name.setText(str_name);
        category.setText(str_category);
        address.setText(str_address);
    }
}
