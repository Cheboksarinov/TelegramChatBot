package com.altopus.schedule;

import orbital.logic.imp.Formula;
import orbital.logic.imp.Logic;
import orbital.logic.sign.ParseException;
import orbital.moon.logic.ClassicalLogic;
import orbital.moon.logic.resolution.ClausalSet;
import orbital.moon.logic.resolution.Clause;
import orbital.moon.logic.resolution.DefaultClausalFactory;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.*;

public class PropositionalSolver {
    Logic logic;

    Map transform;
    Map inverseTransform;

    Formula targetFormula;
    ISolver solver;


    public PropositionalSolver()
    {
        logic = new ClassicalLogic(ClassicalLogic.PROPOSITIONAL_INFERENCE);
    }

    private ISolver SAT4JInitializer(String axiomFormula, String[] ListOfPredicates) throws ParseException, ContradictionException {
        solver = SolverFactory.newDefault();
        solver.setTimeout(3600);
        solver.newVar(ListOfPredicates.length);


        targetFormula = (Formula) logic.createExpression(axiomFormula);
        Formula CNFForm = ClassicalLogic.Utilities.conjunctiveForm(targetFormula);

        DefaultClausalFactory tt = new DefaultClausalFactory();
        ClausalSet clausalSet = tt.asClausalSet((CNFForm));

        solver.setExpectedNumberOfClauses(clausalSet.size());

        //System.out.println("Count of variables: "+ListOfPredicates.length);
        //System.out.println("Count of clauses: " + clausalSet.size());

        //Теперь пойдем по каждому из clause'сов:
        Iterator separateClause = clausalSet.iterator();
        while (separateClause.hasNext()) {
            Object clause = separateClause.next();
            Vector clauseArray = new Vector();
            Iterator resolvableLiterals = ((Clause)clause).getResolvableLiterals();
            while (resolvableLiterals.hasNext()) {
                Object currentElement = resolvableLiterals.next();
                clauseArray.add(transform.get(currentElement.toString()));
            }
            Set probableLiterals = ((Clause)clause).getUnifiables(CNFForm);


            //System.out.println("First element: "+clauseArray.get(1));
            int h00ita[] = new int[clauseArray.size()];
            for (int i=0; i<clauseArray.size(); i++) {
                h00ita[i] = (Integer) clauseArray.get(i);
            }
            solver.addClause(new VecInt(h00ita));
        }
        return solver;
    }

    private void createTransformatorsFromListOfPredicates(String[] listOfNamePredicates) {
        transform = new HashMap();
        inverseTransform = new HashMap();

        for (int i=0; i < listOfNamePredicates.length; i++) {
            transform.put(listOfNamePredicates[i].toString(), (i+1));
            transform.put(("~"+listOfNamePredicates[i].toString()), -(i+1));

            inverseTransform.put((i+1), listOfNamePredicates[i].toString());
            inverseTransform.put(-(i+1), "~"+listOfNamePredicates[i].toString());
        }

    }

    private int[] conversionNamesAndValuesPredicatesToConditionWithTarget (String[] listOfNamesPredicates,
                                                                           String[] valuesOfPredicates,
                                                                           String targetPredicate,
                                                                           String targetValue)
    {
        final String trueRepresentation = "true";
        final String falseRepresentation = "false";
        final String unknownRepresentation = "unknown";

        Vector<Integer> listOfConditions = new Vector<Integer>();
        if (listOfNamesPredicates.length != valuesOfPredicates.length)
        {
            return null;
        }
        for (int i = 0; i < listOfNamesPredicates.length; i++)
        {
            if (listOfNamesPredicates[i].equals(targetPredicate))
            {
                int val = (Integer)transform.get(listOfNamesPredicates[i]);
                if (targetValue.equals(falseRepresentation)) {
                    val = val*(-1);
                }
                listOfConditions.add(val);
            } else if (!valuesOfPredicates[i].equals(unknownRepresentation)) {
                if (!valuesOfPredicates[i].equals(unknownRepresentation)){
                    int val = (Integer)transform.get(listOfNamesPredicates[i]);
                    if (valuesOfPredicates[i].equals(falseRepresentation))
                    {
                        val = val*(-1);
                    }
                    listOfConditions.add(val);
                }
            }
        }
        int[] conditions = new int[listOfConditions.size()];
        for (int i=0; i < listOfConditions.size(); i++)
        {
            conditions[i] = listOfConditions.elementAt(i);
        }
        return conditions;
    }

    private int[] conversionNamesAndValuesPredicatesToConditions(String[] listOfNamesPredicates, String[] valuesOfPredicates)
    {
        final String trueRepresentation = "true";
        final String falseRepresentation = "false";
        final String unknownRepresentation = "unknown";

        Vector<Integer> listOfConditions = new Vector<Integer>();
        if (listOfNamesPredicates.length != valuesOfPredicates.length)
        {
            return null;
        }
        for (int i=0; i<listOfNamesPredicates.length; i++) {
            if (!valuesOfPredicates[i].equals(unknownRepresentation)){
                int val = (Integer)transform.get(listOfNamesPredicates[i]);
                if (valuesOfPredicates[i].equals(falseRepresentation))
                {
                    val = val*(-1);
                }
                listOfConditions.add(val);
            }
        }
        int [] conditions = new int[listOfConditions.size()];
        for (int i=0; i<listOfConditions.size(); i++)
        {
            conditions[i] = listOfConditions.elementAt(i);
        }
        return conditions;
    }

    private String substituteString (String axiom) {
        String newAxiom = new String();
        return newAxiom;
    }

    public boolean SATCall(String nameOfPredicates, String valueOfPredicates, String axiom) throws ParseException, ContradictionException, TimeoutException {

        //Проводим сепарацию по пробелам
        String [] listOfNamePredicates = nameOfPredicates.split(" "); //Массив имен предикатов
        String [] valuesOfPredicates = valueOfPredicates.split(" "); // Массив значений предикатов

        createTransformatorsFromListOfPredicates(listOfNamePredicates);

        //nameOfPredicates.split(" ").length;
        ISolver solverRR = SAT4JInitializer(axiom, listOfNamePredicates);
        int[] conditionForThisTask =  conversionNamesAndValuesPredicatesToConditions(listOfNamePredicates, valuesOfPredicates);
        return solver.isSatisfiable(new VecInt(conditionForThisTask));
    }

    public boolean SATInference(String nameOfPredicates, String valueOfPredicates, String axiom,
                                String targetPredicate, String targetValue) throws ParseException, ContradictionException, TimeoutException {
        //Проводим сепарацию по пробелам
        String [] listOfNamePredicates = nameOfPredicates.split(" "); //Массив имен предикатов
        String [] valuesOfPredicates = valueOfPredicates.split(" "); // Массив значений предикатов


        createTransformatorsFromListOfPredicates(listOfNamePredicates);
        ISolver solveRR = SAT4JInitializer(axiom, listOfNamePredicates);

        int [] trueCondition = conversionNamesAndValuesPredicatesToConditionWithTarget(listOfNamePredicates,
                valuesOfPredicates, targetPredicate, targetValue);
        boolean resultOfSATIfTrue = solver.isSatisfiable(new VecInt(trueCondition));
        return resultOfSATIfTrue;
    }

    public boolean CheckIfTargetVariableInListOfNamePredicates(String nameOfPredicates, String targetPredicate)
    {
        String [] listOfNamePredicates = nameOfPredicates.split(" ");
        for (int i=0; i<listOfNamePredicates.length; i++) {
            if (listOfNamePredicates[i].equals(targetPredicate)) {
                return true;
            }
        }
        return false;
    }
}
