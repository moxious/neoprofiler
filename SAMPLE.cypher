create (p:Person {name:"Bob", age:35, gender:"M" })
       -[:KNOWS]->(m:Person {name:"Susan", age:43, gender:"F"});
create (p:Person {name:"Sam", age:23, gender:"M" })
       -[:KNOWS]->(m:Person {name:"Michael", age:22, gender:"M"});

match (boss:Person {name:"Susan"}), (emp:Person {name:"Sam"}),
      (emp2:Person {name:"Michael"})
    create emp-[:WORKS_FOR]->boss, emp2-[:WORKS_FOR]->boss;

