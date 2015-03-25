package com.altopus.schedule;


import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.FileNotFoundException;

public class Planner {
    public DirectedMultigraph<Vertex, DefaultEdge> graph;
    public Successors successors;

    public Planner() {
        graph = new DirectedMultigraph<>(DefaultEdge.class);
        successors = new Successors();
    }

    public void DoPlan(String start, String finish) throws FileNotFoundException {
        DataBase.getInstance().MatchPredicateWithSerialNumber("map.txt");
        DataBase.getInstance().ParsePDDL("лампа_PDDL.txt");

        Vertex startVertex = successors.BackwardSearchInit(start, finish);

        /*List<Vertex> usedVertex = new ArrayList<>();

        List<Vertex> vertexs1 = new ArrayList<>();
        vertexs1.add(startVertex);
        Iterator iterator = vertexs1.iterator();
        while (iterator.hasNext()) {
            iterator.hasNext();
            for (Vertex v : vertexs1) {
                vertexs1.addAll(successors.SearchQuestionSuccessors(v, graph));
                vertexs1.addAll(successors.CompoundPathSuccessors(v, usedVertex, graph));
            }
        }*/

        System.out.println(graph.toString());
    }
}



