package com.altopus.planner.convert;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {

        ObjectsJson objectsJson;
        Gson gson = new Gson();

        try {
            //BufferedReader json = new BufferedReader(new FileReader(args[0]));
            BufferedReader json = new BufferedReader(new FileReader("Lamp_Dictionary.json"));
            //convert the json string to object
            objectsJson = gson.fromJson(json, ObjectsJson.class);
            Converter converter = new Converter(objectsJson);
            converter.start("лампа_PDDL.txt");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}