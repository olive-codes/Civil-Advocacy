package com.oliviabecht.civiladvocacy;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    private Picasso picasso;
    private ConstraintLayout cS;
    private ImageView photoImage;
    private ImageView partyImage;
    private TextView location;
    private TextView jobTitle;
    private TextView name;
    private String photoURL = "";
    private static int IMAGE_OPEN_REQUEST = 1;
    private View rectangle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        Office currentOffice = (Office) getIntent().getSerializableExtra("Office");

        location = findViewById(R.id.PhotoDetailUserLocation);
        jobTitle = findViewById(R.id.PhotoDetailJobTitle);
        name = findViewById(R.id.PhotoDetailName);
        photoImage = findViewById(R.id.PhotoDetailOfficialPhoto);
        partyImage = findViewById(R.id.PhotoDetailPartyIcon);
        rectangle = findViewById(R.id.PhotoDetailRectangleView);

        picasso = Picasso.get();

        location.setText(MainActivity.locationString);
        name.setText(currentOffice.getName());
        jobTitle.setText(currentOffice.getJobTitle());

        if (currentOffice.getParty() != null) {
            if (currentOffice.getParty().contains("Republican")) {
                rectangle.setBackgroundColor(Color.RED);
                Drawable Rep = getResources().getDrawable(R.drawable.rep_logo);
                partyImage.setImageDrawable(Rep);
            }
            else if (currentOffice.getParty().contains("Democratic")) {
                rectangle.setBackgroundColor(Color.BLUE);
                Drawable Dem = getResources().getDrawable(R.drawable.dem_logo);
                partyImage.setImageDrawable(Dem);
            }
            else {
                partyImage.setVisibility(View.INVISIBLE);
            }
        } else {
            partyImage.setVisibility(View.INVISIBLE);
        }

        picasso.load(currentOffice.getPhoto()).placeholder(R.drawable.missing).error(R.drawable.brokenimage).resize(116, 128).centerCrop().into(photoImage);





    }




}
