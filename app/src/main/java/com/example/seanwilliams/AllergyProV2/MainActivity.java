package com.example.seanwilliams.AllergyProV2;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    TextView input;
    Button speakButton;
    MenuItem[] master_list;
    MenuItemDisplay[] search_results_display_list;
    MenuItemDisplay[] search_results_display_list_include;
    menuXMLLoader loader_class;
    HashMap<String, String[]> allergy_list;
    ListView menu_list_view;
    mainViewModel model;
    TabLayout master_tab;
    TabLayout.Tab include_tab;
    TabLayout.Tab exclude_tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        model = ViewModelProviders.of(this).get(mainViewModel.class);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Resources re = getResources();
        String pName = getPackageName();
        loader_class = new menuXMLLoader(getApplicationContext().getResources().getAssets());
        try {

            InputStream in = this.getResources().openRawResource(R.raw.blah);
            master_list = loader_class.loadMenu(in, re, pName);
        } catch (IOException e) {
            Log.e("IOWARNING", e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e("XMLWARNING", e.getMessage());
        }


        input = (TextView) findViewById(R.id.editText);
        speakButton = (Button) findViewById(R.id.speakButton);
        speakButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say 'comma' to separate allergens:");
                startActivityForResult(i, 3000);
            }
        });

        allergy_list = new HashMap<>();
        String[] gluten = {"wheat", "wheatberries", "durum", "emmer", "semolina", "spelt", "farina", "farro", "graham", "khorasan", "einkorn", "rye", "barley", "triticale", "malt", "malted", "brewers yeast", "brewer's yeast", "wheat starch", "beer"};
        allergy_list.put("gluten", gluten);

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String input_text = input.getText().toString().toLowerCase().trim();
                    setSearch_inputs(input_text);
                    String[] inputs = getAllergenInputs(input_text);
                    do_the_display(inputs);
                }
                return false;
            }
        });


        master_tab = (TabLayout) findViewById(R.id.master_tab);
        if (model.getSearch_inputs() != null) {
            String[] inputs = getAllergenInputs(model.getSearch_inputs());
            do_the_display(inputs);
        }

        master_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment frag = null;
                Bundle b = null;
                switch (tab.getPosition())
                {
                    case 0:
                        frag = new excludeFragment();
                        if(search_results_display_list != null) {
                            b = new Bundle();
                            b.putParcelableArray("data", search_results_display_list);
                            frag.setArguments(b);
                        }
                        break;
                    case 1:
                        frag = new includeFragment();
                        if(search_results_display_list_include != null) {
                            b = new Bundle();
                            b.putParcelableArray("data", search_results_display_list_include);
                            frag.setArguments(b);
                        }
                        break;
                    default:
                        break;
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_layout, frag);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void setSearch_inputs(String s)
    {
        model.setSearch_inputs(s);
    }

    public void display_list(View view) {
        String input_text = input.getText().toString().toLowerCase().trim();
        setSearch_inputs(input_text);
        String[] inputs = getAllergenInputs(input_text);
        do_the_display(inputs);
    }

    public String[] getAllergenInputs(String search_input)
    {
        String[] blah = search_input.split(",");
        return blah;
    }

    public void do_the_display(String[] search_inputs)
    {
        String i_hate_this_fucking_pattern_start_2 = "([a-zA-Z0-9\\s])*((?<=\\s|,|^)";
        String i_hate_this_fucking_pattern_end_2 = ")([a-zA-Z0-9\\s$]*)";
        //This method checks the hashmap allergy_list, and potentially adds items to the search_inputs array
        search_inputs = checkListedAllergens(search_inputs);

        ArrayList<MenuItemDisplay> results = new ArrayList<>();
        ArrayList<MenuItemDisplay> results_include = new ArrayList<>();

        //Do this search feature for every item in the search string array
        int id = 0;
        for(String search_input:search_inputs)
        {
            search_input = search_input.trim();
            for (MenuItem m : master_list)
            {
                String component_list = m.get_complexingredients().keySet().toString().toLowerCase();
                //if (m.get_menuitemingredients().contains(search_input))
                if( Pattern.compile(i_hate_this_fucking_pattern_start_2+ search_input+i_hate_this_fucking_pattern_end_2).matcher(m.get_menuitemingredients()).find() ||Pattern.compile(i_hate_this_fucking_pattern_start_2+ search_input+i_hate_this_fucking_pattern_end_2).matcher(component_list).find())
                {
                    //If the menu item is not in the result list, add it
                    //Otherwise, simply add the allergen info into the display object
                    if(checkForDuplicates(results, m))
                    {
                        results.add(new MenuItemDisplay(m));
                        results.get(results.size() - 1).setAllergen(search_input);
                    }
                    else
                    {
                        results.get(findMenuItem(results, m)).setAllergen(search_input);
                    }
                }
                else
                {
                    if(checkForDuplicates(results_include, m))
                    {
                        results_include.add(new MenuItemDisplay(m));
                        results_include.get(results_include.size() - 1).setAllergen(search_input);
                    }
                    else
                    {
                        results_include.get(findMenuItem(results_include, m)).setAllergen(search_input);
                    }
                }
            }
            id++;
        }

        search_results_display_list = new MenuItemDisplay[results.size()];
        results.toArray(search_results_display_list);
        search_results_display_list_include = new MenuItemDisplay[results_include.size()];
        results_include.toArray(search_results_display_list_include);
        master_tab.getTabAt(0).select();
        Fragment frag = new excludeFragment();
        Bundle b = new Bundle();
        b.putParcelableArray("data", search_results_display_list);
        frag.setArguments(b);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_layout, frag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();



//        ListAdapter lAdapter = new CustomAdapter(this, search_results_display_list);
//        ListView lView = (ListView) findViewById(R.id.viewList);
//        lView.setAdapter(lAdapter);
        hideKeyboard(this);

        final Context context = this;
//        menu_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
  //          @Override
    //        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      //          Intent detail_intent = new Intent(context, menuItemDetailPage.class);
        //        detail_intent.putExtra("data", search_results_display_list[position]);
          //      startActivity(detail_intent);
            //}
        //});
    }

    private String[] checkListedAllergens(String[] search_inputs)
    {
        ArrayList<String> temp = new ArrayList<>();
        for(String s : search_inputs)
        {
            s = s.trim();
            //If not already in the temp list, add it
            //Add code to check for duplicate
            if(checkForDuplicates(temp, s))
            {
                temp.add(s);
            }
            if(allergy_list.containsKey(s))
            {
                for(String t : allergy_list.get(s))
                {
                    if(checkForDuplicates(temp, t))
                    {
                        temp.add(t);
                    }
                }
            }
        }
        String[] return_array = new String[temp.size()];
        return temp.toArray(return_array);
    }

    private int findMenuItem(ArrayList<MenuItemDisplay> list, MenuItem item)
    {
        int id = 0;
        for(MenuItemDisplay m : list)
        {
            if(m.getFood().equals(item))
            {
                return id;
            }
            id++;
        }
        return id;
    }

    private boolean checkForDuplicates(ArrayList<String> list, String item)
    {
        for(String s : list)
        {
            if(s.equals(item))
            {
                return false;
            }
        }
        return true;
    }

    private boolean checkForDuplicates(ArrayList<MenuItemDisplay> list, MenuItem item)
    {
        //Return true if there are no duplicates
        for(MenuItemDisplay m : list)
        {
            if(m.getFood().equals(item))
            {
                return false;
            }
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 3000)
        {
            if(resultCode == RESULT_OK)
            {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String answer = results.get(0);
                input.setText(answer);
                model.setSearch_inputs(answer);
                String[] inputs = getAllergenInputs(model.getSearch_inputs());
                do_the_display(inputs);

            }
        }
    }
}
