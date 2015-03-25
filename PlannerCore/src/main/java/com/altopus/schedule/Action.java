package com.altopus.schedule;

import java.util.ArrayList;
import java.util.List;

public class Action {
    public  String nameActions;
    public  List<String> preconditions;
    public  List<String> effects;
    public  List <Integer> position;

    public Action() {
        preconditions = new ArrayList<>();
        effects = new ArrayList<>();
        position = new ArrayList<>();
    }
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(getClass() == obj.getClass()))
            return false;
        else {
            Action tmp = (Action)obj;
            if(tmp.nameActions == this.nameActions)
                return true;
            else
                return false;
        }
    }
}
