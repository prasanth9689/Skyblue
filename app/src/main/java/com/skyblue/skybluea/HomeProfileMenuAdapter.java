package com.skyblue.skybluea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeProfileMenuAdapter extends BaseAdapter {
    Context context;
    private final String [] values;
    private final String [] numbers;
    private final String [] details;
    private final int [] images;

    public HomeProfileMenuAdapter(Context context, String [] values, String [] numbers, int [] images , String [] details){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.values = values;
        this.numbers = numbers;
        this.images = images;
        this.details = details;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.row_model_home_profile_menu, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
         //   viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.aVersiontxt);
            viewHolder.txtDetails = (TextView) convertView.findViewById(R.id.aVersiontxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(values[position]);
     //   viewHolder.txtVersion.setText("Version: "+numbers[position]);
        viewHolder.icon.setImageResource(images[position]);
        viewHolder.txtDetails.setText(details[position]);

        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
      //  TextView txtVersion;
        ImageView icon;
        TextView txtDetails;

    }

}