package org.ontology.service;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.*;
import java.text.Collator;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.jena.vocabulary.OWL2.NamedIndividual;
import static org.ontology.service.PropertyKeys.NAMED_INDIVIDUAL;
import static org.ontology.service.PropertyKeys.TYPE;

public class AppService {

    private final Model model;
    private String baseUri;
    private String baseName;
    private String prefixBase;
    private final String prefixRDF = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
    private final String prefixOWL = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>";
    private static final Pattern COORD_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

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

    public List<String> getAllInstances() {
        List<String> instances = new ArrayList<>();

        // dodaj prefiksy do zapytania
        String queryStr = """
        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        SELECT ?instance
        WHERE {
            ?instance rdf:type owl:NamedIndividual .
        }
        ORDER BY ?instance
        """;

        Query query = QueryFactory.create(queryStr);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                Resource res = sol.getResource("instance");

                String name;
                if (res.isURIResource()) {
                    String instanceUri = res.getURI();
                    name = instanceUri.substring(instanceUri.lastIndexOf('#') + 1);
                } else {
                    name = res.getId().getLabelString();
                }

                instances.add(name);
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

    public void updateInstance(String instanceName, Map<String, String> properties) {
        String NS = baseUri + "#";

        Resource instance = model.getResource(NS + instanceName);
        if (!model.containsResource(instance)) {
            instance = model.createResource(NS + instanceName);
        }

        String typeName = properties.get("type");
        if (typeName != null && !typeName.isBlank()) {
            Resource classResource = model.createResource(NS + typeName);
            model.removeAll(instance, RDF.type, classResource);
            instance.addProperty(RDF.type, classResource);
        }

        for (String key : properties.keySet()) {
            if (key.equals("type")) continue;
            Property prop = model.createProperty(NS + key);
            model.removeAll(instance, prop, null);
        }

        for (Map.Entry<String, String> e : properties.entrySet()) {
            String key = e.getKey();
            if (key.equals("type")) continue;
            String value = e.getValue();
            if (value != null && !value.isBlank()) {
                Property prop = model.createProperty(NS + key);
                if (key.equals("comment")) {
                    instance.addProperty(RDFS.comment, value);
                } else {
                    instance.addProperty(prop, value);
                }
            }
        }
        //todo poprawic
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
        // todo dodac usuwanie relacji
    }

    public boolean isValidLatitude(String value) {
        if (!COORD_PATTERN.matcher(value).matches()) {
            return false;
        }

        double lat = Double.parseDouble(value);
        return lat >= -90 && lat <= 90;
    }

    public boolean isValidLongitude(String value) {
        if (!COORD_PATTERN.matcher(value).matches()) {
            return false;
        }

        double lon = Double.parseDouble(value);
        return lon >= -180 && lon <= 180;
    }

//    public boolean addNewRelation(String selectedIndividual1, String selectedRelationType, String selectedIndividual2) {
//        return true;
//    }

    public boolean addNewRelation(String selectedIndividual1, String selectedRelationType, String selectedIndividual2) {
        try {
            String NS = baseUri + "#";

            // Pobieramy lub tworzymy indywiduum1
            Resource individual1 = model.containsResource(model.getResource(NS + selectedIndividual1))
                    ? model.getResource(NS + selectedIndividual1)
                    : model.createResource(NS + selectedIndividual1);

            // Pobieramy lub tworzymy indywiduum2
            Resource individual2 = model.containsResource(model.getResource(NS + selectedIndividual2))
                    ? model.getResource(NS + selectedIndividual2)
                    : model.createResource(NS + selectedIndividual2);

            // Tworzymy property dla relacji (predicate)
            Property relationProp = model.createProperty(NS + selectedRelationType);

            // Dodajemy trójkę RDF: indywiduum1 --relacja--> indywiduum2
            individual1.addProperty(relationProp, individual2);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
