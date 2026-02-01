package org.example.PATHTest.tuGraph;

import org.example.PATHTest.cypher.CypherGlobalState;
import org.example.PATHTest.tuGraph.gen.TuGraphSchemaGenerator;

/**
 * @ClassName TuGraphGlobalState
 * @Description TODO
 * date 2025/5/30 12:00
 * version V1.0
 **/
public class TuGraphGlobalState extends CypherGlobalState<TuGraphOptions, TuGraphSchema> {
    private TuGraphSchema tuGraphSchema = null;

    public TuGraphGlobalState(){
        super();
        System.out.println("new global state");
    }

    @Override
    protected TuGraphSchema readSchema() throws Exception {
        if(tuGraphSchema == null){
            tuGraphSchema = new TuGraphSchemaGenerator(this).generateSchema();
        }
        return tuGraphSchema;
    }

}
