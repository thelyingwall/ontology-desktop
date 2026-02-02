package org.ontology.service;

public enum PropertyType {
    type,
    NamedIndividual,
    location_gps_coordinates,
    comment,
    address_street_name,
    address_building_number,
    address_city_name,
    address_country,
    address_post_code,
    address_province,
    address_telefone,
    realEstate_name,


    //relations
    hasDirectNeighbor,
    isLocatedOn,
    hasLocation,
    recordedInTheLocation,
}
