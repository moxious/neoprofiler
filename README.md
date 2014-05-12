## Synopsis

This package is a lightweight set of profiling utilities for Neo4J databases.
This is intended to quickly help users derive the rough structure and
population of a new Neo4J database.

## Quick Start

**Clone the repo**: `git clone https://github.com/moxious/neoprofiler.git`

**Package the application**:  `mvn package`

Note that packaging includes the creation of a runnable "One JAR" which includes all necessary dependencies.

**Run from the command line**: 
`java -jar target/neoprofiler-0.1.one-jar.jar -db path_to_db -format html -output db_profile_report.html`

The argument given to the application should be the path to a directory containing a neo4j database.

## Usage

```
usage: NeoProfiler
 -db <db>           Path to directory where neo4j database is located
 -format <format>   Output format: valid values are json, markdown, or
                    html
 -output <output>   Name of output file to write; program will print to
                    console if not specified.
```

## Motivation

If you work with many Neo4J databases, it's often convenient to get a
high-level picture of what is in a particular dataset before you jump in.  If
you write application code which interacts with a Neo4J database, it's good to
be able to verify that the population of nodes and relationships you're writing
have the properties that you expect.

## Output Example

The program knows how to output JSON, Markdown, and HTML as output.   To see
a sample HTML report, download [sample-report.html](sample-report.html) and
view it in a browser.

## Metagraphs

One of the things that the profiler outputs is what we call a "metagraph".  Neo4J
database structures themselves can be described as a graph.  The nodes in the metagraph
describe what kinds of labeled nodes, relationships, and so on are in your database.
The relationships in the metagraph outline how these things connect.

An example and picture of a metagraph can be [found here](https://imgur.com/qsPvQFY).  
A longer discussion of metagraphs can be [found here](http://gist.neo4j.org/?8640853).

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



