package com.example.seanwilliams.AllergyProV2;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

public class MenuItem implements Parcelable {

    private int _id;
    private String _menuitemname;
    private String _menuitemingredients;
    private String _basicingredients;
    //This is where individual components of the dish are stored
    //i.e. whipped cream for a thunder, so if someone had an ultrasperse allergy,
    //you'd know to just leave out the whipped cream.
    private HashMap<String, String> _complexingredients;

    public MenuItem()
    {
        _complexingredients = new HashMap<>();
    }

    public MenuItem(Parcel in)
    {
        _id = in.readInt();
        _menuitemname = in.readString();
        _menuitemingredients = in.readString();
        _basicingredients = in.readString();
        Bundle b = in.readBundle();
        _complexingredients = (HashMap<String, String>)b.getSerializable("map");
    }

    public MenuItem(String itemname, String itemingredients)
    {
        this._menuitemname = itemname;
        this._menuitemingredients = itemingredients;

        _complexingredients = new HashMap<>();
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_menuitemname() {
        return _menuitemname;
    }

    public void set_menuitemname(String _menuitemname) {
        this._menuitemname = _menuitemname;
    }

    public String get_menuitemingredients() {
        return _menuitemingredients;
    }

    public void set_menuitemingredients(String _menuitemingredients) {
        this._menuitemingredients = _menuitemingredients;
    }

    public boolean checkIngredients(String search)
    {
        return _menuitemingredients.contains(search);
    }

    public HashMap<String, String> get_complexingredients() {
        return _complexingredients;
    }

    public String getFromComplexIngredients(String key)
    {
        return _complexingredients.get(key);
    }

    public void set_complexingredients(String key, String value)
    {
        _complexingredients.put(key, value);
    }
    public void set_complexingredients(HashMap<String, String> _complexingredients) {
        this._complexingredients = _complexingredients;
    }

    public String get_basicingredients() {
        return _basicingredients;
    }

    public void set_basicingredients(String _basicingredients) {
        this._basicingredients = _basicingredients;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_menuitemname);
        dest.writeString(_menuitemingredients);
        dest.writeString(_basicingredients);
        Bundle b = new Bundle();
        b.putSerializable("map", _complexingredients);
        dest.writeBundle(b);

    }public static final Parcelable.Creator<MenuItem> CREATOR = new Parcelable.Creator<MenuItem>()
    {
        @Override
        public MenuItem createFromParcel(Parcel source) {
            return new MenuItem(source);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[0];
        }
    };
}
