package com.skyblue.skybluea.channel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.skyblue.skybluea.R;
import com.skyblue.skybluea.SessionHandler;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.util.Objects;

public class PageCreateBusNameActivity extends AppCompatActivity {
    private SessionHandler session;

    private static final String SHARED_PREFE_ID = "mypref";
    private static final String KEY_PREFE_ID = "myid";


    EmojiEditText nameText;
    Button createBtn;
    ViewGroup rootView;

    String GetSendString , dateString , timeString , timeDateString;
    String  toServerUnicodeEncoded;

    ProgressDialog progressDialog;

    EditText txtDescription;
    Spinner txtSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new IosEmojiProvider());

        setContentView(R.layout.activity_page_create_bus_name);
        nameText = findViewById(R.id.id_name_text);
        createBtn = findViewById(R.id.id_create_button);
        rootView = findViewById(R.id.main_activity_root_view);
        txtDescription = findViewById(R.id.id_description);
        txtSpinner = findViewById(R.id.id_spinner);

        nameText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(nameText);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_business, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtSpinner.setAdapter(adapter);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String descriptionHolder = txtDescription.getText().toString();
                String nameHolder = Objects.requireNonNull(nameText.getText()).toString().trim();
                String spinnerHolder = txtSpinner.getSelectedItem().toString().trim();


                if (nameHolder.isEmpty() || nameHolder.length() < 2) {
                    nameText.setError("Please enter name");
                    nameText.requestFocus();
                    return;
                }
                if (descriptionHolder.isEmpty() || descriptionHolder.length() < 2)
                {
                    txtDescription.setError("Please enter description");
                    txtDescription.requestFocus();
                }else
                {
                    String page_id = getIntent().getStringExtra("page_id");

                    Intent intent = new Intent(PageCreateBusNameActivity.this, PageCreateBusContactActivity.class);
                    intent.putExtra("page_name", nameHolder);
                    intent.putExtra("page_des", descriptionHolder);
                    intent.putExtra("page_spinner", spinnerHolder);
                    intent.putExtra("page_id", page_id);
                    startActivity(intent);

                    //  Toast.makeText(PageCreateNameActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();
                }





                /*
                GetSendString = Objects.requireNonNull(nameText.getText()).toString().trim();

                dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                timeString = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                timeDateString  = dateString +" "+timeString;

                //     toServerUnicodeEncoded = org.apache.commons.text.StringEscapeUtils.escapeJava(GetSendString);
                try {
                    byte[] data = nameText.getText().toString().getBytes("UTF-8");
                    toServerUnicodeEncoded = Base64.encodeToString(data, Base64.DEFAULT);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                CREATEPAGE();

                 */
            }


        });
    }


}