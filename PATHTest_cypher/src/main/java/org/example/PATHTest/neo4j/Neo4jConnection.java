package org.example.PATHTest.neo4j;

import org.example.PATHTest.common.query.GDSmithResultSet;
import org.example.PATHTest.cypher.CypherConnection;
import org.neo4j.driver.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class Neo4jConnection extends CypherConnection {


    private Driver driver;
    private Neo4jOptions options;

    public Neo4jConnection(Driver driver, Neo4jOptions options){
        this.driver = driver;
        this.options = options;
    }


    @Override
    public String getDatabaseVersion() throws Exception {
        //todo complete
        return "neo4j";
    }

    @Override
    public void close() throws Exception {
//        Neo4jDriverManager.closeDriver(driver);
    }

    @Override
    public void executeStatement(String arg) throws Exception{
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    tx.run(arg);
                    return "";
                }
            } );
            //System.out.println( greeting );
        }
    }


    @Override
    public List<GDSmithResultSet> executeStatementAndGet(String arg) throws Exception{
        try ( Session session = driver.session() )
        {
            GDSmithResultSet resultSet = new GDSmithResultSet(session.run(arg));
            resultSet.resolveFloat();
            session.close();
            return Arrays.asList(resultSet);
        }
    }


    public static void main(String[] args) throws Exception {
        String url,  username,  password;
        url = String.format("bolt://%s:%d", "127.0.0.1", 7695);
        Driver driver = GraphDatabase.driver(url, AuthTokens.basic("neo4j", "neo4j_2025"));
        try ( Session session = driver.session() ){
            FileReader fr=new FileReader("D:\\githubProject\\GraphSET\\graphset1\\src\\main\\java\\org\\example\\graphset\\neo4j\\20250608T212437.log");
            BufferedReader br=new BufferedReader(fr);
            String query = "";
            for (String line=br.readLine(); line != null; line=br.readLine()) {
                if (line.contains("java.lang.AssertionError")){
                    session.close();
                    break;}
                query += line + " ";
                if (line.contains("RETURN") || line.contains("SHOW ")){
                    session.run(query);
                }
                System.out.println(line);

            }
        }
        driver.close();
    }
}
