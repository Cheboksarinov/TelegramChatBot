package com.altopus.schedule;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class DataBase {

    private static DataBase instance;

    public  List<String> axioms;
    public  List<String> physicalStates;
    public  Map <String, Integer> serialNumber;
    public  Integer Length;
    public  List<Action> allActions;
    public  List<Action> activeActions;

    private DataBase() {}

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public void GetCollectionPhysicalStates() {
        physicalStates = new ArrayList<String>();
        List <String> irrelevantSet = new ArrayList<>();
        int numberPredicate = 0;
        // генерация всех возможных наборов значений предикатов
        while (numberPredicate < Math.pow(2, Length)) {
            String binString = Integer.toBinaryString(numberPredicate);
            while (binString.length() < Length) {
                binString = "0" + binString;
            }
            physicalStates.add(binString);
            numberPredicate++;
        }
        // логический вывод
        PropositionalSolver ps = new PropositionalSolver();
        String nameOfPredicates = "";
        for (Map.Entry<String, Integer> entry : serialNumber.entrySet()) {
            nameOfPredicates += entry.getKey() + " ";
        }
        for (String set : physicalStates) {
            String transformator = "";
            int i = 0;
            while (i < set.length()) {
                if (set.charAt(i) == '0')
                    transformator += "false" + " ";
                else
                    transformator += "true" + " ";

                i++;
            }

            // Если набор не выполним в рамках аксиомы, то добавить в лист нерелевантных set'ов
            try {
                for (String axiom : axioms) {
                    if (!ps.SATCall(nameOfPredicates, transformator, axiom)) {
                        if (!irrelevantSet.contains(set))
                            irrelevantSet.add(set);
                    }
                }
            }
            catch (Exception e){}
        }
        // Удалить противоречащие аксиомам set'ы
        physicalStates.removeAll(irrelevantSet);
    }

    //Получить из pddl аксиомы, действия
    public void ParsePDDL(String file) throws FileNotFoundException {

        allActions = new ArrayList<Action>();
        try {
            BufferedReader pddl = new BufferedReader(new FileReader(new File(file).getAbsoluteFile()));
            try {
                StringBuilder sb = new StringBuilder();
                String s;
                while ((s = pddl.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }

                axioms = new ArrayList<String>();
                String[] token = sb.toString().split("\\(: axiom");
                for (int i = 1; i < token.length; i++) {
                    axioms.add(token[i].split("\\)\n")[0]);
                }
                GetCollectionPhysicalStates();

                String[] actions = sb.toString().split("\\(: action");
                for (String action : actions) {
                    String[] names = action.split("\\:precond");
                    if (names.length == 2) {
                        String[] tokens = names[1].split(":effect");
                        String preconditions = tokens[0].trim();
                        String effects = tokens[1].split("\\)\n")[0].trim();

                        Action a = new Action();
                        a.nameActions = names[0].trim();
                        a.preconditions = ParseFormula(preconditions);
                        a.effects = ParseFormula(effects);
                        a.position = GetNumberActivePredicates(preconditions);
                        allActions.add(a);
                    }
                }

            } finally {
                pddl.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
    // Считывает txt файл с соответсвием предикатов и их порядковому номеру
    public  void MatchPredicateWithSerialNumber(String file) {
        try {
            BufferedReader txt = new BufferedReader(new FileReader(new File(file).getAbsoluteFile()));
            serialNumber = new LinkedHashMap<String, Integer>();

            try {
                String s;
                while ((s = txt.readLine()) != null) {
                    String[] tokens = s.split(" ");
                    serialNumber.put(tokens[0].trim(), Integer.parseInt(tokens[1].trim()));
                }
                Length = serialNumber.size();
            } finally {
                txt.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private  List<Integer> GetNumberActivePredicates (String precondition) {
        precondition = precondition.replaceAll("(\\(|\\)|&|~|\\?)", " ").trim();
        String[] nameOfPredicates = precondition.split(" ");
        List <Integer> positions = new ArrayList<>();

        for (String name : nameOfPredicates) {
            if (!name.isEmpty())
                positions.add(serialNumber.get(name) - 1);
        }
        return positions;
    }

    private  List <String> ParseFormula(String formula) {
        List <String> binarySetOfPredicates = new ArrayList<>();
        binarySetOfPredicates.addAll(physicalStates);
        formula = formula.replaceAll("(\\(|\\)|&| )", " ").trim();
        String[] tokens = formula.split(" ");

        int index;
        char operation;

        for (String token : tokens) {
            if (! token.isEmpty()) {
                operation = token.charAt(0);
                switch (operation) {
                    case '~':
                        index = serialNumber.get(token.substring(1));
                        binarySetOfPredicates.removeAll(GetCollectionIrrelevantSet(index, binarySetOfPredicates, '0'));
                        break;
                    case '?':
                        break;
                    default:
                        index = serialNumber.get(token.substring(0));
                        binarySetOfPredicates.removeAll(GetCollectionIrrelevantSet(index, binarySetOfPredicates, '1'));

                }
            }
       }
       return binarySetOfPredicates;
    }

    private  List <String> GetCollectionIrrelevantSet(int index, List <String> binarySetOfPredicates, char valuePredicate) {
        // Добавить в список нерелевантных set'ов наборы с противоположным valuePredicate
        List <String> irrelevantSet = new ArrayList<String>();
        for (String set : binarySetOfPredicates) {
            if (set.charAt(index - 1) != valuePredicate) {
                irrelevantSet.add(set);
            }
        }
        return irrelevantSet;
    }

}