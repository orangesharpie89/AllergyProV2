package com.example.seanwilliams.AllergyProV2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class excludeFragment extends Fragment {


    private ListView list;
    private Context thisC;
    MenuItemDisplay[] search_results_display_list;

    public excludeFragment()
    {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
     {
         Bundle b = getArguments();
         //thisC = container.getContext();
         View view = inflater.inflate(R.layout.exclude_fragment, container, false);
      //   search_results_display_list = new MenuItemDisplay[5];
         if(b != null) {
             search_results_display_list = (MenuItemDisplay[]) b.getParcelableArray("data");
             list = (ListView) view.findViewById(R.id.listView);
             ListAdapter lAdapter = new CustomAdapter(getActivity(), search_results_display_list);
             list.setAdapter(lAdapter);

             list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     Intent detail_intent = new Intent(getActivity(), menuItemDetailPage.class);
                     detail_intent.putExtra("data", search_results_display_list[position]);
                     startActivity(detail_intent);
                 }
             });
         }
         return view;
     }

     public void set_display(MenuItemDisplay[] in)
     {
         search_results_display_list = in;
     }
}
