package org.ontology.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;

import java.io.*;
import java.text.Collator;
import java.util.*;

import static org.ontology.service.PropertyKeys.NAMED_INDIVIDUAL;
import static org.ontology.service.PropertyKeys.TYPE;

public class AppService {

    private final Model model;
    private String baseUri;
    private String baseName;
    private String prefixBase;
    private final String prefixRDF = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
    private final String prefixOWL = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>";

    public AppService(String ontologyPath) {
        model = ModelFactory.createDefaultModel();

        try (InputStream in = FileManager.get().open(ontologyPath)) {
            if (in == null) {
                throw new IllegalArgumentException("Nie znaleziono pliku: " + ontologyPath);
            }
            model.read(in, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ontologyQueryExample() {
        String queryStr = """
                SELECT ?s ?p ?o 
                WHERE { ?s ?p ?o }
                LIMIT 10
                """;

        Query query = QueryFactory.create(queryStr);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(results);
        }
    }

    public List<String> getClasses() {
        String queryStr = prefixRDF + prefixOWL + """
        SELECT ?class
        WHERE {
            ?class rdf:type owl:Class .
        }
        ORDER BY ?class
        """;

        Query query = QueryFactory.create(queryStr);

        List<String> classNames = new ArrayList<>();

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                String clsUri = sol.getResource("class").getURI();
                String localName = clsUri.substring(clsUri.lastIndexOf('#') + 1);
                classNames.add(localName);

                if (baseUri == null) {
                    baseUri = clsUri.substring(0, clsUri.indexOf('#'));
                    baseName = baseUri.substring(baseUri.lastIndexOf('/') + 1);
                    prefixBase = "PREFIX " + baseName + ": <" + baseUri + "#>";
                }
            }
        }
        Collator collator = Collator.getInstance(new Locale("pl", "PL"));
        collator.setStrength(Collator.PRIMARY);
        Collections.sort(classNames, collator);
        return classNames;
    }

    public List<String> getInstancesOfClass(String selectedClass) {
        List<String> instances = new ArrayList<>();
        String classUri = baseUri + "#" + selectedClass;

        String queryStr = prefixRDF + """
            SELECT ?instance
            WHERE {
                ?instance rdf:type <%s> .
            }
            ORDER BY ?instance
            """.formatted(classUri);

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                String instanceUri = sol.getResource("instance").getURI();
                String localName = instanceUri.substring(instanceUri.lastIndexOf('#') + 1);
                instances.add(localName);
            }
        }
        return instances;
    }

    public String getGpsCoordinatesOfInstance(String instanceName) {
        String instanceUri = baseUri + "#" + instanceName;

        String queryStr = prefixRDF + prefixBase +"""
        SELECT ?gps
        WHERE {
            <%s> %s:location_gps_coordinates ?gps .
        }
        """.formatted(instanceUri, baseName);

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            if (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                return sol.getLiteral("gps").getString();
            }
        }
        return null;
    }

    public Map<String, String> getPropertiesOfInstance(String instanceName) {

        String queryStr = prefixRDF + prefixBase + """
        SELECT ?property ?value
        WHERE {
            %s:%s ?property ?value .
        }
        """.formatted(baseName, instanceName);

        Query query = QueryFactory.create(queryStr);
        Map<String, String> result = new LinkedHashMap<>();

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                String property = sol.get("property").toString();
                property =  property.substring(property.lastIndexOf('#') + 1);
                String value = sol.get("value").toString();
                value = value.replace(baseUri + "#","");

                if (!property.equals("type"))
                    result.put(property, value);
            }
        }
        return result;
    }

    public Boolean saveInstance(Map<String, String> properties) {
        try {
            String NS = baseUri + "#";

            String instanceName = properties.get(NAMED_INDIVIDUAL);
            Resource individual = model.createResource(NS + instanceName);

            String typeName = properties.get(TYPE);
            if (typeName != null) {
                Resource typeResource = model.createResource(NS + typeName);
                individual.addProperty(RDF.type, typeResource);
            }

            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.equals(NAMED_INDIVIDUAL) || key.equals(TYPE)) continue;
                individual.addProperty(model.createProperty(NS + key), value);
            }

//            try (FileOutputStream out = new FileOutputStream("src/main/resources/ontology.rdf")) {
//                model.write(out, "RDF/XML-ABBREV");
//            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String decapitalize(String str) {
        if (str == null || str.isEmpty()) return str;

        if (str.equals(str.toUpperCase())) {
            return str.toLowerCase();
        }

        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public boolean saveFile(File file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            model.write(out, "RDF/XML-ABBREV");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadFile(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            model.removeAll();
            model.read(in, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean noLocalization(String selectedClass) {
        List<String> abstractClasses = List.of(
                Classes.Bike.toString(),
                Classes.Bus.toString(),
                Classes.Car.toString(),
                Classes.ElementsOfCityArchitecture.toString(),
                Classes.GPSCoordinates.toString(),
                Classes.UserAvatar.toString(),
                Classes.Motorcycle.toString(),
                Classes.Vehicle.toString(),
                Classes.Pavement.toString()
        );
        return abstractClasses.contains(selectedClass);
    }

    public boolean isAbstractClass(String selectedClass) {
        List<String> abstractClasses = List.of(
                Classes.Bike.toString(),
                Classes.Bus.toString(),
                Classes.Car.toString(),
                Classes.ElementsOfCityArchitecture.toString(),
                Classes.GPSCoordinates.toString(),
                Classes.UserAvatar.toString(),
                Classes.Motorcycle.toString(),
                Classes.Vehicle.toString()
        );
        return abstractClasses.contains(selectedClass);
    }

    public void deleteInstance(String instance) {
        String uri = baseUri + "#" + instance;
        Resource individual = model.getResource(uri);

        if (model.containsResource(individual)) {
            model.removeAll(individual, null, null);
            model.removeAll(null, null, (RDFNode) individual);
        } else {
            System.out.println("Indywiduum nie istnieje w modelu: " + uri);
        }
    }

}
