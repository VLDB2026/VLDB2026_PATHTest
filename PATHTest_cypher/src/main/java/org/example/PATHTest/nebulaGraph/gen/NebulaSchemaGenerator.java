package org.example.PATHTest.nebulaGraph.gen;

import org.example.PATHTest.cypher.gen.CypherSchemaGenerator;
import org.example.PATHTest.cypher.schema.CypherSchema;
import org.example.PATHTest.nebulaGraph.NebulaGlobalState;
import org.example.PATHTest.nebulaGraph.NebulaSchema;

import java.util.List;

/**
 * @ClassName NebulaGraphSchemaGenerator
 * @Description TODO
 * author jinxingui
 * date 2025/5/8 14:12
 * version V1.0
 **/
public class NebulaSchemaGenerator extends CypherSchemaGenerator<NebulaSchema, NebulaGlobalState> {
    public NebulaSchemaGenerator(NebulaGlobalState globalState) {
        super(globalState);
    }

    @Override
    public NebulaSchema generateSchemaObject(NebulaGlobalState globalState, List<CypherSchema.CypherLabelInfo> labels, List<CypherSchema.CypherRelationTypeInfo> relationTypes, List<CypherSchema.CypherPatternInfo> patternInfos) {
        return null;
    }
}
