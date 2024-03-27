package com.skyblue.skybluea.helper.custom_media_picker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.skyblue.skybluea.R;
import com.skyblue.skybluea.databinding.ActivityMediaPickerBinding;

import java.util.ArrayList;

public class MediaPickerActivity extends AppCompatActivity {
    private ActivityMediaPickerBinding binding;
    private Context context = this;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerFragmentAdapter adapter;

    // array for tab labels
    private String[] tabs = new String[]{"Video", "Image"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMediaPickerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // call function to initialize views
        viewPager2Init();

        // bind and set tabLayout to viewPager2 and set labels for every tab
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            tab.setText(tabs[position]);
        }).attach();

        // set default position to 1 instead of default 0
        viewPager2.setCurrentItem(0, false);

        binding.back.setOnClickListener(v -> finish());
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
    private void viewPager2Init() {
        // initialize tabLayout
        tabLayout = findViewById(R.id.tab_layout);
        // initialize viewPager2
        viewPager2 = findViewById(R.id.view_pager2);
        // create adapter instance
        adapter = new ViewPagerFragmentAdapter(this);
        // set adapter to viewPager2
        viewPager2.setAdapter(adapter);

        // remove default elevation of actionbar
//        getSupportActionBar().setElevation(0);
    }

    // create adapter to attach fragments to viewpager2 using FragmentStateAdapter
    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new VideoFragment();
                case 1:
                    return new ImageFragment();
            }
            return new VideoFragment(); //chats fragment
        }

        @Override
        public int getItemCount() {
            return tabs.length;
        }
    }
}