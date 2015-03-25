package com.altopus.schedule;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Vertex {
    public State [] states;
    public Set<Integer> mismatchedPositions;
    public static List <Integer> activePosition;

    public Vertex (List <String> stringStates, List <Integer> position){
        states = new State[stringStates.size()];
        for (int i = 0; i < stringStates.size(); i++) {
             states[i] = new State(stringStates.get(i));
        }
        SetActivePosition(position);
        IsPredicateDefine();
    }
    public Vertex(State [] states) {
        this.states = new State[states.length];
        this.states = states;
        IsPredicateDefine();
    }

    private static void SetActivePosition(List <Integer> position) {
        activePosition = position;
    }
    public static State[] ConvertFromStringToState(List <String> stringStates) {
       State [] s = new State[stringStates.size()];
       for (int i = 0; i < stringStates.size(); i++) {
            s[i] = new State(stringStates.get(i));
       }
       return s;
    }

    public void IsPredicateDefine() {
        mismatchedPositions = new HashSet<Integer>();
        for (int i : activePosition) {
            int k = 0;
            while (k < states.length) {
                for (int j = 0; j < states.length; j++) {
                    if (states[k].predicates[i] != states[j].predicates[i]) {
                        mismatchedPositions.add(i);
                        break;
                    }
                }
                k++;
            }
       }
    }
    public String toString() {
        String result = "";
        for (int i: activePosition) {
            int count = 0;
            for (Map.Entry<String, Integer> entry : DataBase.getInstance().serialNumber.entrySet()) {
                if (i + 1 == entry.getValue()) {
                    for (State s : states) {
                        if (s.predicates[i] == 1)
                             count++;
                    }
                    if (count == 0)
                        result += "~" + entry.getKey() + " ";
                    else if (count == states.length)
                        result += entry.getKey() + " ";
                    else result += "?" + entry.getKey() + " ";
                }

            }
        }
        return result;
    }
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(getClass() == obj.getClass()))
            return false;
        else {
            Vertex tmp = (Vertex)obj;
            if(tmp.states.length != this.states.length)
                return false;
            else
                for (int i = 0; i < states.length; i++) {
                    if (tmp.states[i] != this.states[i])
                        return false;
                }
                return true;
        }
    }

}
