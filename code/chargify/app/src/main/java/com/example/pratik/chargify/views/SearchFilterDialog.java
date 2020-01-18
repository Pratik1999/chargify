package com.example.pratik.chargify.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.pratik.chargify.R;

/**
 * Created by pratik on 12/1/19.
 */

public class SearchFilterDialog extends Dialog {
    Context context;
    Button applySearchFilterButton;
    CheckBox slowBox,fastBox,rapidBox;
    EditText radiusBox;

    boolean slow,fast, rapid;
    double radius;
    String type=null,occupied=null,totalSpace=null,ratings=null,phone=null;
    double latitude,longitude;
    public SearchFilterDialog(@NonNull Context context,boolean free,boolean assured, boolean miniSpace,double radius) {
        super(context);
        this.slow=free;
        this.fast=assured;
        this.rapid=miniSpace;
        this.radius=radius;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_filter_dialog_layout);
        super.setCancelable(true);
        applySearchFilterButton=findViewById(R.id.applySearchFilterButton);
        slowBox=findViewById(R.id.filterSlowCheckBox);
        fastBox=findViewById(R.id.filterFastCheckBox);
        rapidBox=findViewById(R.id.filterRapidCheckBox);
        radiusBox=findViewById(R.id.radiusBox);

        radiusBox.setText(String.valueOf(radius));
        slowBox.setChecked(slow);
        fastBox.setChecked(fast);
        rapidBox.setChecked(rapid);

        applySearchFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFilter(slowBox.isChecked(),fastBox.isChecked(),rapidBox.isChecked(),Double.valueOf(radiusBox.getText().toString()));
                closeDialog();
            }
        });
    }




    public void closeDialog()
    {
        this.dismiss();
    }

    public void applyFilter(boolean slow,boolean fast, boolean rapid,double radius)
    {

    }


}
