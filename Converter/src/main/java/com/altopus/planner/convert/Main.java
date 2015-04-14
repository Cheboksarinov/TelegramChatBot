package com.altopus.planner.convert;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

      try {
          ObjectsJson objectsJson = Parser.parse();
          Converter converter = new Converter(objectsJson);
          objectsJson = converter.start();
          DataBase.getInstance().initialize(objectsJson);
      }
      catch (FileNotFoundException e) {
          System.out.println(e.getMessage());
      }
    }
}