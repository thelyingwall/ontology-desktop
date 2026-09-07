# Ontology Java Desktop Application

Aplikacja desktopowa w języku **Java 21** służąca do pracy z ontologią RDF opisującą elementy przestrzeni miejskiej i relacje między nimi.

Projekt został opracowany w ramach pracy dyplomowej dotyczącej wykorzystania ontologii i języków zapytań semantycznych do wspierania nauki orientacji przestrzennej osób niewidomych.

## Technologie

* **Java 21**
* **Java Swing** – interfejs graficzny
* **Apache Jena 4.10.0** – obsługa RDF/OWL i SPARQL
* **Maven** – zarządzanie projektem i zależnościami
* **Lombok**

## Funkcjonalności

Aplikacja umożliwia:

* wczytywanie i zapisywanie ontologii RDF/XML,
* przeglądanie klas i instancji,
* wyszukiwanie danych za pomocą SPARQL,
* wyszukiwanie relacji między elementami ontologii,
* dodawanie, edycję i usuwanie instancji,
* dodawanie relacji między instancjami,
* obsługę współrzędnych GPS,
* eksport wyników do CSV,
* pomiar czasu wykonywania zapytań.

## Uruchomienie

Wymagana jest **Java 21** oraz **Maven**.

Kompilacja:

```bash
mvn clean compile
```

Uruchomienie:

```bash
mvn exec:java
```

## Ontologia

Aplikacja wykorzystuje ontologię zapisaną w formacie **RDF/XML** i przetwarza ją przy użyciu biblioteki Apache Jena.

Projekt zawiera również implementacje wyszukiwania relacji z wykorzystaniem **SPARQL** oraz bezpośredniego API Apache Jena, co umożliwia porównanie obu podejść.
