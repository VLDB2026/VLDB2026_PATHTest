package org.example.PATHTest.cypher.algorithm;

import org.example.PATHTest.DBMSSpecificOptions;
import org.example.PATHTest.IgnoreMeException;
import org.example.PATHTest.StateToReproduce;
import org.example.PATHTest.common.oracle.TestOracle;
import org.example.PATHTest.cypher.CypherConnection;
import org.example.PATHTest.cypher.CypherGlobalState;
import org.example.PATHTest.cypher.CypherProviderAdapter;
import org.example.PATHTest.cypher.CypherQueryAdapter;
import org.example.PATHTest.cypher.gen.graph.SlidingGraphGenerator;
import org.example.PATHTest.cypher.gen.query.SlidingQueryGenerator;
import org.example.PATHTest.cypher.oracle.DifferentialNonEmptyBranchOracle;
import org.example.PATHTest.cypher.schema.CypherSchema;
import org.example.PATHTest.exceptions.DatabaseCrashException;
import org.example.PATHTest.exceptions.MustRestartDatabaseException;
import org.example.PATHTest.cypher.gen.GraphManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Compared3AlgorithmNew<S extends CypherSchema<G,?>, G extends CypherGlobalState<O, S>,
        O extends DBMSSpecificOptions, C extends CypherConnection> extends CypherTestingAlgorithm<S,G,O,C>{
    private GraphManager graphManager;


    public Compared3AlgorithmNew(CypherProviderAdapter<G, S, O> provider) {
        super(provider);
    }

    @Override
    public void generateAndTestDatabase(G globalState) throws Exception {
        try {
            generateDatabase(globalState); //抽象，应该是生成表结构，并往里面插入了初始的数据
            globalState.getManager().incrementCreateDatabase(); //原子操作计数

            File file = new File("coverage_info");
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);


            TestOracle oracle = new DifferentialNonEmptyBranchOracle<G, S>(globalState, new SlidingQueryGenerator<>(graphManager));
            for (int i = 0; i < globalState.getOptions().getNrQueries(); i++) {
                try (StateToReproduce.OracleRunReproductionState localState = globalState.getState().createLocalState()) {
                    assert localState != null;
                    try {
                        oracle.check();
                        globalState.getManager().incrementSelectQueryCount();
                    } catch (IgnoreMeException e) {
                    } catch (MustRestartDatabaseException e){
                        oracle.getFw().flush();
                        throw e;
                    } catch (DatabaseCrashException e){
                        if(e.getCause() instanceof MustRestartDatabaseException){
                            throw new MustRestartDatabaseException(e);
                        }
                        oracle.getFw().flush();
                        e.printStackTrace();
//                        executor.getStateToReproduce().exception = reduce.getMessage();
//                        globalState.getLogger().logFileWriter = null;
                        globalState.getLogger().logException(e, globalState.getState());
                    } catch (Exception e){
                        oracle.getFw().flush();
                        e.printStackTrace();
//                        executor.getStateToReproduce().exception = reduce.getMessage();
//                        globalState.getLogger().logFileWriter = null;
                        globalState.getLogger().logException(e, globalState.getState());
                    }
                    assert localState != null;
                    localState.executedWithoutError();
                }

            }
            oracle.getFw().close();
            throw new RuntimeException("total number reached");
//            System.exit(0);
        } finally {
            globalState.getConnection().close();
        }
    }

    public void generateDatabase(G globalState) throws Exception{
        SlidingGraphGenerator<G,S> generator = new SlidingGraphGenerator<>(globalState);// 破坏了多态性，所有图数据库共用一个查询生成方法
        this.graphManager = generator.getGraphManager();
        List<CypherQueryAdapter> queries = generator.createGraph(globalState);

        for(CypherQueryAdapter query : queries){
            globalState.executeStatement(query);
            globalState.getState().logCreateStatement(query);
        }
        if (DifferentialNonEmptyBranchOracle.needLabel()){
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
