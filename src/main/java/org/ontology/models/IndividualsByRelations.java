package org.ontology.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Reprezentuje pojedynczą relację RDF wraz z jej źródłowym i docelowym indywiduum.
 */
@Data
@AllArgsConstructor
public class IndividualsByRelations {
    private String sourceIndividual;
    private String relation;
    private String targetIndividual;
}
