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


