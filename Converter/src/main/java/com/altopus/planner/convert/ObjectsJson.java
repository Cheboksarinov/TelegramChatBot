package com.altopus.planner.convert;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectsJson {
    public Map<String, LinkedTreeMap> Predicates;
    public Map<String, ArrayList<String>> Actions;
    public ArrayList<String> Axioms = new ArrayList<String>();

    public List<String> namePredicates = new ArrayList<String>();
    public List<String> sentencesPredicates = new ArrayList<String>();
    public List<String> regSentencePredicates = new ArrayList<String>();
    public List<String> nameActions = new ArrayList<String>();
    public List<String> sentencesAction = new ArrayList<String>();
    public List<String> regSentenceAction = new ArrayList<String>();

    public ObjectsJson() {};

}
