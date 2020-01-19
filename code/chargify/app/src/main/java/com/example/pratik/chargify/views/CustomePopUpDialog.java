package com.example.pratik.chargify.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pratik.chargify.R;
import com.example.pratik.chargify.models.ParkingSpot;
import com.google.android.gms.maps.model.LatLng;


public class CustomePopUpDialog extends Dialog {
    Context context;
    TextView typeText,occupiedText,totalSpaceText,ratingsText,phoneText;
    Button getDirectionButton;
    String type=null,occupied=null,totalSpace=null,ratings=null,phone=null;
    double latitude,longitude;

    public CustomePopUpDialog(@NonNull Context context, ParkingSpot spot) {
        super(context);
        this.context=context;
        this.type=spot.getType();
        this.occupied=String.valueOf(spot.getOccupiedSpace());
        this.totalSpace=String.valueOf(spot.getCapacity());
        this.ratings=String.valueOf(spot.getRating());
        this.phone=spot.getPhone();
        this.latitude=spot.getLatitude();
        this.longitude=spot.getLongitude();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custome_pop_up_dialog);
        typeText=findViewById(R.id.description_type);
        occupiedText=findViewById(R.id.description_occupied);
        totalSpaceText=findViewById(R.id.description_availability);
        ratingsText=findViewById(R.id.description_ratings);
        phoneText=findViewById(R.id.description_phoneno);
        getDirectionButton=findViewById(R.id.getDirectionsButton);
        if(type!=null&&occupied!=null&&totalSpace!=null&&ratings!=null&&phone!=null)
        {
         typeText.setText(type);
         occupiedText.setText(occupied);
         totalSpaceText.setText(totalSpace);
         ratingsText.setText(ratings);
         phoneText.setText(phone);
        }
        else
        {
            Toast.makeText(context,"Information Not Available",Toast.LENGTH_LONG).show();
        }
        super.setCancelable(true);



        getDirectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Getting Direction, Wait...",Toast.LENGTH_LONG).show();
                getDirectionButtonClicked(new LatLng(latitude,longitude));
                closeDialog();
            }
        });

    }


    public void getDirectionButtonClicked(LatLng latLng)
    {

    }

    public void closeDialog()
    {
        this.dismiss();
    }


}
