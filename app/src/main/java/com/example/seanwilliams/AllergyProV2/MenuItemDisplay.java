package com.example.seanwilliams.AllergyProV2;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MenuItemDisplay implements Parcelable
{
    private MenuItem food;
    private ArrayList<String> allergen;

    public MenuItemDisplay()
    {

    }

    public MenuItemDisplay(Parcel in)
    {
        food = in.readParcelable(MenuItem.class.getClassLoader());
        Bundle b = in.readBundle();
        allergen = (ArrayList<String>)b.getSerializable("list");
    }

    public MenuItemDisplay(MenuItem food)
    {
        this.food = food;
        allergen = new ArrayList<String>(0);
    }

    public MenuItem getFood() {
        return food;
    }

    public void setFood(MenuItem food) {
        this.food = food;
    }

    public ArrayList<String> getAllergen() {
        return allergen;
    }

    public void setAllergen(ArrayList<String> allergen) {
        this.allergen = allergen;
    }

    public void setAllergen(String allergen)
    {
        this.allergen.add(allergen);
    }

    public String formatAllergens()
    {
        String results = "";
        for(String x : allergen)
        {
            results+=x+", ";
        }
        return results.substring(0, results.length()-2);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(food, flags);
        Bundle b = new Bundle();
        b.putSerializable("list", allergen);
        dest.writeBundle(b);
    }

    public static final Parcelable.Creator<MenuItemDisplay> CREATOR = new Parcelable.Creator<MenuItemDisplay>()
{
    @Override
    public MenuItemDisplay createFromParcel(Parcel source) {
        return new MenuItemDisplay(source);
    }

    @Override
    public MenuItemDisplay[] newArray(int size) {
        return new MenuItemDisplay[0];
    }
};
}
