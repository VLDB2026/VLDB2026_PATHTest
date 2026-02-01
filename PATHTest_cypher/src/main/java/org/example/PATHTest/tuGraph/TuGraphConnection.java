package org.example.PATHTest.tuGraph;

import org.example.PATHTest.common.query.GDSmithResultSet;
import org.example.PATHTest.cypher.CypherConnection;
import org.neo4j.driver.*;

import java.util.Arrays;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

/**
 * @ClassName TuGraphConnection
 * @Description TODO
 * date 2025/5/30 11:56
 * version V1.0
 **/
public class TuGraphConnection extends CypherConnection {
    private Driver driver;
    private TuGraphOptions options;

    public TuGraphConnection(Driver driver, TuGraphOptions options){
        this.driver = driver;
        this.options = options;
    }


    @Override
    public String getDatabaseVersion() throws Exception {
        //todo complete
        return "tuGraph";
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void executeStatement(String arg) throws Exception{
        Session session = driver.session(SessionConfig.forDatabase("default"));
        session.run(arg);
    }


    @Override
    public List<GDSmithResultSet> executeStatementAndGet(String arg) throws Exception{
        try ( Session session = driver.session() )
        {
            GDSmithResultSet resultSet = new GDSmithResultSet(session.run(arg));
            resultSet.resolveFloat();
            return Arrays.asList(resultSet);
        }
    }
    public static void main(String [] args) throws Exception {
        Driver driver = GraphDatabase.driver("bolt://127.0.0.1:7689", AuthTokens.basic("admin", "73@TuGraph"));
        //通过 driver 对象创建一个 Session，设置会话连接到特定的数据库，用于执行Cypher语句
        Session session = driver.session(SessionConfig.forDatabase("default"));
        //清空图项目，请不要轻易尝试，它会清空你选中的图项目的模型以及数据
        session.run("CALL db.dropDB()");
        //创建点模型
        session.run("CALL db.createVertexLabel('person', 'id' , 'id' ,INT32, false, 'name' ,STRING, false)");
        //创建边模型
        session.run("CALL db.createEdgeLabel('is_friend','[[\"person\",\"person\"]]')");
        //创建索引
        session.run("CALL db.addIndex(\"person\", \"name\", false)");
        //插入点数据
        session.run("create (n1:person {name:'jack',id:1}), (n2:person {name:'lucy',id:2})");
        //插入边数据
        session.run("match (n1:person {id:1}), (n2:person {id:2}) create (n1)-[r:is_friend]->(n2)");
        //查询点和边
        Result res = session.run("match (n)-[r]->(m) return n,r,m");
        //Parameterized Query
        String cypherQuery = "MATCH (n1:person {id:$id})-[r]-(n2:person {name:$name}) RETURN n1, r, n2";
        Result result1 = session.run(cypherQuery, parameters("id", 1, "name", "lucy"));
        while (result1.hasNext()) {
            org.neo4j.driver.Record record = result1.next();
            System.out.println("n1: " + record.get("n1").asMap());
            System.out.println("r: " + record.get("r").asMap());
            System.out.println("n2: " + record.get("n2").asMap());
        }
        //删除点数据
        session.run("match (n1:person {id:1}) delete n1");
        //删除边数据
        session.run("match (n1:person {id:1})-[r]-(n2:person{id:2}) delete r");
        //删除边模型
        session.run("CALL db.deleteLabel('edge', 'is_friend')");
        //删除点模型
        session.run("CALL db.deleteLabel('vertex', 'person')");
    }
}