# PATHTest

We propose PATHTest, a metamorphic testing approach targeting the two fundamental features of graph querying: graph pattern matching and graph navigation, corresponding respectively to fixed-length pattern query and variable-length path query.
We currently support two mainstream graph query languages, Cypher and Gremlin. Since our approach targets the fundamental characteristics of graph query languages, it can be easily adapted to other query languages with only minimal implementation effort.
# Implementation
- **Cypher implementation**: [`PATHTest_cypher`](./PATHTest_cypher)
- **Gremlin implementation**: [`PATHTest_gremlin`](./PATHTest_gremlin)

## Bugs First Found by PATHTest
| Bug          | Link                                                                  | Status    | Type  |
|--------------|-----------------------------------------------------------------------|-----------|-------|
| neo4j1       | https://github.com/neo4j/neo4j/issues/13624                           | fixed     | error |
| neo4j2       | https://github.com/neo4j/neo4j/issues/13629                           | confirmed | error |
| neo4j3       | https://github.com/neo4j/neo4j/issues/13631                           | fixed     | error |
| neo4j4       | https://github.com/neo4j/neo4j/issues/13643                           | fixed     | logic |
| neo4j5       | https://github.com/neo4j/neo4j/issues/13648                           | fixed     | error |
| neo4j6       | https://github.com/neo4j/neo4j/issues/13662                           | confirmed | error |
| memgraph1    | https://github.com/memgraph/memgraph/issues/2863                      | confirmed | error |
| memgraph2    | https://github.com/memgraph/memgraph/issues/2864                      | confirmed | logic |
| memgraph3    | https://github.com/memgraph/memgraph/issues/2865                      | confirmed | logic |
| memgraph4    | https://github.com/memgraph/memgraph/issues/2866                      | confirmed | error |
| memgraph5    | https://github.com/memgraph/memgraph/issues/2872                      | confirmed | logic |
| memgraph6    | https://github.com/memgraph/memgraph/issues/2873                      | confirmed | logic |
| memgraph7    | https://github.com/memgraph/memgraph/issues/2874                      | confirmed | logic |
| memgraph8    | https://github.com/memgraph/memgraph/issues/2876                      | confirmed | logic |
| memgraph9    | https://github.com/memgraph/memgraph/issues/2877                      | confirmed | logic |
| agensgraph1  | https://github.com/skaiworldwide-oss/agensgraph/issues/726            | confirmed | error |
| agensgraph2  | https://github.com/skaiworldwide-oss/agensgraph/issues/729            | fixed     | error |
| agensgraph3  | https://github.com/skaiworldwide-oss/agensgraph/issues/728            | fixed     | logic |
| agensgraph4  | https://github.com/skaiworldwide-oss/agensgraph/issues/730            | reported  | error |
| agensgraph5  | https://github.com/skaiworldwide-oss/agensgraph/issues/731            | confirmed | error |
| agensgraph6  | https://github.com/skaiworldwide-oss/agensgraph/issues/732            | confirmed | error |
| agensgraph7  | https://github.com/skaiworldwide-oss/agensgraph/issues/733            | reported  | logic |
| agensgraph8  | https://github.com/skaiworldwide-oss/agensgraph/issues/734            | reported  | logic |
| agensgraph9  | https://github.com/skaiworldwide-oss/agensgraph/issues/735            | reported  | logic |
| agensgraph10 | https://github.com/skaiworldwide-oss/agensgraph/issues/736            | reported  | logic |
| nebula1      | https://github.com/vesoft-inc/nebula/issues/6053                      | confirmed | logic |
| nebula2      | https://github.com/vesoft-inc/nebula/issues/6054                      | confirmed | error |
| nebula3      | https://github.com/vesoft-inc/nebula/issues/6055                      | confirmed | logic |
| nebula4      | https://github.com/vesoft-inc/nebula/issues/6056                      | confirmed | logic |
| nebula5      | https://github.com/vesoft-inc/nebula/issues/6057                      | confirmed | error |
| janusgraph1  | [NewJanus1](https://github.com/vesoft-inc/nebula/issues/6053)         | reported  | logic |
| hugegraph1   | [NewHuge1](https://github.com/apache/incubator-hugegraph/issues/2929) | reported  | error |
| hugegraph2   | [NewHuge2](https://github.com/apache/incubator-hugegraph/issues/2930) | reported  | logic |
| hugegraph3   | [NewHuge3](https://github.com/apache/incubator-hugegraph/issues/2931) | reported  | logic |
| hugegraph4   | [NewHuge4](https://github.com/apache/incubator-hugegraph/issues/2935) | confirmed | error |
| tinkergraph1 | [NewTinker1](./tinkergraphLogicBug.md)                                | pending   | error |
| memgraph1    | [NewMem1](https://github.com/memgraph/memgraph/issues/3733)           | reported   | logic |
| memgraph2    | [NewMem2](https://github.com/memgraph/memgraph/issues/3734)           | reported   | error |
| memgraph3    | [NewMem3](https://github.com/memgraph/memgraph/issues/3735)           | reported   | logic |
| memgraph4    | [NewMem4](https://github.com/memgraph/memgraph/issues/3736)           | reported   | logic |
| memgraph5    | [NewMem5](https://github.com/memgraph/memgraph/issues/3737)           | reported   | logic |


## Bugs Found by PATHTest But Not The First
These bugs were identified by PATHTest, but after a thorough review, we found that they have already been reported. Therefore, we list them here separately.

| Bug        | Link                                                               | Status    | Type  |
|------------|--------------------------------------------------------------------|-----------|-------|
| hugegraph1 | [Huge1](https://github.com/apache/incubator-hugegraph/issues/2932) | reported     | error |
| hugegraph2 | [Huge2](https://github.com/apache/incubator-hugegraph/issues/2933) | reported | error |
| hugegraph3 | [Huge3](https://github.com/apache/incubator-hugegraph/issues/2934) | reported     | error |
| hugegraph4 | [Huge4](https://github.com/apache/incubator-hugegraph/issues/2936) | reported     | logic |
