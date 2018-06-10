package com.example.seanwilliams.AllergyProV2;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class menuItemDetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_detail_page);



        MenuItemDisplay food = getIntent().getParcelableExtra("data");

        String i_hate_this_fucking_pattern_start = "(?<=^)[a-zA-Z0-9\\s]*([^a-zA-Z,]|(?<=,|^))(";
        String i_hate_this_fucking_pattern_start_2 = "([a-zA-Z0-9\\s])*((?<=\\s|,|^)";
        String i_hate_this_fucking_pattern_end = ")(?=\\s|,|$)[a-zA-Z0-9\\s$]*";
        String i_hate_this_fucking_pattern_end_2 = ")([a-zA-Z0-9\\s$]*)";

        ImageView title_pic = (ImageView) findViewById(R.id.menuitempicturedisplay);
        title_pic.setImageResource(food.getFood().get_id());
        TextView title = (TextView) findViewById(R.id.displayPageTitle);
        title.setText(food.getFood().get_menuitemname());

        TableRow.LayoutParams param = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        param.weight = 1;
        TableLayout table = (TableLayout) findViewById(R.id.recipedisplaytable);
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        TextView complex_item_name = new TextView(this);
        complex_item_name.setText("Basic Ingredients:");
        //complex_item_name.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT));
        complex_item_name.setLayoutParams(param);
        row.addView(complex_item_name);


        String allergies_to_match = "";
        TextView basic_item_ingredients = new TextView(this);
        param = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        param.weight = 1;
        //param.setMargins(0, 0, 0, 0);
        if(food.getAllergen().get(0).length() > 0)
        {
            for (String s : food.getAllergen()) {
                allergies_to_match += s + "|";
            }
            allergies_to_match = allergies_to_match.substring(0, allergies_to_match.length() - 1);
            //Pattern p = Pattern.compile("([a-zA-Z0-9\\s]*(" + allergies_to_match + "))+(?=,|$)");
            Pattern p = Pattern.compile(i_hate_this_fucking_pattern_start_2+ allergies_to_match+i_hate_this_fucking_pattern_end_2);
            Matcher m = p.matcher(food.getFood().get_basicingredients().replace(",", ", "));
            SpannableStringBuilder str_builder = new SpannableStringBuilder(String.valueOf(food.getFood().get_basicingredients()).replace(",", ", "));
            while (m.find()) {
                str_builder.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            basic_item_ingredients.setText(str_builder);
            basic_item_ingredients.setLayoutParams(param);
        }
        else
        {
            basic_item_ingredients.setText(food.getFood().get_basicingredients().replace(",", ", "));
            basic_item_ingredients.setLayoutParams(param);
        }

        row.addView(basic_item_ingredients);
        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        Iterator iterator = food.getFood().get_complexingredients().entrySet().iterator();
        while(iterator.hasNext())
        {
            param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            param.weight = 1;
            Map.Entry pair = (Map.Entry)iterator.next();
            row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            complex_item_name = new TextView(this);
            complex_item_name.setText(String.valueOf(pair.getKey())+":");
            complex_item_name.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT));
            row.addView(complex_item_name);

            TextView complex_item_ingredients = new TextView(this);
            if(food.getAllergen().get(0).length() > 0) {
                //Pattern p = Pattern.compile("([a-zA-Z0-9\\s]*(" + allergies_to_match + "))+(?=,)");
                Pattern p = Pattern.compile(i_hate_this_fucking_pattern_start_2+allergies_to_match+i_hate_this_fucking_pattern_end_2);
                Matcher m = p.matcher(String.valueOf(pair.getValue()).replace(",", ", "));
                SpannableStringBuilder str_builder = new SpannableStringBuilder(String.valueOf(pair.getValue()).replace(",", ", "));
                while (m.find()) {
                    str_builder.setSpan(new ForegroundColorSpan(Color.RED), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }


                param = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
                param.weight = 1;
                //param.setMargins(0, 0, 0, 0);
                complex_item_ingredients.setText(str_builder);
                complex_item_ingredients.setLayoutParams(param);
            }
            else
            {
                param = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
                complex_item_ingredients.setText(String.valueOf(pair.getValue()).replace(",", ", "));
                complex_item_ingredients.setLayoutParams(param);
            }

            row.addView(complex_item_ingredients);
            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }


}
