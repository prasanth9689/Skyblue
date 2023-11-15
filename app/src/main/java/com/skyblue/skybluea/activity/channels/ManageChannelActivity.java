package com.skyblue.skybluea.activity.channels;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.activity.HomeActivity2;
import com.skyblue.skybluea.databinding.ActivityManageChannelBinding;

public class ManageChannelActivity extends AppCompatActivity {
    private ActivityManageChannelBinding binding;
    private final Context context = this;
    private Dialog channelRenameDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManageChannelBinding inflate = ActivityManageChannelBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView((View) inflate.getRoot());

        initOnClick();
        initChannelRenameDialog();
    }

    private void initChannelRenameDialog() {
        channelRenameDialog = new Dialog(ManageChannelActivity.this);
        channelRenameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        channelRenameDialog.setContentView(R.layout.model_primary_channel_rename);
        channelRenameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        channelRenameDialog.setCancelable(false);

        RelativeLayout close = channelRenameDialog.findViewById(R.id.close);
        Button saveBtn = channelRenameDialog.findViewById(R.id.save_button);
        EditText nameEditText = channelRenameDialog.findViewById(R.id.name_edit_text);

        saveBtn.setOnClickListener(v -> {

            String mChannealName = nameEditText.getText().toString().trim();

            if ("". equals(mChannealName)){
                nameEditText.setError(getString(R.string.enter_channel_name));
                nameEditText.requestFocus();
                return;
            }
            Log.e("channel_", "valid channel name");
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelRenameDialog.dismiss();
            }
        });
    }

    private void initDialog() {

    }

    private void initOnClick() {
        binding.changeChannelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelRenameDialog.show();
            }
        });
        binding.deleteChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.back.setOnClickListener(v -> finish());
    }
}
