package com.myapp.mycompass;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.myapp.mycompass.Adapter.PlaceAutoCompleteAdapter;



public class LandingActivity extends AppCompatActivity {

    TextView tvLocationIcon;

    EditText edLocationName;

    Typeface myFont;

    Button btnFindInMap, btnSearchFromMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        myFont = Typeface.createFromAsset(this.getAssets(), "fontawesome-webfont.ttf");

        tvLocationIcon = (TextView)findViewById(R.id.tv_loc_icon);
        tvLocationIcon.setTypeface(myFont);

        edLocationName = (EditText) findViewById(R.id.ed_loc_name);

        btnFindInMap = (Button) findViewById(R.id.btn_find);
        btnSearchFromMap = (Button) findViewById(R.id.btn_search);

        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        //autocompleteView.setAdapter(new PlaceAutoCompleteA(getActivity(), R.layout.autocomplete_list_item));
        autocompleteView.setAdapter(new PlaceAutoCompleteAdapter(this,R.layout.autocomplete_list_item));


        btnFindInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edLocationName.getText().toString().equals(""))
                    edLocationName.setError("You must enter a location");
                else{
                    Intent intent = new Intent(LandingActivity.this, MapsActivity.class);
                    intent.putExtra("flag","yesInput");
                    startActivity(intent);
                }
            }

        });

        btnSearchFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(LandingActivity.this, TestActivity.class);
                Intent intent = new Intent(LandingActivity.this, MapsActivity.class);
                intent.putExtra("flag","noInput");
                startActivity(intent);
            }
        });

    }


}

