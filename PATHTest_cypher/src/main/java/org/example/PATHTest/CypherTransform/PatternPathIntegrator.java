package org.example.PATHTest.CypherTransform;

import org.example.PATHTest.Randomly;
import org.example.PATHTest.parsercypher.CypherParser;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static org.example.PATHTest.CypherTransform.PatternFormater.format_full_text;
import static org.example.PATHTest.parserutil.parserUtils.getFullTextOriginal;

/**
 * @ClassName PatternPathIntegration
 * @Description TODO
 * date 2025/12/19 16:15
 * version V1.0
 **/
public class PatternPathIntegrator {
    private static String databaseName;

    public static void setDatabaseName(String databaseName){
        PatternPathIntegrator.databaseName = databaseName;
    }


    public static Pair<String, Pair<List<String>, Pair<Map<String, String>, List<String>>>> orig_pattern_path_integrate(CypherParser.PatternElemContext pe, int current_ID){
        StringBuilder pattern = new StringBuilder();
        ArrayList<String> identifierList = new ArrayList<>();
        ArrayList<String> filter = new ArrayList<>();
        ArrayList<String> labelList = new ArrayList<>();
        Randomly r = new Randomly();

        CypherParser.NodePatternContext np = pe.nodePattern();
        if (np.symbol() != null && np.nodeLabels() != null) {
            pattern.append("(").append(np.symbol().getText()).append(format_full_text(np.nodeLabels().getText()));
        } else if (np.symbol() != null) {
            pattern.append("(").append(np.symbol().getText());
        } else if (np.nodeLabels() != null) {
            String format_labels = format_full_text(np.nodeLabels().getText());
            pattern.append("(").append(format_labels);
        }

        if (np.properties() != null)
            pattern.append(np.properties());
        pattern.append(")");

        for (CypherParser.PatternElemChainContext pec : pe.patternElemChain()) {
            np = pec.nodePattern();
            CypherParser.RelationshipPatternContext rp = pec.relationshipPattern();
            String id = "";
            if (rp.relationDetail().symbol() != null) {
                id = rp.relationDetail().symbol().getText();
            } else if(rp.relationDetail().rangeLit()==null) {
                id = "m" + current_ID;
                current_ID++;
                identifierList.add(id);
            }
            identifierList.add(id);
            String lr_id = "";
            String rr_id = "";
            if (rp.LT() != null) {
                lr_id = "<-";
                rr_id = "-";
            } else if (rp.GT() != null) {
                lr_id = "-";
                rr_id = "->";
            }
            //todo 模式拆分后的关系必须有方向
            else {
                lr_id = "-";
                rr_id = "-";
            }
            pattern.append(lr_id).append("[").append(id);
            if (rp.relationDetail().relationshipTypes() != null) {
                String format_relations = format_full_text(rp.relationDetail().relationshipTypes().getText());

                pattern.append(format_relations);
            }
            if (rp.relationDetail().rangeLit() != null) {
                pattern.append(rp.relationDetail().rangeLit().getText());
            }else {
                if (r.getInteger(0,3) == 0 && !(databaseName.contains("nebula") && ((CypherParser.MatchStContext) pe.parent.parent.parent.parent).OPTIONAL() != null)){
                    pattern.append("*1..").append(r.getInteger(1,5));
                }else {
                    pattern.append("*1..1");
                }
            }
            if (rp.relationDetail().properties() != null) {
                pattern.append(getFullTextOriginal(rp.relationDetail().properties()));
            }
            pattern.append("]" + rr_id);
            if (np.symbol() != null && np.nodeLabels() != null) {
                pattern.append("(" + np.symbol().getText() + format_full_text(np.nodeLabels().getText()));
            } else if (np.symbol() != null) {
                pattern.append("(" + np.symbol().getText());
            } else if (np.nodeLabels() != null) {
                pattern.append("("+ format_full_text(np.nodeLabels().getText()));
            }
            if (np.properties() != null)
                pattern.append(getFullTextOriginal(np.properties())) ;
            pattern.append(")") ;
        }

        for(String id : identifierList){
            if (!databaseName.contains("agens") && !databaseName.contains("nebula")){
                filter.add("SIZE(" + id + ")=1 " + "AND ");
            }else if (databaseName.contains("agens")){
                filter.add("length(" + id + ")=1 " + "AND ");
            }else if (databaseName.contains("nebula") && ((CypherParser.MatchStContext) pe.parent.parent.parent).OPTIONAL() == null){
                filter.add("SIZE(" + id + ")==1 " + "AND ");
            }
        }

        return new Pair<>(pattern.toString(), new Pair<>(filter, new Pair<>(new HashMap<String ,String>(), new ArrayList<String>())));
    }

