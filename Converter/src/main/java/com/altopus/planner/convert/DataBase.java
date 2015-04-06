package com.altopus.planner.convert;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Map;

public class DataBase {

    private LinkedTreeMap<String, Action> actions;
    private static DataBase instance;
    private ObjectsJson objectsJson;

    private DataBase() {}

    public void initialize(ObjectsJson objectsJson) {
        this.objectsJson = objectsJson;
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public LinkedTreeMap<String, Action> getActions() {
        actions = new LinkedTreeMap<>();

        for (Map.Entry<String, ArrayList<String>> nameAction : objectsJson.Actions.entrySet()) {
            ArrayList <String> link = nameAction.getValue();
            Action action = new Action();
            action.sentences = link.get(0);
            action.regSentence = link.get(1);
            action.effects = link.get(2);
            action.preconditions = link.get(3);
            actions.put(nameAction.getKey(), action);
        }
        return actions;
    }

    public Map<String, LinkedTreeMap> getPredicates() {
        return objectsJson.Predicates;
    }

    public ArrayList<String> getAxioms() {
        return objectsJson.Axioms;
    }

    public class Action {
        public String sentences;
        public String regSentence;
        public String preconditions;
        public String effects;

        public Action() { }
    }
}
