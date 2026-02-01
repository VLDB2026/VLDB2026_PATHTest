package org.example.PATHTest.janusGraph;

import org.apache.tinkerpop.gremlin.driver.MessageSerializer;
import org.apache.tinkerpop.gremlin.driver.ser.GraphBinaryMessageSerializerV1;
import org.apache.tinkerpop.gremlin.structure.io.binary.GraphBinaryMapper;
import org.example.PATHTest.common.query.GDSmithResultSet;
import org.example.PATHTest.cypher.CypherConnection;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.opencypher.gremlin.client.CypherGremlinClient;
import org.opencypher.gremlin.translation.TranslationFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JanusConnection extends CypherConnection {


    private Cluster cluster;

    public JanusConnection(Cluster cluster){
        this.cluster = cluster;
    }

    public JanusConnection(){
    }


    @Override
    public String getDatabaseVersion() throws Exception {
        //todo complete
        return "";
    }

    @Override
    public void close() throws Exception {
        Client gremlinClient = cluster.connect();
        gremlinClient.submit("MATCH (n) DETACH DELETE n");
        gremlinClient.close();
        cluster.close();
    }

    @Override
    public void executeStatement(String arg) throws Exception{
        String cypher = arg;
        Client gremlinClient = cluster.connect();
        CypherGremlinClient translatingGremlinClient = CypherGremlinClient.translating(gremlinClient);
        String gremlin = (new TranslationFacade()).toGremlinGroovy(cypher);
        System.out.println(gremlin);
        translatingGremlinClient.submit(cypher).all();
    }

    @Override
    public List<GDSmithResultSet> executeStatementAndGet(String arg) throws Exception{
        String cypher = arg;
        Client gremlinClient = cluster.connect();
        CypherGremlinClient translatingGremlinClient = CypherGremlinClient.translating(gremlinClient);
        String gremlin = (new TranslationFacade()).toGremlinGroovy(cypher);
        System.out.println(gremlin);
        return Arrays.asList(new GDSmithResultSet(translatingGremlinClient.submit(cypher).all()));
    }

    public static void main(String[] args) throws Exception {
        // 设置正确的 serializer
        MessageSerializer<GraphBinaryMapper> serializer = new GraphBinaryMessageSerializerV1();
        Map<String, Object> config = new HashMap<>();
        //config.put(TOKEN_SERIALIZE_RESULT_TO_STRING, true);
        serializer.configure(config, null);

        Cluster cluster = Cluster.build()
                .addContactPoint("localhost")
                .port(8182)
                .serializer(serializer)
                .credentials("janusgraph", "janusgraph_2025")
                .create();
        JanusConnection con = new JanusConnection(cluster);
        Client gremlinClient = cluster.connect();
        String cypher = "MATCH(n) RETURN n";
        System.out.println(cypher.substring(0));
        CypherGremlinClient translatingGremlinClient = CypherGremlinClient.translating(gremlinClient);
        String gremlin = (new TranslationFacade()).toGremlinGroovy(cypher);
        System.out.println(gremlin);
        Arrays.asList(new GDSmithResultSet(translatingGremlinClient.submit(cypher).all()));
    }
}
