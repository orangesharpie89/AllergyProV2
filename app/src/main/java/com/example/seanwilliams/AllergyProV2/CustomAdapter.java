package com.example.seanwilliams.AllergyProV2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seanwilliams.AllergyProV2.R;

import java.util.HashMap;

class CustomAdapter extends ArrayAdapter<MenuItemDisplay>
{
    public HashMap<String, Integer> pics;

    public CustomAdapter(@NonNull Context context, MenuItemDisplay[] resource) {
        super(context, R.layout.custom_row, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater lInflater = LayoutInflater.from(getContext());
        View customView = lInflater.inflate(R.layout.custom_row, parent, false);

        MenuItem item = getItem(position).getFood();
        TextView text = (TextView) customView.findViewById(R.id.menuitemname);
        TextView allergen_trigger = (TextView) customView.findViewById(R.id.menuitemallergens);
        ImageView image = (ImageView) customView.findViewById(R.id.menuitempicture);


        text.setText(item.get_menuitemname());
        allergen_trigger.setText(getItem(position).formatAllergens());
        image.setImageResource(item.get_id());
        return customView;
    }
}