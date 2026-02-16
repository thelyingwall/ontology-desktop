package org.ontology.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndividualsByRelations {
    private String sourceIndividual;
    private String relation;
    private String targetIndividual;
}
