## Synopsis

This package is a lightweight set of profiling utilities for Neo4J databases.
This is intended to quickly help users derive the rough structure and
population of a new Neo4J database.

## Quick Start

**Clone the repo**: `git clone https://github.com/moxious/neoprofiler.git`

**Package the application**:  `mvn package`

**Run from the command line**: `java -jar target/neoprofiler-0.1.one-jar.jar <path-to-neo4j-database>`

The argument given to the application should be the path to a directory containing a neo4j database.

## Motivation

If you work with many Neo4J databases, it's often convenient to get a
high-level picture of what is in a particular dataset before you jump in.  If
you write application code which interacts with a Neo4J database, it's good to
be able to verify that the population of nodes and relationships you're writing
have the properties that you expect.

## Output Example

Currently, neoprofiler outputs a JSON object as its result, which contains a
profile of the database.  

The source distribution contains a file called [SAMPLE.cypher](SAMPLE.cypher) with a few
simple statements to create a sample database.  The output below corresponds
to what the program creates when run on that sample database.

Code is under development, and so this output format is not yet stable; as
new features are added, changes are likely to occur.

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

## FAQ

### How does it work?

The program contains a number of profilers, most of which run very simple
Cypher queries against your database and provide summary statistics.  Some
profilers will actually discover data in your graph and then spawn other
profilers which will run later.  For example, if a label called "Person" is
discovered in the data, a label profiler will be added to the run queue to 
inspect the population of nodes with that label.

Right now, the focus is on simple queries that return descriptive statistics.

Additionally, profiling right now must occur while the database is offline,
because the profiler opens the database in embedded mode.

### Can I profile a database available via a URL?

No, not yet - but this is part of the plan, and is in the works.

### Will the profiler modify my database?

Absolutely not.  All queries are read-only; any storage that is needed will
happen in memory or via another method, to guarantee no modifications are
made to the database being profiled.



