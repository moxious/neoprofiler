## Synopsis

This package is a lightweight set of profiling utilities for Neo4J databases.
This is intended to quickly help users derive the rough structure and
population of a new Neo4J database.

## Quick Start

Install one dependency:
 - `git clone https://github.com/neo4j/java-rest-binding.git`
 - `cd java-rest-binding ; mvn install`

Clone the repo: `git clone https://github.com/moxious/neoprofiler.git`

Package the application:  `mvn package`

Run from the command line: `java -jar target/neoprofiler-0.1.one-jar.jar <path-to-neo4j-database>`

The argument given to the application should be the path to a directory containing a neo4j database.

## Output

Currently, neoprofiler outputs a JSON object as its result, which contains a
profile of the database.  

The source distribution contains a file called `SAMPLE.cypher` with a few
simple statements to create a sample database.  The output below corresponds
to what the program creates when run on that sample database.

```
{
  "name": "NeoProfile of src/main/resources/sample.db/ generated 2014/05/06 08:29:57",
  "description": "src/main/resources/sample.db/",
  "observations": {},
  "profiles": [
    {
      "constraints": [],
      "name": "SchemaProfile",
      "description": "Information about Neo4J\u0027s database schema",
      "observations": {
        "Run Time (ms)": 2
      }
    },
    {
      "name": "NodesProfile",
      "description": "Summary statistics about nodes in the graph",
      "observations": {
        "Total Nodes": 4,
        "Node Labels": [
          [
            "Person"
          ]
        ],
        "Run Time (ms)": 1030
      }
    },
    {
      "name": "RelationshipsProfile",
      "description": "Summary statistics about nodes in the graph",
      "observations": {
        "Total Relationships": 4,
        "Available Relationship Types": [
          "KNOWS",
          "WORKS_FOR"
        ],
        "Run Time (ms)": 136
      }
    },
    {
      "params": {
        "label": "Person"
      },
      "name": "NodeProfile",
      "description": "Profile of nodes labeled \u0027Person\u0027",
      "observations": {
        "Total nodes": "4",
        "Sample properties": [
          {
            "name": "age",
            "type": "Long"
          },
          {
            "name": "gender",
            "type": "String"
          },
          {
            "name": "name",
            "type": "String"
          }
        ],
        "Outbound relationship types": [
          "KNOWS",
          "WORKS_FOR"
        ],
        "Run Time (ms)": 113,
        "Inbound relationship types": [
          "KNOWS",
          "WORKS_FOR"
        ]
      }
    },
    {
      "params": {
        "type": "KNOWS"
      },
      "name": "RelationshipTypeProfile",
      "description": "Profile of relationships of type \u0027KNOWS\u0027",
      "observations": {
        "Total relationships": "2",
        "domain": [
          "Person"
        ],
        "range": [
          "Person"
        ],
        "Run Time (ms)": 69
      }
    },
    {
      "params": {
        "type": "WORKS_FOR"
      },
      "name": "RelationshipTypeProfile",
      "description": "Profile of relationships of type \u0027WORKS_FOR\u0027",
      "observations": {
        "Total relationships": "2",
        "domain": [
          "Person"
        ],
        "range": [
          "Person"
        ],
        "Run Time (ms)": 59
      }
    }
  ]
}
```
