package com.example.seanwilliams.AllergyProV2;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class menuXMLLoader
{
    InputStream in;
    XmlPullParserFactory parseFactory;
    XmlPullParser parser;
    AssetManager assMan;

    public menuXMLLoader(AssetManager assMan)
    {
        this.assMan = assMan;
        try
        {
            parseFactory = XmlPullParserFactory.newInstance();
            parser = parseFactory.newPullParser();
            in = assMan.open("menu.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
        }catch(XmlPullParserException e)
        {

        }catch(IOException ex)
        {

        }
    }

    //This reads the menu.xml file, loads into the three different types of objects:
    //item
    //complexingredient
    //sauce
    //Then it iterates the items objects and loads in the complex ingredients and sauces recursively
    public MenuItem[] loadMenu(InputStream file, Resources re, String pName) throws XmlPullParserException, IOException
    {
        parseFactory = XmlPullParserFactory.newInstance();
        parser = parseFactory.newPullParser();
       // in = assMan.open("menu.xml");
        in = file;
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);

        int eventType = parser.getEventType();
        ArrayList<menu> menus = new ArrayList<>();
        ArrayList<complexitem> complexities = new ArrayList<>();
        ArrayList<sauce> sauces = new ArrayList<>();

        menu curMenu = null;
        complexitem curComplex = null;
        sauce curSauce = null;
        int whichClass = 0;
        while(eventType != XmlPullParser.END_DOCUMENT)
        {
            String name = null;
            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if("menuitem".equals(name))
                    {
                        curMenu = new menu();
                        whichClass = 1;
                    }
                    else if("complexitem".equals(name))
                    {
                        curComplex = new complexitem();
                        whichClass = 2;
                    }
                    else if("sauce".equals(name))
                    {
                        curSauce = new sauce();
                        whichClass = 3;
                    }
                    else if("name".equals(name))
                    {
                        switch(whichClass)
                        {
                            case 1:
                                curMenu.setName(parser.nextText());
                                break;
                            case 2:
                                curComplex.setName(parser.nextText());
                                break;
                            case 3:
                                curSauce.setName(parser.nextText());
                                break;
                        }
                    }
                    else if("basicingredients".equals(name))
                    {
                        switch(whichClass)
                        {
                            case 1:
                                curMenu.setBasicIngredients(parser.nextText());
                                break;
                            case 2:
                                curComplex.setBasicIngredients(parser.nextText());
                                break;
                            case 3:
                                curSauce.setBasicIngredients(parser.nextText());
                                break;
                        }
                    }
                    else if("complexingredients".equals(name))
                    {
                        switch(whichClass)
                        {
                            case 1:
                                curMenu.setComplexIngredients(parser.nextText());
                                break;
                            case 2:
                                curComplex.setComplexIngredients(parser.nextText());
                                break;
                            case 3:
                                curSauce.setComplexIngredients(parser.nextText());
                                break;
                        }
                    }
                    else if("sauces".equals(name))
                    {
                        curMenu.setSauce(parser.nextText());
                    }
                    else if("picture".equals(name))
                    {
                        curMenu.setPicID(re.getIdentifier(parser.nextText(), "drawable", pName));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if("menuitem".equals(name))
                    {
                        menus.add(curMenu);
                    }
                    else if("complexitem".equals(name))
                    {
                        complexities.add(curComplex);
                    }
                    else if("sauce".equals(name))
                    {
                        sauces.add(curSauce);
                    }
                    else if("name".equals(name) || "basicingredients".equals(name) || "complexingredients".equals(name) || "sauces".equals(name) || "picture".equals(name))
                    {
                        //parser.nextTag();
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return recursiveParser(menus, complexities, sauces);
    }

    public MenuItem[] recursiveParser(ArrayList<menu> m, ArrayList<complexitem> c, ArrayList<sauce> s)
    {

        HashMap<String, complexitem> complexMap = new HashMap<>();
        for(complexitem x : c)
        {
            complexMap.put(x.name, x);
        }
        HashMap<String, sauce> sauceMap = new HashMap<>();
        for(sauce x : s)
        {
            sauceMap.put(x.name, x);
        }
        MenuItem[] final_menu = new MenuItem[m.size()];
        int id = 0;

        for(menu x : m)
        {
            final_menu[id] = new MenuItem();
            final_menu[id].set_id(x.getPicID());
            final_menu[id].set_menuitemname(x.getName());
            final_menu[id].set_menuitemingredients(x.getBasicIngredients());
            final_menu[id].set_basicingredients(x.getBasicIngredients());
            if(x.complexIngredients != null && x.complexIngredients.length() > 0)
            {
                String[] stuff = x.complexIngredients.split(",");
                String temp = "";
                for(String thing : stuff)
                {
                    temp = recurseComplexIngredients(thing, complexMap);
                    final_menu[id].set_complexingredients(thing, temp.substring(1, temp.length()));
                    final_menu[id].set_menuitemingredients(final_menu[id].get_menuitemingredients()+temp);
                }
            }
            id++;
        }

        return final_menu;
    }

    public String recurseComplexIngredients(String pointer, HashMap<String, complexitem> map)
    {
        String complex = map.get(pointer).getBasicIngredients();
        if(map.get(pointer).hasComplex())
        {
            String[] stuff = map.get(pointer).complexIngredients.split(",");
            for(String thing : stuff)
            {
                complex = complex + recurseComplexIngredients(thing, map);
            }
        }
        return "," + complex;
    }

    public String recurseSauces(String pointer, HashMap<String, sauce> map, HashMap<String, complexitem> cmap)
    {
        String complex = "";
        if(map.get(pointer).hasComplex())
        {
            String[] stuff = map.get(pointer).complexIngredients.split(",");
            for(String thing : stuff)
            {
                complex = complex + recurseSauces(thing, map, cmap);
            }
        }
        return "," + complex;
    }

    public class menu {
        String name;
        String basicIngredients;
        String complexIngredients;
        String sauce;
        int picID;

        public menu()
        {
            name = null;
            basicIngredients = null;
            complexIngredients = null;
            sauce = null;
        }

        public void setName(String n)
        {
            name = n;
        }

        public void setBasicIngredients(String basicIngredients) {
            this.basicIngredients = basicIngredients;
        }

        public void setComplexIngredients(String complexIngredients) {
            this.complexIngredients = complexIngredients;
        }

        public void setSauce(String sauce) {
            this.sauce = sauce;
        }

        public String getName() {
            return name;
        }

        public String getBasicIngredients() {
            return basicIngredients;
        }

        public String getComplexIngredients() {
            return complexIngredients;
        }

        public String getSauce() {
            return sauce;
        }

        public int getPicID() {
            return picID;
        }

        public void setPicID(int picID) {
            this.picID = picID;
        }
    }

    public class complexitem {
        String name;
        String basicIngredients;
        String complexIngredients;

        public complexitem()
        {
            name = "";
            basicIngredients = "";
            complexIngredients = "";
        }

        public void setName(String n)
        {
            name = n;
        }

        public void setBasicIngredients(String basicIngredients) {
            this.basicIngredients = basicIngredients;
        }

        public void setComplexIngredients(String complexIngredients) {
            this.complexIngredients = complexIngredients;
        }

        public String getName() {
            return name;
        }

        public String getBasicIngredients() {
            return basicIngredients;
        }

        public String getComplexIngredients() {
            return complexIngredients;
        }

        public boolean hasComplex()
        {
            if(complexIngredients.length() > 0)
            {
                return true;
            }
            return false;
        }
    }

    public class sauce
    {
        String name;
        String basicIngredients;
        String complexIngredients;

        public sauce()
        {
            name = "";
            basicIngredients = "";
            complexIngredients = "";
        }

        public void setName(String n)
        {
            name = n;
        }

        public void setBasicIngredients(String basicIngredients) {
            this.basicIngredients = basicIngredients;
        }

        public void setComplexIngredients(String complexIngredients) {
            this.complexIngredients = complexIngredients;
        }

        public String getName() {
            return name;
        }

        public String getBasicIngredients() {
            return basicIngredients;
        }

        public String getComplexIngredients() {
            return complexIngredients;
        }

        public boolean hasComplex()
        {
            if(complexIngredients.length() > 0)
            {
                return true;
            }
            return false;
        }
    }
}

