package com.example.seanwilliams.AllergyProV2;

import android.arch.lifecycle.ViewModel;

public class mainViewModel extends ViewModel
{
    private String search_inputs;

    public String getSearch_inputs() {
        return search_inputs;
    }

    public void setSearch_inputs(String search_inputs) {
        this.search_inputs = search_inputs;
    }
}
