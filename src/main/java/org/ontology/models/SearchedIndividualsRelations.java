package org.ontology.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchedIndividualsRelations {
    private List<IndividualsByRelations> relations;
    private String time;
}
