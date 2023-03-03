package com.oliviabecht.civiladvocacy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.location.Address;

import androidx.annotation.NonNull;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recView;
    private Adapter adp;
    ArrayList<Office> officeList = new ArrayList<>();
    private static final String TAG = "MainActivity";

    TextView UserAddress;
    String locationToPass;
    String zipCode;
    String City;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public static String locationString = "Unspecified Location";
    private String govAddress;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LOCATION CODE BASED OFF PROFESSOR CODE EXAMPLE
        UserAddress = findViewById(R.id.MainUserLocation);
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        determineLocation();


// GO TO ABOUT PAGE USING BUTTON
        ImageButton aboutPageButton = (ImageButton) findViewById(R.id.MainAboutButton);
        aboutPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        //UPDATE USER LOCATION VIA DIALOG BOX
        ImageButton locationButton = (ImageButton) findViewById(R.id.MainLocationSearchButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, AboutActivity.class));
                if (checkAirplaneMode() == true) {
                    enterAddressDialog();
                } else {
                    noNetworkDialog();
                }
            }
        });


        //DUMMY DATA
        //Office dummy1 = new Office("First M. Last", "U.S.Representative", "(Democratic Party)", "address", "000-000-0000", "email@email.com", "web.com");
        //Office dummy2 = new Office("blah", "blah", "blah", "blah", "blah", "blah", "blah");

        //officeList.add(dummy2);
        //officeList.add(dummy1);

        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                System.out.println("URI failing: " + uri.toString());
                exception.printStackTrace();
            }
        });
        Picasso picassoBuild = builder.build();

        //SETUP ADAPTER AND RECYCLER VIEW
        recView = findViewById(R.id.MainRecView);
        adp = new Adapter(officeList, this, picassoBuild);
        recView.setAdapter(adp);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recView.setLayoutManager(layoutManager);

    }
    //OUTSIDE OF ONCREATE METHOD


    //LOCATION STUFF
    public void determineLocation() {
        // Check perm - if not then start the  request and return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        locationString = getPlace(location);
                        zipCode = getZipCode(location);
                        govAddress = locationString;
                        UserAddress.setText(locationString);
                        //UserAddress.setText(locationString);

                        //TODO zipCode variable
                        //DOWNLOADER
                        Downloader Downloader = new Downloader(this, zipCode);
                        new Thread(Downloader).start();

                    }
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    UserAddress.setText("Bad text");
                    //textView.setText(R.string.deniedText);
                }
            }
        }
    }

    public String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            zipCode = addresses.get(0).getPostalCode();
            String line = addresses.get(0).getAddressLine(0);
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s",
                    line));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String getZipCode(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            zipCode = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipCode;
    }

    //END OF LOCATION STUFF


    //CHECK AIRPLANE MODE STATUS
    public boolean checkAirplaneMode() {
        if (Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0) {
            return true;
        } else {
            return false;
        }
    }

    //ERROR DIALOG WHEN ON AIRPLANE MODE
    public void noNetworkDialog() {
        //TODO MAYBE REPLACE WITH CUSTOM DIALOG
        //No Network
        //if there is no network connection aka airplane
        //error dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without an internet connection.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
            //Stay in main activity
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void addOfficialData(Office newOfficeF) {
        if (newOfficeF == null) {
            //Log.d(TAG, "addOffice: New Office is NULL");
        }
        officeList.add(newOfficeF);
        //officialList.add(newOfficeF);
        adp.notifyDataSetChanged();
        //officeAdapter.notifyDataSetChanged();
    }


    //DIALOG FOR ENTERING A DIFFERENT ADDRESS
    public void enterAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter Address");
        // Set up the input
        final EditText inputAddress = new EditText(MainActivity.this);
        builder.setView(inputAddress);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {

            setLocation(inputAddress.getText().toString());
            locationString = inputAddress.getText().toString();

        });
        builder.setNegativeButton("CANCEL", (DialogInterface.OnClickListener) (dialog, which) -> {
            //Click Cancel - Negative -> do nothing
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void displayAddress(List<Address> addresses) {
        if (addresses.size() == 0) {
            ((TextView) findViewById(R.id.MainUserLocation)).setText("No Data For Location333");
            return;
        }
        Address one = addresses.get(0);
        // check if it's null or not
        String city = one.getLocality() == null ? "" : one.getLocality();
        String postalCode = one.getPostalCode() == null ? "" : one.getPostalCode();
        String state = one.getAdminArea() == null ? "" : one.getAdminArea();
        govAddress = city + ", " + state + " " + postalCode;
        zipCode = postalCode;
        ((TextView) findViewById(R.id.MainUserLocation)).setText(city + ", " + state + " " + postalCode);
    }

    //called from man location dialog box
    private void setLocation(String input) {
        if (geocoder == null)
            //using geocoder...
            geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            //if user did not enter anything
            if (input.trim().isEmpty()) {
                //do nothing and stay in main activity...
                return;
            }
            //find and display input
            addresses = geocoder.getFromLocationName(input, 5);
            displayAddress(addresses);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Download for new area set
        Downloader downloader = new Downloader(this, input);
        //If list of official curr exsists clear the main act and re download for new man area
        officeList.clear();
        //new thread to handle timeout
        new Thread(downloader).start();
    }


}
