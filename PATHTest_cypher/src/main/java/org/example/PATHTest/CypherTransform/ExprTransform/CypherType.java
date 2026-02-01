package org.example.PATHTest.CypherTransform.ExprTransform;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CypherType
 * @Description TODO
 * author jinxingui
 * date 2025/3/20 19:58
 * version V1.0
 **/
public class CypherType {
    private String name;

    private static Map<String,CypherType> cypherTypeMap;

    public CypherType(String name) {
        this.name = name;
    }

    public static CypherType getCypherType(String name) {
        if (cypherTypeMap == null) {
            cypherTypeMap = new HashMap<String,CypherType>();
        }

        if (cypherTypeMap.containsKey(name)) {
            return cypherTypeMap.get(name);
        }else {
            CypherType cypherType = new CypherType(name);
            cypherTypeMap.put(name, cypherType);
            return cypherType;
        }
    }
}
