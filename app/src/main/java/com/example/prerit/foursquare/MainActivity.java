package com.example.prerit.foursquare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
/**
 * Created by Prerit on 07-11-2015.
 */
public class MainActivity extends Activity {
    EditText query;
    String query_string;
    ArrayList<HashMap<String, String>> list;
    ListView search_list;
    ProgressDialog dialog = null;
    String latitude = null;
    String longitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search_list = (ListView) findViewById(R.id.listView1);
        query = (EditText) findViewById(R.id.search_query);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        int location_on = statusCheck();
        SharedPref shared = new SharedPref(getApplicationContext());
        HashMap<String, String> user = shared.get_saved_location();
        latitude = user.get(SharedPref.LATITUDE);
        latitude = user.get(SharedPref.LONGITUDE);
        if (location_on == 1 && latitude == null) {
            new LocationFetch().execute();
            shared.save_location(latitude, longitude);
        }

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(query.getWindowToken(), 0);
                    int location = statusCheck();
                    if (location == 1) {
                        query_string = query.getText().toString().trim();
                        try {
                            query_string = URLEncoder.encode(query_string, "UTF-8");
                        } catch (Exception e) {

                        }
                        if (latitude == null)
                            new LocationFetch().execute();
                        new ApiCall().execute();
                    }
                    return true;
                }
                return false;
            }
        });

        search_list
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        Intent intent = new Intent(MainActivity.this,
                                SearchView.class);
                        HashMap<String, String> item = list.get(position);
                        try {
                            intent.putExtra("name", item.get("name")
                                    .toString());
                        } catch (Exception e) {
                            intent.putExtra("name", "");
                        }
                        try {
                            intent.putExtra("address", item.get("address").toString());
                        } catch (Exception e) {
                            intent.putExtra("address", "");
                        }
                        try {
                            intent.putExtra("category", item.get("category").toString());
                        } catch (Exception e) {
                            intent.putExtra("category", "");
                        }
                        try {
                            intent.putExtra("usersCount", item.get("usersCount").toString());
                        } catch (Exception e) {
                            intent.putExtra("usersCount", "");
                        }
                        try {
                            intent.putExtra("tipCount", item.get("tipCount").toString());
                        } catch (Exception e) {
                            intent.putExtra("tipCount", "");
                        }
                        try {
                            intent.putExtra("checkinsCount", item.get("checkinsCount").toString());
                        } catch (Exception e) {
                            intent.putExtra("checkinsCount", "");
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
    }

    public int statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return 0;
        }
        return 1;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("FourSquare App wants to use your GPS location. Please enable it.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private class LocationFetch extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            while (latitude == null) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            Log.e("lat:", latitude + "  ,  " + longitude);
        }

        @Override
        protected void onPreExecute() {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            dialog = ProgressDialog.show(MainActivity.this, "", "Fetching your location...",
                    true);
        }
    }

    private class ApiCall extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                Keys key = new Keys();
                String client_id = key.client_id;
                String client_secret = key.client_secret;
                String URL = "https://api.foursquare.com/v2/venues/search?client_id=" + client_id + "&client_secret=" + client_secret + "&v=20130815&ll=" + latitude + "," + longitude + "&query=" + query_string + "&limit=20";
                String SetServerString = "";
                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                SetServerString = Client.execute(httpget, responseHandler);
                JSONObject obj = new JSONObject(SetServerString);
                String resp = obj.getString("response");
                JSONObject obj2 = new JSONObject(resp);
                JSONArray arr = obj2.getJSONArray("venues");
                try {
                    list = new ArrayList<HashMap<String, String>>();
                    for (int i = 0; i < arr.length(); i++) {
                        HashMap<String, String> temp = new HashMap<String, String>();
                        try {
                            String name = arr.getJSONObject(i).getString("name");
                            System.out.println(name + "\n");
                            temp.put("name", "" + arr.getJSONObject(i).getString("name")
                                    + "");
                        } catch (Exception e) {
                            Log.e("Log: ", e.toString());
                        }
                        try {
                            JSONObject objlocation = new JSONObject(arr.getJSONObject(i).getString("location"));
                            temp.put("lat", objlocation.getString("lat"));
                            temp.put("long", objlocation.getString("lng"));
                            temp.put("address", "Address : " + objlocation.getString("address"));
                        } catch (Exception e) {
                            Log.e("Log:", e.toString());
                        }
                        try {
                            JSONArray arr2 = arr.getJSONObject(i).getJSONArray("categories");
                            temp.put("category", arr2.getJSONObject(0).getString("shortName"));
                        } catch (Exception e) {
                            Log.e("Logcat: ", e.toString());
                        }

                        try {
                            JSONObject obj_stats = new JSONObject(arr.getJSONObject(i).getString("stats"));
                            temp.put("checkinsCount", obj_stats.getString("checkinsCount"));
                            temp.put("usersCount", obj_stats.getString("usersCount"));
                            temp.put("tipCount", obj_stats.getString("tipCount"));
                        } catch (Exception e) {
                            Log.e("Log:", e.toString());
                        }
                        list.add(temp);
                    }
                } catch (Exception e) {
                    Log.e("log_tag", "fata code");
                }

            } catch (Exception ex) {
                Log.e("log_etag", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            int length = list.size();
            if (length == 20)
                Toast.makeText(getApplicationContext(), "Top " + length + " results found!",
                        Toast.LENGTH_LONG).show();
            else if (length < 20)
                Toast.makeText(getApplicationContext(), length + " results found!",
                        Toast.LENGTH_LONG).show();
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, list,
                    R.layout.listview_row, new String[]{
                    "name", "category", "address"}, new int[]{
                    R.id.name, R.id.category, R.id.address}

            );
            search_list.setAdapter(adapter);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, "", "Please Wait..",
                    true);
        }
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Double longa = loc.getLongitude();
            longitude = longa.toString().trim();
            Double lati = loc.getLatitude();
            latitude = lati.toString().trim();
        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0)
                    cityName = addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            SharedPref shared = new SharedPref(getApplicationContext());
            shared.save_location(latitude, longitude);
            System.out.println(s);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}
