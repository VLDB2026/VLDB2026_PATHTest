## Issue1: logic bug in TinkerGraph
```gremlin
g.V().has('vp3',0.62307286)
```
it returns:
```shell
empty result
```
+ file
  + 4TinkerGraph-create.txt
  + 4schema-out.txt
ref: When we execute the same query in hugeGraph, it returns the inserted node.

## Issue10: logic bug in HugeGraph
title:
- Server Version: 1.7.0
- Backend: RocksDB x nodes
- OS: 192 CPUs, 256 G RAM, Ubuntu 22.04
- Data Size:  50 vertices, 100 edges
### Expected behavior
For the two query below, the same result should be returned:
```gremlin
g.V().both('el2','el1','el3').inE('el2').where(__.bothV().count().is(outside(-1435889948263879801,-3366956858553110955))).count()

g.V().repeat(__.both('el2','el1','el3')).times(1).inE('el2').match(__.as('start').where(__.bothV().count().is(outside(-1435889948263879801,-3366956858553110955))).as('end')).select('end').count()
```
the first query returns: ```0```
the second query returns: ```99```

### Actual behavior
+ The first query returns: 0
+ The second query returns: 99
+ We replaced the ```both('el2','el1','el3')``` step in the first query with ```repeat(__.both('el2','el1','el3')).times(1)```, and moved the```where (...)``` step into the ```match()```step. Both of these operations should not affect the result.
+ When I executed these two queries on Janusgraph and Tinkerpop, they both returned the same results.
+ file
  + 4HugeGraph-create.txt
  + 4schema-out.txt

ref: When we execute the same query in JanusGraph and TinkerGraph, both queries in both GDBMSs returns 99.