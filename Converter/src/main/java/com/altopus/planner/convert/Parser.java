package com.altopus.planner.convert;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;

public class Parser {

    private static final String filename = "alice2\\domain.json.txt";

    static ObjectsJson parse() {
        ObjectsJson objectsJson;
        Gson gson = new Gson();

        try {
            BufferedReader json = new BufferedReader(new FileReader(filename));
            objectsJson = gson.fromJson(json, ObjectsJson.class);
            return objectsJson;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
