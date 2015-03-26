package com.altopus.schedule;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

public class Successors {

    public Successors() {}

    // поиск начального состояния
    public Vertex BackwardSearchInit(String start, String finish) {
        List <Integer> activePosition = new ArrayList<Integer>();
        List <String> startSets = ConvertFromStringToSet(start, activePosition);
        List <String> finishSets = ConvertFromStringToSet(finish, activePosition);

        List <Action> usedActions =  new ArrayList<>();

        // начальное состояние не является подмножеством целевого
        if (!CompareSets(startSets, finishSets)){
            DFS(finishSets, usedActions);
        }
        for (Action usedAction : usedActions) {
             activePosition.addAll(usedAction.position);
        }
        DataBase.getInstance().activeActions = usedActions;

        Set<Integer> set = new HashSet<Integer>(activePosition);
        activePosition.clear();
        activePosition.addAll(set);
        return new Vertex(startSets, activePosition);
    }

    // поиск в глубину
    public void DFS(List <String> currentSets,  List <Action> usedActions){
        Iterator<Action> iterator = DataBase.getInstance().allActions.iterator();
        while (iterator.hasNext()) {
            Action element = iterator.next();
            if (element.effects.containsAll(currentSets) && !usedActions.contains(element)) {
                usedActions.add(element);
                DFS(new ArrayList<>(element.preconditions), usedActions);
            }
        }
    }

    private List <String> ConvertFromStringToSet(String nameOfPredicates,  List <Integer> activePosition ) {
        String [] listOfNamePredicates = nameOfPredicates.split(" ");
        List <String> list = new ArrayList<String>();
        list.addAll(DataBase.getInstance().physicalStates);

        for (int i = 0; i < listOfNamePredicates.length; i++) {
            if (listOfNamePredicates[i].charAt(0) == '~') {
                int index = DataBase.getInstance().serialNumber.get(listOfNamePredicates[i].substring(1));
                list.removeIf(l -> l.charAt(index - 1) != '0');
                activePosition.add(index - 1);
            }
            else if (listOfNamePredicates[i].charAt(0) != '?'){
                int index = DataBase.getInstance().serialNumber.get(listOfNamePredicates[i]);
                list.removeIf(l -> l.charAt(index - 1) != '1');
                activePosition.add(index - 1);
            }
        }
        return list;
    }

    private boolean CompareSets(List <String> start, List <String> finish) {
          return finish.containsAll(start);
    }

    // построение пути из действий
    public List<Vertex> CompoundPathSuccessors(Vertex sourceVertex, List <Vertex> usedVertex, DirectedMultigraph<Vertex, DefaultEdge> graph) {

        List<String> currentCollection = new ArrayList<String>();
        for (State state : sourceVertex.states) {
            currentCollection.add(state.stringState);
        }
        Iterator<Action> iterator = DataBase.getInstance().activeActions.iterator();
        while (iterator.hasNext()) {
            Action element = iterator.next();
            if (element.preconditions.containsAll(currentCollection) && !usedVertex.contains(sourceVertex)) {
                //List <Vertex> usedVertex1 = new ArrayList<>(usedVertex);
                currentCollection.clear();
                currentCollection.addAll(element.effects);
                State [] states = Vertex.ConvertFromStringToState(currentCollection);
                Vertex targetVertex = new Vertex(states);
                usedVertex.add(targetVertex);
                AddVertexAndEdge(graph, sourceVertex, targetVertex);
                CompoundPathSuccessors(targetVertex, usedVertex, graph);
            }
        }
        return usedVertex;
    }

    public List<Vertex> SearchQuestionSuccessors(Vertex sourceVertex, DirectedMultigraph<Vertex, DefaultEdge> graph){
        List <State> positiveStates = new ArrayList<State>();
        List <State> negativeStates = new ArrayList<State>();
        List<Vertex> usedVertex =  new ArrayList<>();

        for (Integer mismatchedPosition : sourceVertex.mismatchedPositions) {
            for (int j = 0; j < sourceVertex.states.length; j++) {
                if (sourceVertex.states[j].predicates[mismatchedPosition] == 1) {
                    positiveStates.add(sourceVertex.states[j]);
                } else {
                    negativeStates.add(sourceVertex.states[j]);
                }
            }

            Vertex targetVertex = new Vertex(positiveStates.toArray(new State[positiveStates.size()]));
            AddVertexAndEdge(graph, sourceVertex, targetVertex);
            usedVertex.add(targetVertex);
            //SearchQuestionSuccessors(targetVertex, graph);


            targetVertex = new Vertex(negativeStates.toArray(new State[negativeStates.size()]));
            AddVertexAndEdge(graph, sourceVertex, targetVertex);
            usedVertex.add(targetVertex);
            //SearchQuestionSuccessors(targetVertex, graph);


            positiveStates.clear();
            negativeStates.clear();
        }
        return usedVertex;
    }
    public void AddVertexAndEdge(DirectedMultigraph<Vertex, DefaultEdge> graph, Vertex source, Vertex target) {
        graph.addVertex(source);
        graph.addVertex(target);
        graph.addEdge(source, target);
    }

}
