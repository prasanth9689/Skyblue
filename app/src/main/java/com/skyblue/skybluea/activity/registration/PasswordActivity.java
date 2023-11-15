package com.skyblue.skybluea.activity.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityPasswordBinding;
import com.skyblue.skybluea.databinding.ActivityVerifyPhoneBinding;
import com.skyblue.skybluea.helper.Utils;

public class PasswordActivity extends AppCompatActivity {
    private ActivityPasswordBinding binding;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordBinding.inflate(getLayoutInflater());
        View view  = binding.getRoot();
        setContentView(view);

        binding.nextBtn.setOnClickListener(view1 -> {

            String password =binding.editTextPass1.getText().toString().trim();
            String confirmpassword = binding.editTextPass2.getText().toString().trim();

            if (password.isEmpty() || password.length() < 1) {
                binding.editTextPass1.setError(getResources().getString(R.string.valid_password_is_required));
                binding.editTextPass1.requestFocus();
                return;
            }

            if (confirmpassword.isEmpty() || confirmpassword.length() < 1) {
                binding.editTextPass2.setError(getResources().getString(R.string.valid_confirm_password_is_required));
                binding.editTextPass2.requestFocus();
                return;
            }
            CheckPasswordConfirmPasswordSame();
        });
    }

    private void CheckPasswordConfirmPasswordSame() {
        String password =binding.editTextPass1.getText().toString();
        String confirmpassword = binding.editTextPass2.getText().toString();

        if (password.equals(confirmpassword))
        {
            Intent intent = new Intent(PasswordActivity.this, NameActivity.class);
            intent.putExtra("password",password);
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
