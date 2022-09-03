package com.skyblue.skybluea.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.Utils;

public class PasswordActivity extends AppCompatActivity {

    EditText Password, ConfirmPassword;
    Button ButtonNext;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_password);
        Password = findViewById(R.id.editTextPass1);
        ConfirmPassword = findViewById(R.id.editTextPass2);
        ButtonNext = findViewById(R.id.buttonPassNext);

        ButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password =Password.getText().toString().trim();
                String confirmpassword = ConfirmPassword.getText().toString().trim();

                if (password.isEmpty() || password.length() < 1) {
                    Password.setError(getResources().getString(R.string.valid_password_is_required));
                    Password.requestFocus();
                    return;
                }

                if (confirmpassword.isEmpty() || confirmpassword.length() < 1) {
                    ConfirmPassword.setError(getResources().getString(R.string.valid_confirm_password_is_required));
                    ConfirmPassword.requestFocus();
                    return;
                }
                CheckPasswordConfirmPasswordSame();
            }
        });
    }

    private void CheckPasswordConfirmPasswordSame() {
        String password =Password.getText().toString().trim();
        String confirmpassword = ConfirmPassword.getText().toString().trim();

        if (password.equals(confirmpassword))
        {
            Intent intent = new Intent(PasswordActivity.this, NameActivity.class);
            intent.putExtra("passwordkey",password);
            intent.putExtra("confirmpasswordkey",confirmpassword);
            intent.putExtra("phonenumber", getIntent().getStringExtra("phonenumber"));
            intent.putExtra("phonenumberonly", getIntent().getStringExtra("phonenumberonly"));
            intent.putExtra("countryname", getIntent().getStringExtra("countryname"));
            intent.putExtra("countrynamecode", getIntent().getStringExtra("countrynamecode"));
            intent.putExtra("phonecode", getIntent().getStringExtra("phonecode"));
            startActivity(intent);
        }else
        {
            Utils.showMessage(context, getResources().getString(R.string.password_and_confirm_password_dose_not_match));
        }
    }

    // Disable back press button
    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
}
