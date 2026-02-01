package org.example.PATHTest.CypherTransform;

import org.example.PATHTest.Randomly;
import org.example.PATHTest.cypher.schema.ILabelInfo;
import org.example.PATHTest.cypher.schema.IRelationTypeInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomMixingExpression {
    private Randomly random;
    private List<ILabelInfo> labelInfoList;
    private List<IRelationTypeInfo> relationTypeInfoList;
    private String databaseName;

    private <T> T randomPick(List<T> list) {
        return list.get(random.getInteger(0, list.size()));
    }
    public void setdatabasename(String databaseName){
        this.databaseName = databaseName;
    }

    public RandomMixingExpression(List<ILabelInfo> labelInfoList, List<IRelationTypeInfo> relationTypeInfoList) {
        this.labelInfoList = labelInfoList;
        this.relationTypeInfoList = relationTypeInfoList;
        this.random = new Randomly();
    }

    // todo:do something to get a multi labels and its relatied filter
    public String random_mixing_label_expression(String target_label) {
        if (!databaseName.contains("neo4j")){
            return target_label;
        }

        boolean wildcard = false;
        boolean firstAndGenerate = false;
        String firstAndPart = "";

        if (target_label == ""){
            wildcard = true;
        }else {
            target_label = target_label.substring(1);
        }

        // 基础：目标表达式始终保留
        List<String> orParts = new ArrayList<>();
        if (!wildcard) {
            orParts.add("(" + target_label + ")");
        }

        int extraBranchNum = random.getInteger(1, 4); // 1~3 个额外 OR 分支

        for (int i = 0; i < extraBranchNum; i++) {
            int andSize = random.getInteger(1, 4); // 1~3 个 label
            Set<String> used = new HashSet<>();
            List<String> andParts = new ArrayList<>();

            for (int j = 0; j < andSize; j++) {
                ILabelInfo info = randomPick(labelInfoList);
                String name = info.getName();

                if (used.contains(name)) {
                    continue;
                }
                used.add(name);

                boolean neg = Randomly.getBooleanWithRatherLowProbability(); // 少量 NOT
                if (neg) {
                    andParts.add("!" + name);
                } else {
                    andParts.add(name);
                }
            }

            if (!andParts.isEmpty()) {
                String andPartStr = "(" + String.join("&", andParts) + ")";
                if (wildcard && !firstAndGenerate) {
                    firstAndGenerate = true;
                    firstAndPart = "!" +  andPartStr;
                }
                orParts.add(andPartStr);
            }
        }

        if (wildcard){
            orParts.add(firstAndPart);
        }
        return ":"+String.join("|", orParts);
    }


    // todo:do somthing to get a multi relation type and its related filter
    public String random_mixing_relation_type_expression(String target_relation_type) {
        if (!databaseName.contains("neo4j")){
            return target_relation_type;
        }

        boolean wildcard = false;
        boolean firstAndGenerate = false;
        String firstAndPart = "";

        if (target_relation_type == ""){
            wildcard = true;
        }else {
            target_relation_type = target_relation_type.substring(1);
        }

        List<String> orParts = new ArrayList<>();
        if (!wildcard) {
            orParts.add("(" + target_relation_type + ")");
        }

        int extraBranchNum = random.getInteger(1, 4);

        for (int i = 0; i < extraBranchNum; i++) {
            int andSize = random.getInteger(1, 4);
            Set<String> used = new HashSet<>();
            List<String> andParts = new ArrayList<>();

            for (int j = 0; j < andSize; j++) {
                IRelationTypeInfo info = randomPick(relationTypeInfoList);
                String name = info.getName();

                if (used.contains(name)) {
                    continue;
                }
                used.add(name);

                boolean neg = Randomly.getBooleanWithRatherLowProbability();
                if (neg) {
                    andParts.add("!" + name);
                } else {
                    andParts.add(name);
                }
            }

            if (!andParts.isEmpty()) {
                String andPartStr = "(" + String.join("&", andParts) + ")";
                if (wildcard && !firstAndGenerate) {
                    firstAndGenerate = true;
                    firstAndPart = "!" + andPartStr;
                }
                orParts.add(andPartStr);
            }
        }

        if (wildcard){
            orParts.add(firstAndPart);
        }
        return ":"+String.join("|", orParts);
    }

    public String target_path_label_relation_filter_expression(List<String> label_relation_list){
        return null;
    }

    public String target_node_label_filter(List<String> node_label_list){
        return null;
    }

    public String target_relation_type_filter(String relation_type, String identifier){
        if (relation_type == ""){
            return null;
        }else {
            if (databaseName.contains("neo4j")){
                return identifier + relation_type + " AND ";
            }else {
                return "type(" + identifier + ")='" + relation_type.substring(1) + "' AND ";
            }
        }
    }

}
