package com.skyblue.skybluea.channel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.skyblue.skybluea.R;

public class PageCreateBusContactActivity extends AppCompatActivity {

    EditText txtPhoneNumber , txtWebsite;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_create_bus_contact);
        txtPhoneNumber = findViewById(R.id.id_phone);
        txtWebsite = findViewById(R.id.id_website);
        nextBtn = findViewById(R.id.id_next_button);

        String name = getIntent().getStringExtra("page_name");
        String des = getIntent().getStringExtra("page_des");
        String spinner = getIntent().getStringExtra("page_spinner");

   //     Toast.makeText(PageCreateBusContactActivity.this, ""+name+des+spinner, Toast.LENGTH_SHORT).show();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String page_id = getIntent().getStringExtra("page_id");
                String name = getIntent().getStringExtra("page_name");
                String des = getIntent().getStringExtra("page_des");
                String spinner = getIntent().getStringExtra("page_spinner");

                String phone = txtPhoneNumber.getText().toString();
                String website = txtWebsite.getText().toString();

                Intent intent = new Intent(PageCreateBusContactActivity.this, PageCreateBusAddressActivity.class);
                intent.putExtra("page_name", name);
                intent.putExtra("page_des", des);
                intent.putExtra("page_spinner", spinner);

                intent.putExtra("page_phone", phone);
                intent.putExtra("page_website", website);
                intent.putExtra("page_id", page_id);
                startActivity(intent);
            }
        });

    }
}