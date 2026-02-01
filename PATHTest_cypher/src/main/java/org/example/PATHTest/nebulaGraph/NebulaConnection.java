package org.example.PATHTest.nebulaGraph;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import org.example.PATHTest.common.query.GDSmithResultSet;
import org.example.PATHTest.cypher.CypherConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName NebulaDBConnection
 * @Description TODO
 * author jinxingui
 * date 2025/5/4 19:47
 * version V1.0
 **/
public class NebulaConnection extends CypherConnection {
    private NebulaPool pool;
    private String space;
    public NebulaConnection(NebulaPool pool) throws IOErrorException, AuthFailedException, ClientServerIncompatibleException, NotValidConnectionException {
        this.pool = pool;
    }

    @Override
    public String getDatabaseVersion() throws Exception {
        // todo: not use
        return "Nebula";
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void executeStatement(String arg) throws Exception {
        Session session = pool.getSession("root","",false);
        if(space != null){
            session.execute(space);
        }
        ResultSet resultSet = session.execute(arg);
        if (arg.contains("USE")){
            space = arg;
        }
        session.release();
        System.out.println(space);
        System.out.println(resultSet.getErrorMessage());
    }

    @Override
    public List<GDSmithResultSet> executeStatementAndGet(String arg) throws Exception {

        Session session = pool.getSession("root","",false);
        session.execute(space);
        ResultSet resultSet = session.execute(arg);
        GDSmithResultSet gdSmithResultSet = new GDSmithResultSet(resultSet);
        if (resultSet.getErrorMessage().length() > 0) {
            System.out.println(resultSet.getErrorMessage());
            session.release();
            throw new Exception(resultSet.getErrorMessage());
        }
        session.release();
        return Arrays.asList(gdSmithResultSet);
    }

    public static void main(String[] args) throws Exception {
        try {
            NebulaPoolConfig nebulaPoolConfig=new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(10);
            
            List<HostAddress> addresses= Arrays.asList(new HostAddress("localhost",9669));
            NebulaPool pool=new NebulaPool();
            Boolean initResult = pool.init(addresses,nebulaPoolConfig);
            //创建 Session 会话,创建会话时需要用户名和密码
            //参数：账号/密码/是否断开后重试
            Session session=pool.getSession("root","",false);
            session.execute("SHOW HOSTS;");
            session.execute("DROP SPACE IF EXISTS test1");
            session.execute("CREATE SPACE IF NOT EXISTS test1(vid_type=FIXED_STRING(30))");
            ResultSet resultSet = session.execute("USE test1");
            FileReader fr=new FileReader("D:\\githubProject\\GraphSET\\graphset1\\src\\main\\java\\org\\example\\graphset\\nebulaGraph\\test.txt");
            BufferedReader br=new BufferedReader(fr);
            for (String line=br.readLine(); line != null; line=br.readLine()) {
                if (line.isBlank()){
                    TimeUnit.SECONDS.sleep(5);
                    session.release();
                    session = pool.getSession("root","",false);
                    session.execute("USE test1");
                    continue;
                }
                System.out.println(line);
                resultSet  = session.execute(line);
                System.out.println(resultSet.getErrorMessage());
            }

            System.out.println(resultSet.getErrorMessage());

            //通知服务器不再需要该会话，并将连接返回到池，该连接将被重用。如果用户不再使用会话，则调用此函数。
            session.release();
            pool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
