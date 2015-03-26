package com.altopus.schedule;

public class State {
    public int [] predicates;
    public String stringState;

    public State(String state) {
        predicates = new int [state.length()];
        stringState = state;
        for (int i = 0 ; i < state.length(); i++) {
            predicates[i] = Character.getNumericValue(state.charAt(i));
        }
    }
    public boolean equals(Object obj) {
        if(obj == this)
            return true;
        if(obj == null)
            return false;
        if(!(getClass() == obj.getClass()))
            return false;
        else {
            State tmp = (State)obj;
            for (int i = 0; i < predicates.length; i++) {
                if (tmp.predicates[i] != this.predicates[i])
                    return  false;
            }
            return true;
        }
    }
}
