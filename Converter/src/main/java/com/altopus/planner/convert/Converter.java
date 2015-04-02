package com.altopus.planner.convert;

import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    private ObjectsJson objectsJson;
    public Converter(ObjectsJson objectsJson){
       this.objectsJson = objectsJson;
    }

    private void parserPrediction(List<String> namePredicates, List<String> sentence, List<String> regSentence) {

        for (String namePredicate : objectsJson.Predicates.keySet()) {
            namePredicates.add(namePredicate);
            LinkedTreeMap link = objectsJson.Predicates.get(namePredicate);
            ArrayList <String> linkTrue = (ArrayList <String>) link.get("true");
            ArrayList <String> linkFalse = (ArrayList <String>) link.get("false");

            regSentence.add("true");
            sentence.add(linkTrue.get(0));
            regSentence.add(linkTrue.get(1));

            regSentence.add("false");
            sentence.add(linkFalse.get(0));
            regSentence.add(linkFalse.get(1));
        }
    }

    private void parserAction(List<String> nameActions, List<String> sentence, List<String> regSentence) {

        for (String nameAction : objectsJson.Actions.keySet()) {
            nameActions.add(nameAction);
            ArrayList <String> link = objectsJson.Actions.get(nameAction);

            sentence.add(link.get(0));
            regSentence.add(link.get(1));
        }
    }

    public void parsePDDL(String file) throws FileNotFoundException {

        try {
            BufferedReader pddl = new BufferedReader(new FileReader(new File(file).getAbsoluteFile()));
            try {
                StringBuilder sb = new StringBuilder();
                String s;
                while ((s = pddl.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }

                String[] token = sb.toString().split("\\(: axiom");
                for (int i = 1; i < token.length; i++) {
                    objectsJson.Axioms.add(token[i].split("\\)\n")[0]);
                }

                String[] actions = sb.toString().split("\\(: action");
                for (String action : actions) {
                    String[] names = action.split("\\:precond");
                    if (names.length == 2) {
                        String name = names[0].trim().split("(\\n|\\t)")[0];
                        String[] tokens = names[1].split(":effect");
                        String preconditions = tokens[0].split("(\\(|\\))")[1].trim();
                        String effects = tokens[1].split("(\\(|\\))")[1].trim();
                        ArrayList <String> link = objectsJson.Actions.get(name);
                        link.add(preconditions);
                        link.add(effects);
                    }
                }

            } finally {
                pddl.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createAction2lingTxt(List<String> nameActions, List<String> sentence) {

        PrintWriter action2ling = null;

        try {
            action2ling = new PrintWriter(new FileOutputStream("maps\\action2ling.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        int count = 0;
        while (count < nameActions.size()) {
            action2ling.println(nameActions.get(count) + " : " + sentence.get(count));
            count++;
        }

        action2ling.close();
    }

    private void createLogic2lingTxt(List<String> namePredicates, List<String> sentence) {

        PrintWriter logic2ling = null;

        try {
            logic2ling = new PrintWriter(new FileOutputStream("maps\\logic2ling.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        int count = 0;
        for (String namePredicate : namePredicates ) {
            logic2ling.println(namePredicate + " true" + " : " + sentence.get(count));
            count++;
            logic2ling.println(namePredicate + " false" + " : " + sentence.get(count));
            count++;
        }

        logic2ling.close();
    }

    private void createActionTxt(List<String> nameActions) {

        PrintWriter action = null;

        try {
            action = new PrintWriter(new FileOutputStream("sets\\action.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        for (String name : nameActions ) {
            action.println(name);
        }

        action.close();
    }

    public void start(String file) throws FileNotFoundException{

        parserPrediction(objectsJson.namePredicates, objectsJson.sentencesPredicates, objectsJson.regSentencePredicates);
        parserAction(objectsJson.nameActions, objectsJson.sentencesAction, objectsJson.regSentenceAction);

        parsePDDL(file);

        createAction2lingTxt(objectsJson.nameActions, objectsJson.sentencesAction);
        createLogic2lingTxt(objectsJson.namePredicates, objectsJson.sentencesPredicates);
        createActionTxt(objectsJson.nameActions);
    }
}