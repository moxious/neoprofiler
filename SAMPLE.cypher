// This is a sample cypher file intended to create a trivial small
// database for testing purposes.
//
// The sample database contains a few individuals, and a few relationships
// amongst them, for the profiler to discover and report on.

create (p:Person {name:"Bob", age:35, gender:"M" })
       -[:KNOWS]->(m:Person {name:"Susan", age:43, gender:"F"});
create (p:Person {name:"Sam", age:23, gender:"M" })
       -[:KNOWS]->(m:Person {name:"Michael", age:22, gender:"M"});

match (boss:Person {name:"Susan"}), (emp:Person {name:"Sam"}),
      (emp2:Person {name:"Michael"})
    create emp-[:WORKS_FOR]->boss, emp2-[:WORKS_FOR]->boss;

