package com.skyblue.skybluea.forget_password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.skyblue.skybluea.LoginActivity;
import com.skyblue.skybluea.R;

public class ForgetPasChangeSuccess extends AppCompatActivity {
    Button backBtn;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pas_change_success);
        backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
