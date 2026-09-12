package org.ontology.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Łączy wyniki wyszukiwania relacji z czasem wykonania zapytania.
 */
@Data
@AllArgsConstructor
public class SearchedIndividualsRelations {
    private List<IndividualsByRelations> relations;
    private String time;
}
