package com.skyblue.skybluea.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.skyblue.skybluea.R;

public class Utils {
    public static void showMessage(Context context,String message){
//
//        final Dialog dialog = new Dialog(context);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.model_dialog_message);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(false);
//
//        Button backBtn = dialog.findViewById(R.id.close);
//        TextView messageText = dialog.findViewById(R.id.message_text);
//        messageText.setEnabled(true);
//        backBtn.setEnabled(true);
//
//        dialog.show();
//
//        messageText.setText(message);
//
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
    }

    public static void showMessageInSnackbar(Context context, String message) {
        Snackbar snack = Snackbar.make(
                (((Activity) context).findViewById(android.R.id.content)),
                message, Snackbar.LENGTH_SHORT);
        snack.setDuration(Snackbar.LENGTH_SHORT);//change Duration as you need
        //snack.setAction(actionButton, new View.OnClickListener());//add your own listener
        View view = snack.getView();
        TextView tv = (TextView) view
                .findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);//change textColor

        TextView tvAction = (TextView) view
                .findViewById(com.google.android.material.R.id.snackbar_action);
        tvAction.setTextSize(16);
        tvAction.setTextColor(Color.WHITE);
        tv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryBlue));
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryBlue));
        snack.show();
    }

    public static void hideKeyboard(Context context){
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void playSound1(Context context){
        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.juntos);
        mPlayer.start();
    }
}
