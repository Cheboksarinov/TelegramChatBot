package com.altopus.planner.convert;

import com.google.gson.internal.LinkedTreeMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    private ObjectsJson objectsJson;
    public Converter(ObjectsJson objectsJson){
       this.objectsJson = objectsJson;
    }

    private void ParserPrediction(List<String> namePredicates, List<String> sentence, List<String> regSentence) {

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

    private void ParserAction(List<String> nameActions, List<String> sentence, List<String> regSentence) {

        for (String nameAction : objectsJson.Actions.keySet()) {
            nameActions.add(nameAction);
            ArrayList <String> link = objectsJson.Actions.get(nameAction);

            sentence.add(link.get(0));
            regSentence.add(link.get(1));
        }
    }

    private static void printAiml(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("predicates_eng.aiml")));
        }
        catch (Exception e) {
        }
    }

    private void CreateAction2lingTxt(List<String> nameActions, List<String> sentence) {

        PrintWriter action2ling = null;

        try {
            action2ling = new PrintWriter(new FileOutputStream("maps\\action2ling.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        int count = 0;
        while (count<nameActions.size()) {
            action2ling.println(nameActions.get(count) + " : " + sentence.get(count));
            count++;
        }

        action2ling.close();
    }

    private void CreateLogic2lingTxt(List<String> namePredicates, List<String> sentence) {

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

    private void CreateActionTxt(List<String> nameActions) {

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

    private Document CreateDocument()
    {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.newDocument();
        }
        catch (Exception e) {
            return null;
        }
    }

    public void ToAiml() {

        ParserPrediction(objectsJson.namePredicates, objectsJson.sentencesPredicates, objectsJson.regSentencePredicates);
        ParserAction(objectsJson.nameActions, objectsJson.sentencesAction, objectsJson.regSentenceAction);

        Document doc = CreateDocument();
        Element root = doc.createElement("aiml");
        doc.appendChild(root);

        int count = 0;

        for (String name : objectsJson.namePredicates) {
            for (int i = count; i < count + 4; i += 2) {
                Element category = doc.createElement("category");
                Element pattern = doc.createElement("pattern");
                Element template = doc.createElement("template");
                Element regexp = doc.createElement("regexp");
                Element srai = doc.createElement("srai");

                regexp.appendChild(doc.createTextNode(objectsJson.regSentencePredicates.get(i + 1)));

                pattern.appendChild(doc.createTextNode("PRED"));
                pattern.appendChild(regexp);

                srai.appendChild(doc.createTextNode("VPRED " + name + " " + objectsJson.regSentencePredicates.get(i)));
                template.appendChild(srai);

                category.appendChild(pattern);
                category.appendChild(template);

                root.appendChild(category);
            }

            count += 4;
        }
        count = 0;
        while (count < objectsJson.nameActions.size()) {
            Element category = doc.createElement("category");
            Element pattern = doc.createElement("pattern");
            Element template = doc.createElement("template");
            Element regexp = doc.createElement("regexp");
            Element srai = doc.createElement("srai");

            regexp.appendChild(doc.createTextNode(objectsJson.regSentenceAction.get(count)));

            pattern.appendChild(doc.createTextNode("ACTN"));
            pattern.appendChild(regexp);

            srai.appendChild(doc.createTextNode(objectsJson.nameActions.get(count)));
            template.appendChild(srai);

            category.appendChild(pattern);
            category.appendChild(template);

            root.appendChild(category);
            count++;
        }

        printAiml(doc);

        CreateAction2lingTxt(objectsJson.nameActions, objectsJson.sentencesAction);
        CreateLogic2lingTxt(objectsJson.namePredicates, objectsJson.sentencesPredicates);
        CreateActionTxt(objectsJson.nameActions);
    }
}