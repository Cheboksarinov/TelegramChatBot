package com.altopus.planner.convert;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {

        ObjectsJson objectsJson;
        Gson gson = new Gson();

        try {
            BufferedReader json = new BufferedReader(new FileReader(args[0]));
            //convert the json string to object
            objectsJson = gson.fromJson(json, ObjectsJson.class);
            System.out.println(objectsJson.toString());
            Converter converter = new Converter(objectsJson);

            converter.ToAiml();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}