    public static boolean reuseAvailableIdentifier(CypherParser.PatternContext p, String identifier){
        int count = 0;
        for (CypherParser.PatternPartContext pp: p.patternPart()){
            if (pp.patternElem().nodePattern().symbol() !=null &&
                    pp.patternElem().nodePattern().symbol().getText().equals(identifier))
            {
                count++;
                if (count > 1){
                    return true;
                }
            }
            if (pp.patternElem().patternElemChain() != null){
                for (CypherParser.PatternElemChainContext pec: pp.patternElem().patternElemChain()){
                    if (pec.nodePattern().symbol()!=null && pec.nodePattern().symbol().getText().equals(identifier))
                    {
                        count++;
                        if (count > 1){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public static Pair<String, Pair<List<String>, Pair<Map<String, String>, List<String>>>> pattern_path_integrate(CypherParser.PatternElemContext pe, int current_ID, int current_path_id, List<String> preIdentifiers){
        boolean newSupport = true;
        if (!newSupport){
            // todo: this part was the original transformation rule, a branch new was implement below
            return orig_pattern_path_integrate(pe, current_ID);
        }

        StringBuilder pattern = new StringBuilder();
        String path_identifier = "";
        ArrayList<String> identifierList = new ArrayList<>();
        ArrayList<String> filter = new ArrayList<>();
        ArrayList<String> letClause = new ArrayList<>();
        int letIndex = 0;
        HashMap<String, String> with_symbol_filter = new HashMap<>();
        // todo: 主体代码已经完成，但是optional情况还需要完善，因为optional下必须完全等价才能保证语义不改变，此外label filter表达式生成也需要针对性实现
        boolean optional = ((CypherParser.MatchStContext) pe.parent.parent.parent.parent).OPTIONAL() != null;
        Randomly r = new Randomly();

        int path_length = 0;
        String direction = "";
        // get the longest same-direction path length
        for (int i = 0; i < pe.patternElemChain().size(); i++) {
            CypherParser.RelationshipPatternContext rp = pe.patternElemChain().get(i).relationshipPattern();
            if (i == 0){
                if (rp.LT() != null){
                    direction = "<";
                }else if (rp.GT() != null){
                    direction = ">";
                }
                path_length++;
            }else {
                // this np is important to design if the current edge can be integration in single var-length pattern
                if (rp.relationDetail().rangeLit() != null) {
                    break;
                }

                if (rp.LT() != null && direction.equals("<")){
                    path_length++;
                }else if (rp.GT() != null && direction.equals(">")){
                    path_length++;
                }else {
                    break;
                }
            }
        }
        int integrate_length = 1;
        if (path_length > 1){
            integrate_length = r.getInteger(0, path_length+1);
        }

        if (integrate_length > 1 && !optional){
            path_identifier = "p" + current_ID ;
            pattern.append(path_identifier).append(" = ");
        } else {
            integrate_length = 0;
        }
        CypherParser.NodePatternContext np = pe.nodePattern();
        if (np.symbol() != null && np.nodeLabels() != null) {
            if (r.getInteger(0, 4) < 1 && !optional){
                pattern.append("(").append(np.symbol().getText());
                Pair<String, String> letWithFilter = integrationFilter(np.symbol().getText(), np.nodeLabels().getText(), "b"+letIndex);
                letIndex++;
                letClause.add(letWithFilter.getValue0());
                filter.add(letWithFilter.getValue1());
            }else {
                pattern.append("(").append(np.symbol().getText()).append(format_full_text(np.nodeLabels().getText()));
            }
        } else if (np.symbol() != null) {
            pattern.append("(").append(np.symbol().getText());
        } else if (np.nodeLabels() != null) {
            String format_labels = format_full_text(np.nodeLabels().getText());
            pattern.append("(").append(format_labels);
        }

        if (np.properties() != null)
            pattern.append(np.properties());
        pattern.append(")");

        String common_relation_type = "";
        // When the longest pattern path length greater than 1, we can use path_identifier to integrate them
        for (int i = 1; i < integrate_length; i++) {
            CypherParser.PatternElemChainContext pec = pe.patternElemChain().get(i);
            CypherParser.PatternElemChainContext prePec = pe.patternElemChain().get(i-1);

            np = prePec.nodePattern();
            CypherParser.RelationshipPatternContext rp = pec.relationshipPattern();
            CypherParser.RelationshipPatternContext preRp = prePec.relationshipPattern();
            if (i == 1){
                String firstRelationType = preRp.relationDetail().relationshipTypes() != null?
                        preRp.relationDetail().relationshipTypes().getText(): "";
                String secondRelationType = rp.relationDetail().relationshipTypes() != null?
                        rp.relationDetail().relationshipTypes().getText(): "";
                common_relation_type = findCommonLabel(firstRelationType, secondRelationType);
                if (preRp.relationDetail().symbol() != null) {
                    String symbol = preRp.relationDetail().symbol().getText().strip();
                    Pair<String, String> symbolMap = withClauseFilter(path_identifier, 0, symbol);
                    with_symbol_filter.put(symbolMap.getValue0(), symbolMap.getValue1());
                    if (preRp.relationDetail().relationshipTypes() != null){
                        if (r.getInteger(0, 2) < 1){
                            filter.add(integrationFilter(path_identifier,
                                    0, symbol, preRp.relationDetail().relationshipTypes().getText()));
                        }else {
                            Pair<String, String> letAndFilter = integrationFilter(path_identifier,
                                    0, symbol, preRp.relationDetail().relationshipTypes().getText(),
                                    "b"+letIndex);
                            letIndex++;
                            letClause.add(letAndFilter.getValue0());
                            filter.add(letAndFilter.getValue1());
                        }
                    }
                }
            }else {
                String currentRelationType = rp.relationDetail().relationshipTypes() != null?
                        rp.relationDetail().relationshipTypes().getText(): "";
                common_relation_type = findCommonLabel(common_relation_type, currentRelationType);

            }


            if (rp.relationDetail().symbol() != null) {
                String symbol = rp.relationDetail().symbol().getText().strip();
                Pair<String, String> symbolMap = withClauseFilter(path_identifier, i, symbol);
                with_symbol_filter.put(symbolMap.getValue0(), symbolMap.getValue1());
                if (rp.relationDetail().relationshipTypes() != null){
                    if (r.getInteger(0, 2) < 1){
                        filter.add(integrationFilter(path_identifier,
                                i, symbol, rp.relationDetail().relationshipTypes().getText()));
                    }else {
                        Pair<String, String> letAndFilter = integrationFilter(path_identifier,
                                i, symbol, rp.relationDetail().relationshipTypes().getText(),
                                "b"+letIndex);
                        letIndex++;
                        letClause.add(letAndFilter.getValue0());
                        filter.add(letAndFilter.getValue1());
                    }
                }
            }
            if (np.symbol() != null) {
                String symbol = np.symbol().getText().strip();
                Pair<String, String> symbolMap = withClauseFilter(path_identifier, i, symbol);
                with_symbol_filter.put(symbolMap.getValue0(), symbolMap.getValue1());
                if (np.nodeLabels() != null){
                    if (r.getInteger(0, 2) < 1){
                        filter.add(integrationFilter(path_identifier, i, symbol, np.nodeLabels().getText()));
                    }else {
                        Pair<String, String> letAndFilter = integrationFilter(path_identifier,
                                i, symbol, np.nodeLabels().getText(),
                                "b"+letIndex);
                        letIndex++;
                        letClause.add(letAndFilter.getValue0());
                        filter.add(letAndFilter.getValue1());
                    }
                }
                if (preIdentifiers.contains(symbol) || reuseAvailableIdentifier((CypherParser.PatternContext) pe.parent.parent, symbol)){
                    filter.add(integrationFilter(path_identifier, i, symbol));
                }
            }
            // preprocess has been done, start integrate pattern
            if (i == integrate_length - 1){
                np = pec.nodePattern();
                String identifier = "m" + current_ID;
                current_ID++;
                String lr_id = "";
                String rr_id = "";
                if (direction.equals(">")){
                    lr_id = "-";
                    rr_id = "->";
                }else if (direction.equals("<")){
                    lr_id = "<-";
                    rr_id = "-";
                }
                pattern.append(lr_id).append("[").append(identifier);
                if (common_relation_type != ""){
                    pattern.append(common_relation_type);
                }

                if (r.getInteger(0,3) == 0 && !(databaseName.contains("nebula") && !optional)){
                    pattern.append("*").append(r.getInteger(0, integrate_length)).
                            append("..").
                            append(r.getInteger(integrate_length, integrate_length+3));
                }else {
                    pattern.append("*").append(integrate_length).append("..").append(integrate_length);
                }

                pattern.append("]").append(rr_id);
                // The path has been integrated, now append the end node.
                if (np.symbol() != null && np.nodeLabels() != null) {
                    if (r.getInteger(0, 4) < 1  && !optional){
                        pattern.append("(").append(np.symbol().getText());
                        Pair<String, String> letWithFilter = integrationFilter(np.symbol().getText(),
                                np.nodeLabels().getText(), "b"+letIndex);
                        letIndex++;
                        letClause.add(letWithFilter.getValue0());
                        filter.add(letWithFilter.getValue1());
                    }else {
                        pattern.append("(").append(np.symbol().getText()).append(format_full_text(np.nodeLabels().getText()));
                    }
                } else if (np.symbol() != null) {
                    pattern.append("(").append(np.symbol().getText());
                } else if (np.nodeLabels() != null) {
                    pattern.append("(").append(format_full_text(np.nodeLabels().getText()));
                }
                if (np.properties() != null)
                    pattern.append(getFullTextOriginal(np.properties())) ;
                pattern.append(")") ;

                // All the integrate path has been generated, now update the filter to keep equivalence.
                filter.add(path_length_filter(identifier, integrate_length));
            }
        }

        // fot the rest pattern or integrate length less than 1, using old pattern integration implement
        for (int i = integrate_length; i < pe.patternElemChain().size(); i++ ){
            CypherParser.PatternElemChainContext pec = pe.patternElemChain().get(i);
            np = pec.nodePattern();
            CypherParser.RelationshipPatternContext rp = pec.relationshipPattern();
            String id = "";
            if (rp.relationDetail().symbol() != null) {
                id = rp.relationDetail().symbol().getText();
                identifierList.add(id);
                Pair<String, String> symbolMap = withClauseFilter(0, id);
                with_symbol_filter.put(symbolMap.getValue0(), symbolMap.getValue1());
            } else if(rp.relationDetail().rangeLit()==null) {
                id = "m" + current_ID;
                current_ID++;
                identifierList.add(id);
            }
            String lr_id = "";
            String rr_id = "";
            if (rp.LT() != null) {
                lr_id = "<-";
                rr_id = "-";
            } else if (rp.GT() != null) {
                lr_id = "-";
                rr_id = "->";
            }
            //todo 模式拆分后的关系必须有方向
            else {
                lr_id = "-";
                rr_id = "-";
            }
            pattern.append(lr_id).append("[").append(id);
            if (rp.relationDetail().relationshipTypes() != null) {
                String format_relations = format_full_text(rp.relationDetail().relationshipTypes().getText());

                pattern.append(format_relations);
            }
            if (rp.relationDetail().rangeLit() != null) {
                pattern.append(rp.relationDetail().rangeLit().getText());
            }else {
                if (r.getInteger(0,3) == 0 && !(databaseName.contains("nebula") && !optional)){
                    pattern.append("*1..").append(r.getInteger(1,5));
                }else {
                    pattern.append("*1..1");
                }
            }
            if (rp.relationDetail().properties() != null) {
                pattern.append(getFullTextOriginal(rp.relationDetail().properties()));
            }
            pattern.append("]").append(rr_id);
            if (np.symbol() != null && np.nodeLabels() != null) {
                if (r.getInteger(0, 4) < 1  && !optional){
                    pattern.append("(").append(np.symbol().getText());
                    Pair<String, String> letWithFilter = integrationFilter(np.symbol().getText(), np.nodeLabels().getText(), "b"+letIndex);
                    letIndex++;
                    letClause.add(letWithFilter.getValue0());
                    filter.add(letWithFilter.getValue1());
                }else {
                    pattern.append("(").append(np.symbol().getText()).append(format_full_text(np.nodeLabels().getText()));
                }
            } else if (np.symbol() != null) {
                pattern.append("(").append(np.symbol().getText());
            } else if (np.nodeLabels() != null) {
                pattern.append("(").append(format_full_text(np.nodeLabels().getText()));
            }
            if (np.properties() != null)
                pattern.append(getFullTextOriginal(np.properties())) ;
            pattern.append(")") ;
        }

        for(String id : identifierList){
            filter.add(path_length_filter(id, 1));
        }

        return new Pair<>(pattern.toString(), new Pair<>(filter, new Pair<>(with_symbol_filter, letClause)));
    }

    public static String path_length_filter(String symbol, int length){
        StringBuilder result = new StringBuilder();
        if (!databaseName.contains("agens") && !databaseName.contains("nebula")){
            result.append("SIZE(").append(symbol).append(")=").append(length).append(" AND ");
        }else if (databaseName.contains("agens")){
            result.append("length(").append(symbol).append(")=").append(length).append(" AND ");
        }else if (databaseName.contains("nebula") ){
            //todo: nebula seems need specified process when OPTIONAL, this expression only applied to MATCH
            result.append("SIZE(").append(symbol).append(")==1").append(length).append(" AND ");
        }
        return result.toString();
    }

    public static String integrationFilter(String path_symbol, int index, String symbol){
        StringBuilder result = new StringBuilder();
        result.append("nodes")
                .append("(").append(path_symbol).append(")[")
                .append(index).append("].id = ")
                .append(symbol).append(".id AND ");
        return result.toString();
    }

    public static Pair<String, String> integrationFilter(String symbol, String path_label_list, String letId){
        String type = "";
        int sizeOfLabelList = 0;
        StringBuilder letClause = new StringBuilder();

        if (symbol.strip().startsWith("n")){
            type = "nodes";
        }else {
            type = "relationships";
        }
        Set<String> labelList =  Arrays.stream(path_label_list.split(":"))
                .filter(s -> !s.isEmpty())
                .map(s -> "'" + s + "'")
                .collect(Collectors.toSet());
        sizeOfLabelList = labelList.size();
        if (sizeOfLabelList == 1) {
            if (databaseName.contains("neo4j")){
                letClause.append(letId).append(" = ").append(labelList.toArray()[0]);
            }else {
                // other dababase doesn't support LET clause, use "WITH *, expression" clause instead.
                letClause.append(labelList.toArray()[0]).append(" AS ").append(letId);
            }
        }else {
            if (databaseName.contains("neo4j")){
                letClause.append(letId).append(" = ");
                letClause.append("[").append(String.join(",", labelList)).append("] ");
            }else {
                letClause.append("[").append(String.join(",", labelList)).append("] ").append(" AS ").append(letId);
            }
        }

        StringBuilder filter = new StringBuilder();
        if (sizeOfLabelList == 1) {
            if (type.equals("nodes")) {
                filter.append(letId).append(" IN ")
                        .append("labels(").append(symbol).append(")");
            }else {
                filter.append(letId).append(" = ")
                        .append("type(").append(symbol).append(")");
            }

        }else {
            filter.append("ALL (x IN ").append(letId).append(" WHERE x IN ")
                    .append("labels(").append(symbol).append("))");
        }
        return new Pair<>(letClause.toString(), filter.append(" AND ").toString());

    }

    public static Pair<String, String> integrationFilter(String path_symbol, int index, String symbol, String path_label_list, String letId){
        String type = "";
        int sizeOfLabelList = 0;
        StringBuilder letClause = new StringBuilder();

        if (symbol.strip().startsWith("n")){
            type = "nodes";
        }else {
            type = "relationships";
        }
        Set<String> labelList =  Arrays.stream(path_label_list.split(":"))
                .filter(s -> !s.isEmpty())
                .map(s -> "'" + s + "'")
                .collect(Collectors.toSet());

        sizeOfLabelList = labelList.size();
        if (databaseName.contains("neo4j")){
            if (sizeOfLabelList == 1) {
                letClause.append(letId).append(" = ").append(labelList.toArray()[0]);
            }else {
                letClause.append(letId).append(" = ");
                letClause.append("[").append(String.join(",", labelList)).append("] ");
            }
        }else {
            if (sizeOfLabelList == 1) {
                letClause.append(labelList.toArray()[0]).append(" AS ").append(letId);
            }else {
                letClause.append("[").append(String.join(",", labelList)).append("] ")
                        .append(" AS ").append(letId);
            }
        }


        StringBuilder filter = new StringBuilder();
        if (sizeOfLabelList == 1) {
            if (type.equals("nodes")) {
                filter.append(letId).append(" IN ")
                        .append("labels(").append(type)
                        .append("(").append(path_symbol).append(")")
                        .append("[").append(index).append("]")
                        .append(")");
            }else {
                filter.append(letId).append(" = ")
                        .append("type(").append(type)
                        .append("(").append(path_symbol).append(")")
                        .append("[").append(index).append("]")
                        .append(")");
            }
        }else {
            filter.append("ALL (x IN ").append(letId).append(" WHERE x IN ")
                    .append("labels(").append(type)
                    .append("(").append(path_symbol).append(")")
                    .append("[").append(index).append("]")
                    .append("))");
        }
        return new Pair<>(letClause.toString(), filter.append(" AND ").toString());
    }

    public static String integrationFilter(String path_symbol, int index, String symbol, String path_label_list){
        String type = "";
        StringBuilder result = new StringBuilder();
        if (symbol.strip().startsWith("n")){
            type = "nodes";
        }else {
            type = "relationships";
        }
        if (databaseName.contains("neo4j")){
            result.append(type)
                    .append("(").append(path_symbol).append(")")
                    .append("[").append(index).append("]")
                    .append(":").append(path_label_list.substring(1).replace(":", "&"))
                    .append(" AND ");
        }else {
            Set<String> labelList =  Arrays.stream(path_label_list.split(":"))
                    .filter(s -> !s.isEmpty())
                    .map(s -> "'" + s + "'")
                    .collect(Collectors.toSet());
            if (type.equals("nodes")) {
                result.append("ALL(x IN ").append("[").append(String.join(",", labelList)).append("] WHERE x IN labels(")
                        .append(type)
                        .append("(").append(path_symbol).append(")")
                        .append("[").append(index).append("]").append(")) AND ");
            }else {
                result.append(String.join(",", labelList)).append(" = ")
                        .append("type(").append(type)
                        .append("(").append(path_symbol).append(")")
                        .append("[").append(index).append("]")
                        .append(") AND ");
            }

        }


        return result.toString();
    }

    public static Pair<String, String> withClauseFilter(int index, String symbol){
        StringBuilder result = new StringBuilder();
        return new Pair<>(symbol, result.append(symbol).append("[").append(index).append("]").toString());
    }

    public static Pair<String, String> withClauseFilter(String path_symbol, int index, String symbol){
        StringBuilder result = new StringBuilder();
        String type = "";
        if (symbol.strip().startsWith("n")){
            type = "nodes";
        }else {
            type = "relationships";
        }

        result.append(type)
                .append("(").append(path_symbol).append(")")
                .append("[").append(index).append("]");

        return new Pair<>(symbol, result.toString());
    }

    public static String findCommonLabel(String labelList1, String labelList2){
        if (labelList1 == "" || labelList2 == ""){
            return "";
        }

        Set<String> set1 = Arrays.stream(labelList1.split(":"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        Set<String> set2 = Arrays.stream(labelList2.split(":"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        set1.retainAll(set2); // 求交集

        if (set1.isEmpty()) {
            return ""; // 或者 return ":" 取决于你的语义
        }

        return set1.stream()
                .map(s -> ":" + s)
                .collect(Collectors.joining());
    }

}
