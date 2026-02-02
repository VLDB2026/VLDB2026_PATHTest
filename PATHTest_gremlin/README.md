# PATHTest_gremlin

PATHTest_gremlin is a metamorphic testing approach targets gremlin-based graph database management systems based on [Grand](https://github.com/choeoe/Grand).
 
# Getting Started

## Requirements

- Java 11
- [Maven](https://maven.apache.org/)
- The graph database engines that you want to test (now supporting JanusGraph, HugeGraph, TinkerGraph)


## Setting GDBs

1. Download the GDBs from official websites. Specifically, find the gremlin-server.yaml in each GDB, and replace the port number in it following the *.yaml file in conf directory we offer. For example, the port number of JanusGraph is 8185.
2. Starting GDBs.

## Running PATHTest_gremlin


1. In src/main/java/org/gdbtesting/Starter.java file, start the project.

2. The parameters in our Starter.java are as follows. You can modify our script and change these parameters as needed.
- `QueryDepth`, the max length of the query we generated, e.g, 5.
- `VerMaxNum`, the maximum number of the Vertex in the generated graph, e.g., 100.
- `EdgeMaxNum`, the maximum number of the Edge in the generated graph, e.g., 200.
- `EdgeLabelNum`, the maximum number of the edge label in the generated graph, e.g., 20.
- `VerLabelNum`, the maximum number of the vertex label in the generated graph, e.g., 10.
- `QueryNum`, the number of query generated in a test round, e.g., 10.
- `RepeatTimes`, the test rounds, e.g., 1.

3. By setting the Java VM option -Dperiod=\*, the experimental results will be saved in the log-\* directory. The schema creation statements will be saved in the `log` folder for easier and faster reproduction, as HugeGraph requires the schema to be created first.

```Note: You need to first create the `log` folder and `log-*` folders before executing.```

## Credit
Some code is based on the [Grand](https://github.com/choeoe/Grand) project.





