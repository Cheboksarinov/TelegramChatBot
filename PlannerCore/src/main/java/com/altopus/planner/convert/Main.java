package com.altopus.planner.convert;

import com.altopus.schedule.Planner;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException{
          Planner planer = new Planner();
          planer.DoPlan("~L", "L");
    }
}