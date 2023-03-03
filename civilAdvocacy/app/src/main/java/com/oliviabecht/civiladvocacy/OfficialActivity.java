package com.oliviabecht.civiladvocacy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    MainActivity mainActivity;
    private Picasso picasso;
    private ConstraintLayout cS;
    private ImageView photoImage;
    private ImageView partyImage;
    private TextView location;
    private TextView jobTitle;
    private TextView name;
    private TextView party;
    private TextView address;
    private TextView phone;
    private TextView website;
    private TextView addressTitle;
    private TextView phoneTitle;
    private TextView emailTitle;
    private TextView websiteTitle;
    private TextView email;
    private View rectangle;
    private String photoURL = "";
    private String facebook;
    private String twitter;
    private String youtube;
    private static int IMAGE_OPEN_REQUEST = 1;
    private static final String DEM_URL = "https://democrats.org";
    private static final String REP_URL = "https://www.gop.com";
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Office OfficeObj;
    //private static final String TAG = "OfficialActivity";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        Office currentOffice = (Office) getIntent().getSerializableExtra("Office");
       // Office currentOfficeList = (Office) getIntent().getSerializableExtra("existingOfficeList");
        //Office currentPosition = (Office) getIntent().getSerializableExtra("positionInOfficeList");
        //MainActivity mainLocation = (MainActivity) getIntent().getExtras("location");

        location = findViewById(R.id.OfficialUserLocation);
        //location.setText(currentOffice.);

        //TextView userAddress = (TextView) findViewById(R.id.OfficialUserLocation);


        jobTitle = findViewById(R.id.OfficialJobTitle);
        name = findViewById(R.id.OfficialName);
        party = findViewById(R.id.OfficialParty);
        address = findViewById(R.id.OfficialAddressLink);
        phone = findViewById(R.id.OfficialPhoneLink);
        website = findViewById(R.id.OfficialWebsiteLink);
        email = findViewById(R.id.OfficialEmailLink);
        cS = findViewById(R.id.OfficialCS);
        rectangle = findViewById(R.id.OfficialRectangleView);
        addressTitle = findViewById(R.id.OfficialAddressTitle);
        emailTitle = findViewById(R.id.OfficialEmailTitle);
        phoneTitle = findViewById(R.id.OfficialPhoneTitle);
        websiteTitle = findViewById(R.id.OfficialWebsiteTitle);
        picasso = Picasso.get();

        ImageView facebookImg = findViewById(R.id.OfficialFBIcon);
        ImageView twitterImg = findViewById(R.id.OfficialTwitterIcon);
        ImageView youtubeImg = findViewById(R.id.OfficialYTIcon);
        photoImage = findViewById(R.id.OfficialOfficialPhoto);
        partyImage = findViewById(R.id.OfficialPartyIcon);

        String tempUserLocation = "setting address";
        location.setText(MainActivity.locationString);


        if (currentOffice == null) {
            // TODO handle the case where the official being passed is null
            super.onBackPressed(); //TODO figure out how to go back without exiting
        }

        name.setText(currentOffice.getName());

        if (currentOffice.getJobTitle() != null) {
            jobTitle.setText(currentOffice.getJobTitle());
        } else {
            jobTitle.setText("");
            jobTitle.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getParty() != null) {
            String partyString = currentOffice.getParty();
            party.setText(partyString);
            //System.out.println(currentOffice.getParty());
            if (partyString.contains("Republican")) {
                rectangle.setBackgroundColor(Color.RED);
                Drawable Rep = getResources().getDrawable(R.drawable.rep_logo);
                partyImage.setImageDrawable(Rep);
            }
            else if (partyString.contains("Democratic")) {
                rectangle.setBackgroundColor(Color.BLUE);
                Drawable Dem = getResources().getDrawable(R.drawable.dem_logo);
                partyImage.setImageDrawable(Dem);
            }
            else {
                partyImage.setVisibility(View.INVISIBLE);
            }
        } else {
            party.setText("");
            party.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getAddress() != null) {
            address.setText(currentOffice.getAddress());
        } else {
            address.setText("");
            address.setVisibility(View.INVISIBLE);
            addressTitle.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getEmail() != null) {
            email.setText(currentOffice.getEmail());
        } else {
            email.setText("");
            email.setVisibility(View.INVISIBLE);
            emailTitle.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getPhone() != null) {
            phone.setText(currentOffice.getPhone());
        } else {
            phone.setText("");
            phone.setVisibility(View.INVISIBLE);
            phoneTitle.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getFacebook() != null) {
            //set intent/ URL
            //ON CLICK LISTENER
        } else {
            //SET URL TO NULL
            facebookImg.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getTwitter() != null) {
            //set intent/ URL
            //ON CLICK LISTENER
        } else {
            //SET URL TO NULL
            twitterImg.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getWebsite() != null) {
            website.setText(currentOffice.getWebsite());
            //set intent/ URL
            //ON CLICK LISTENER
        } else {
            website.setText("");
            website.setVisibility(View.INVISIBLE);
           websiteTitle.setVisibility(View.INVISIBLE);
        }

        if (currentOffice.getYoutube() != null) {
            //set intent/ URL
            //ON CLICK LISTENER
        } else {
            //SET URL TO NULL
            youtubeImg.setVisibility(View.INVISIBLE);
        }
        picasso.load(currentOffice.getPhoto()).placeholder(R.drawable.missing).error(R.drawable.brokenimage).resize(116, 128).centerCrop().into(photoImage);


            photoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent PhotoIntent = new Intent(v.getContext(), PhotoDetailActivity.class);
                    PhotoIntent.putExtra("Office", currentOffice);
                    v.getContext().startActivity(PhotoIntent);

                    //startActivity(new Intent(OfficialActivity.this, PhotoDetailActivity.class));
                }
            });

        facebookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentOffice.getFacebook()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(i);
            }
        });

        youtubeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentOffice.getYoutube()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(i);
            }
        });

        twitterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentOffice.getTwitter()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(i);
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri geoLocation = Uri.parse(currentOffice.getAddress());
                intent.setData(geoLocation); //lat lng or address query
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_no = currentOffice.getPhone().replaceAll("-", "");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);

            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //NOT SURE

            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentOffice.getWebsite()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(i);
            }
        });

        partyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentOffice.getParty().contains("Republican")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(REP_URL));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(i);
                }
                else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(DEM_URL));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(i);
                }

            }
        });
    }
    //OUTSIDE OF ON CREATE

    }


