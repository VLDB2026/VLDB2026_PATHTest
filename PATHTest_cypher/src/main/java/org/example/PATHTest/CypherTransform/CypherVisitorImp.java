package org.example.PATHTest.CypherTransform;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.example.PATHTest.Randomly;
import org.example.PATHTest.cypher.CypherGlobalState;
import org.example.PATHTest.cypher.schema.CypherSchema;
import org.example.PATHTest.parsercypher.gen.CypherLexer;
import org.example.PATHTest.parsercypher.CypherParser;
import org.example.PATHTest.parsercypher.CypherParserBaseVisitor;
import org.javatuples.Pair;


import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.PATHTest.CypherTransform.PatternFormater.format_full_text;
import static org.example.PATHTest.CypherTransform.PatternPathIntegrator.pattern_path_integrate;
import static org.example.PATHTest.parserutil.parserUtils.getFullTextOriginal;

public class CypherVisitorImp<G extends CypherGlobalState<?, S>, S extends CypherSchema<G, ?>> extends CypherParserBaseVisitor<String> {
    protected String result_qurey;
    protected ArrayList<String> resultQueryUnionPartition;
    protected ArrayList<String> unionPartitionPattern;
    protected int unionPartitionIndex;
    protected boolean isUnionPartition;
    protected boolean inUnionPartition;
    protected boolean isDistinctReturn;
    protected boolean isFilterUsed;
    protected static RandomMixingExpression randomMixingExpression;


    private G globalState;
    private List<String> list_function = Arrays.asList("nodes", "toIntegerList", "toStringList", "toBooleanList", "range", "relationships", "keys", "labels", "collect");
    private List<String> string_function = Arrays.asList("type", "substring", "toStringOrNull");
    private List<String> boolean_function = Arrays.asList("exists", "isEmpty", "toBooleanOrNull");
    private List<String> with_operator = Arrays.asList("WITH * ");
    private List<String> where_operator = Arrays.asList("WHERE TRUE ");
    private List<String> order_operator = Arrays.asList("ORDER BY TRUE ", "ORDER BY FALSE ", "ORDER BY NULL ", "ORDER BY 1 ");
    private List<String> order_operator_add = Arrays.asList(",TRUE ", ",FALSE ", ",NULL ", ",1 ");
    private List<String> unwind_operator_mem = Arrays.asList("UNWIND [1] as l", "UNWIND [NULL] as l");
    private List<String> unwind_operator = Arrays.asList("UNWIND [1] as l", "UNWIND [NULL] as l", "UNWIND 1 as l");
    private List<String> case_operator = Arrays.asList("CASE WHEN TRUE THEN ", "CASE WHEN FALSE THEN NULL ELSE ", "END ");
    private List<String> reduce_operator = Arrays.asList("reduce(a=", ",b IN []|a) ");
    private List<String> list_add_operator = Arrays.asList("([]+", "+[]) ");
    private List<String> string_add_operator = Arrays.asList("(\'\'+", "+\'\') ");
    private List<String> bool_add_operator = Arrays.asList("TRUE AND ", "FALSE OR ", "AND TRUE ", "OR FALSE ");
    private List<String> return_operator = Arrays.asList("RETURN * ", "RETURN NULL ");
    private List<String> path_operator = Arrays.asList("pp", "=");
    private List<String> desc_operator = Arrays.asList("DESC ");
    private List<String> call_return_operator = Arrays.asList("NULL AS aa");

    //    private List<String> call_operator = Arrays.asList("CALL{","} ");
    public CypherVisitorImp() {
        this.result_qurey = "";
        this.resultQueryUnionPartition = new ArrayList<>();
        this.unionPartitionPattern = new ArrayList<>();
        this.unionPartitionIndex = 0;
        this.isUnionPartition = false;
        this.inUnionPartition = false;
        this.isDistinctReturn = false;
        this.isFilterUsed = false;
        this.r = new Randomly();
        this.ID = 0;
        this.create_id = 0;
        this.unwind_id = 0;
        this.path_id = 0;
        this.call_return_id = 0;
        this.avaliableIdentifiers=new ArrayList<>();
        this.avaliableLocalIdentifiers = new ArrayList<>();
        this.varLengthPartition = false;
        this.optionalMatch = false;
    }

    public void setGlobalState(G g) {
        this.globalState = g;
        // refresh the schema information
        this.refresh_schema_info();

    }
    public void setIsUnionPartition(boolean isUnionPartition) {
        this.isUnionPartition = isUnionPartition;
    }
    public int ID = 0;
    public boolean in_where = false;
    public int create_id = 0;
    public int unwind_id = 0;
    public int path_id = 0;
    public int call_return_id = 0;
    public Randomly r;
    public ArrayList<String> avaliableIdentifiers;
    public ArrayList<String> avaliableLocalIdentifiers;
    public boolean varLengthPartition;
    public boolean optionalMatch;
    public static Boolean in_subquery = false;


    public String Getresult_qurey() {
        if (!unionPartitionPattern.isEmpty()){
            int replaceLength = unionPartitionPattern.get(0).length();
            StringBuilder replaced_result = new StringBuilder();
            StringBuilder unionAllPartitionResult = new StringBuilder();
            unionAllPartitionResult.append(result_qurey);
            if (isDistinctReturn){
                unionAllPartitionResult.append(" UNION ");
            }else {
                unionAllPartitionResult.append(" UNION ALL ");
            }
            for (int i = 1; i < unionPartitionPattern.size(); i++){
                replaced_result.append(result_qurey.substring(0, unionPartitionIndex));
                replaced_result.append(unionPartitionPattern.get(i));
                replaced_result.append(result_qurey.substring(unionPartitionIndex+replaceLength, result_qurey.length()));
                unionAllPartitionResult.append(replaced_result);
                if (i != unionPartitionPattern.size()-1){
                    if (isDistinctReturn){
                        unionAllPartitionResult.append(" UNION ");
                    }else {
                        unionAllPartitionResult.append(" UNION ALL ");
                    }                }
                replaced_result = new StringBuilder();
            }
            if (isFilterUsed){
                return "CYPHER 25 " + unionAllPartitionResult.toString();
            }else return unionAllPartitionResult.toString();
        }
        if (isFilterUsed){
            return "CYPHER 25 " + result_qurey;
        }else return this.result_qurey;
    }

    public void Clear() {
        this.result_qurey = "";
        this.resultQueryUnionPartition.clear();
        this.unionPartitionPattern.clear();
        this.unionPartitionIndex = 0;
        this.isUnionPartition = false;
        this.inUnionPartition = false;
        this.isDistinctReturn = false;
        this.isFilterUsed = false;
        this.ID = 0;
        this.create_id = 0;
        this.unwind_id = 0;
        this.path_id = 0;
        this.in_where = false;
        this.avaliableIdentifiers.clear();
        this.avaliableLocalIdentifiers.clear();
    }

    private ArrayList<String> updateIdentifiers(ParserRuleContext ctx) {
        if (ctx instanceof CypherParser.PatternWhereContext){
            for (int i = 0; i < ((CypherParser.PatternWhereContext)ctx).pattern().patternPart().size(); i++){
                CypherParser.PatternPartContext patternPart = ((CypherParser.PatternWhereContext)ctx).pattern().patternPart(i);
                CypherParser.NodePatternContext nodePatternContext = patternPart.patternElem().nodePattern();
                List<CypherParser.PatternElemChainContext> patternElemChainContextList = patternPart.patternElem().patternElemChain();
                if (nodePatternContext.symbol() != null){
                    if (!avaliableIdentifiers.contains(nodePatternContext.symbol().getText())){
                        avaliableIdentifiers.add(nodePatternContext.symbol().getText());
                        avaliableLocalIdentifiers.add(nodePatternContext.symbol().getText());
                    }
                }
                if (!patternElemChainContextList.isEmpty()){
                    for (CypherParser.PatternElemChainContext chainContext : patternElemChainContextList){
                        if (chainContext.nodePattern().symbol() != null){
                            if (!avaliableIdentifiers.contains(chainContext.nodePattern().symbol().getText())){
                                avaliableIdentifiers.add(chainContext.nodePattern().symbol().getText());
                                avaliableLocalIdentifiers.add(chainContext.nodePattern().symbol().getText());
                            }
                        }
                        if (chainContext.relationshipPattern().relationDetail().symbol() != null){
                            if (!avaliableIdentifiers.contains(chainContext.relationshipPattern().relationDetail().symbol().getText())){
                                avaliableIdentifiers.add(chainContext.relationshipPattern().relationDetail().symbol().getText());
                                avaliableLocalIdentifiers.add(chainContext.relationshipPattern().relationDetail().symbol().getText());
                            }
                        }
                    }
                }
            }
            return null;
        }else if (ctx instanceof CypherParser.UnwindStContext){
            if (((CypherParser.UnwindStContext)ctx).symbol() != null) {
                if (!avaliableIdentifiers.contains(((CypherParser.UnwindStContext)ctx).symbol().getText())) {
                    avaliableIdentifiers.add(((CypherParser.UnwindStContext)ctx).symbol().getText());
                }
            }
        }else if (ctx instanceof CypherParser.WithStContext){
            if (((CypherParser.WithStContext)ctx).projectionBody().projectionItems().MULT()==null) {
                avaliableIdentifiers.clear();
            }
            for(CypherParser.ProjectionItemContext projectionItem: ((CypherParser.WithStContext)ctx).projectionBody().projectionItems().projectionItem()){
                if(projectionItem.AS()!=null) {
                    if (!avaliableIdentifiers.contains(projectionItem.AS().getText())) {
                        avaliableIdentifiers.add(projectionItem.symbol().getText());
                    }
                }else {
                    String symbol = projectionItem.expression().xorExpression(0).andExpression(0).notExpression(0).comparisonExpression().addSubExpression(0).multDivExpression(0).powerExpression(0).unaryAddSubExpression(0).atomicExpression().propertyOrLabelExpression().propertyExpression().atom().symbol().getText();
                    if(!avaliableIdentifiers.contains(symbol)){
                        avaliableIdentifiers.add(symbol);
                    }
                }
            }
        }else if (ctx instanceof CypherParser.ScriptContext){
            avaliableIdentifiers.clear();
        }

        return null;
    }

    private void avaliableLocalIdentifiersClear(){
        avaliableLocalIdentifiers.clear();
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        if (!node.getText().equals(";") && !node.getText().equals("<EOF>")){
            if (node.getParent() instanceof CypherParser.RelationshipPatternContext || node.getParent() instanceof CypherParser.RelationDetailContext){
                result_qurey += node.getText();
            }else if (node.getParent() instanceof CypherParser.NodePatternContext){
                result_qurey += node.getText();
            }else {
                result_qurey += node.getText() + " ";
            }
        }
        return null;
    }

    @Override
    public String visitPatternWhere(CypherParser.PatternWhereContext ctx) {
        //todo 对于非Redis数据库，只拆分最外层的matchstatement语句中的模式（忽略子查询中的match模式）
        if (ctx.parent.parent.parent instanceof CypherParser.SingleQueryContext && (r.getInteger(0,2)==0 || varLengthPartition) && !globalState.getDatabaseName().contains("redis")) {
            List<String> identifiers = new ArrayList<>(); // pattern list in this clause. For example (n), (n)-[]->(m) will be two elements in this list
            List<String> patterns = new ArrayList<>(); // pattern filter list
            Map<String, String> withClauseFilter = new HashMap<>(); // a special clause, which is used when f2v applied
            List<String> letClause = new ArrayList<>();
            Map<Integer, List<String>> branchPatternLabelFilterList = new HashMap<>();
            List<String> single_node_patterns = new ArrayList<>();
            List<String> doubleNegationIdentifiers = new ArrayList<>();
            List<StringBuilder> varLengthPartitionIdentifierBuilders = new ArrayList<>();
            List<String> varLengthPartitionIdentifiers = new ArrayList<>();
            List<String> varBranchFilter = new ArrayList<>();
            ArrayList<Integer> lowBound = new ArrayList<>();
            ArrayList<Integer> upperBound = new ArrayList<>();
            int boundIndex = 0, varLengthPatternIndex = 0;
            int random = r.getInteger(0, ctx.pattern().patternPart().size());
            // 随机选择一种路径匹配蜕变策略进行等价变换
            int randomPathMutationStrategy = r.getInteger(2, 4);
            if (varLengthPartition){
                ArrayList<Integer> varLengthPattern  = new ArrayList<>();
                ArrayList<Integer> varLengthIndexes = new ArrayList<>();
                for (int i = 0; i < ctx.pattern().patternPart().size(); i++){
                    if (ctx.pattern().patternPart().get(i).patternElem().patternElemChain()!=null) {
                        List<CypherParser.PatternElemChainContext> patternElemChainContexts = ctx.pattern().patternPart().get(i).patternElem().patternElemChain();
                        for (int j = 0; j < patternElemChainContexts.size(); j++) {
                            CypherParser.PatternElemChainContext patternElemChainContext = patternElemChainContexts.get(j);
                            if (patternElemChainContext.relationshipPattern().relationDetail().rangeLit() == null){
                                continue;
                            }
                            List<CypherParser.NumLitContext> numLitContexts = patternElemChainContext.relationshipPattern().relationDetail().rangeLit().numLit();
                            if (numLitContexts.size() == 2 || (numLitContexts.size() == 1 && numLitContexts.get(0).parent.getChild(2) instanceof CypherParser.NumLitContext)) {
                                varLengthPattern.add(i);
                                varLengthIndexes.add(j);
                                if (numLitContexts.size() == 2){
                                    int tempLow = Integer.parseInt(numLitContexts.get(0).getText());
                                    lowBound.add(tempLow==0?1:tempLow);
                                    upperBound.add(Integer.parseInt(numLitContexts.get(1).getText()));
                                }else {
                                    lowBound.add(1);
                                    upperBound.add(Integer.parseInt(numLitContexts.get(0).getText()));
                                }
                                break;
                            }
                        }
                    }
                }
                if (!varLengthPattern.isEmpty()){
                    boundIndex = r.getInteger(0, varLengthPattern.size());
                    varLengthPatternIndex = varLengthIndexes.get(boundIndex);
                    random = varLengthPattern.get(boundIndex);
                    randomPathMutationStrategy = 4;
                    if (optionalMatch){
                        result_qurey = result_qurey.substring(0, result_qurey.length()-15);
                        result_qurey += "MATCH ";
                    }
                }else {
                    varLengthPartition = false;
                    if (!inUnionPartition) {
                        if (optionalMatch) {
                            result_qurey = result_qurey.substring(0, result_qurey.length() - 23);
                            result_qurey += "MATCH ";
                            optionalMatch = false;
                        } else {
                            result_qurey = globalState.getDatabaseName().contains("mem") ? result_qurey.substring(0, result_qurey.length() - 11) : result_qurey.substring(0, result_qurey.length() - 14);
                            result_qurey += " MATCH ";
                        }
                    }
                }
            }


            for (int i = 0; i < ctx.pattern().patternPart().size(); i++) {
                CypherParser.PatternPartContext p = ctx.pattern().patternPart().get(i);
                //todo 随机选择一个模式拆分
                if (i == random) {

                    if (p.ASSIGN() != null) {
                        identifiers.add(format_full_text(p));
                    }
                    else {
                        CypherParser.PatternElemContext pe = p.patternElem();
                        while (pe.patternElem() != null)
                            pe = pe.patternElem();
                        //对于只有一个点的模式，只提取属性值
                        if (pe.patternElemChain().size() == 0) {
                            if (pe.nodePattern().properties() == null || pe.nodePattern().symbol() == null) {
                                identifiers.add(format_full_text(pe));
                            } else {
                                if (pe.nodePattern().nodeLabels() != null)
                                    identifiers.add("(" + pe.nodePattern().symbol().getText() + format_full_text(pe.nodePattern().nodeLabels().getText()) + ")");
                                else
                                    identifiers.add("(" + pe.nodePattern().symbol().getText() + ")");
                                for (CypherParser.MapPairContext m : pe.nodePattern().properties().mapLit().mapPair()) {
                                    single_node_patterns.add(pe.nodePattern().symbol().getText() + "." + m.name().getText() + "=" + getFullTextOriginal(m.expression()));
                                }
                            }
                        }
                        //对于有边的模式
                        else {
                            // 隐式匹配，将匹配条件迁移到where子句中
                            if (randomPathMutationStrategy < 1) {
                                String pattern = "";
                                CypherParser.NodePatternContext np = pe.nodePattern();
                                if (np.symbol() != null && np.nodeLabels() != null) {
                                    identifiers.add("(" + np.symbol().getText() + np.nodeLabels().getText() + ")");
                                } else if (np.symbol() != null) {
                                    identifiers.add("(" + np.symbol().getText() + ")");
                                } else if (np.nodeLabels() != null) {
                                    identifiers.add("(" + "nn" + this.ID + np.nodeLabels().getText() + ")");
                                    ID++;
                                } else {
                                    identifiers.add("(" + "nn" + this.ID + ")");
                                    ID++;
                                }
                                pattern += "(";
                                if (np.symbol() != null)
                                    pattern += np.symbol().getText();
                                else {
                                    ID--;
                                    pattern += "nn" + this.ID;
                                    ID++;
                                }
                                if (np.properties() != null)
                                    pattern += getFullTextOriginal(np.properties());
                                pattern += ")";
                                for (CypherParser.PatternElemChainContext pec : pe.patternElemChain()) {
                                    np = pec.nodePattern();
                                    CypherParser.RelationshipPatternContext rp = pec.relationshipPattern();
                                    String id = "";
                                    if (rp.relationDetail().symbol() != null) {
                                        id = rp.relationDetail().symbol().getText();
                                    } else {
                                        id = "m" + this.ID;
                                        this.ID++;
                                    }
                                    String lr_id = "";
                                    String rr_id = "";
                                    String lr_p = "";
                                    String rr_p = "";
                                    if (rp.LT() != null) {
                                        lr_id = "<-";
                                        lr_p = "<-";
                                        rr_id = "-";
                                        rr_p = "-";
                                    } else if (rp.GT() != null) {
                                        lr_id = "-";
                                        lr_p = "-";
                                        rr_id = "->";
                                        rr_p = "->";
                                    }
                                    //todo 模式拆分后的关系必须有方向
                                    else {
                                        lr_id = "<-";
                                        rr_id = "-";
                                        lr_p = "-";
                                        rr_p = "-";
                                    }
                                    String r = "()" + lr_id + "[" + id;
                                    if (rp.relationDetail().relationshipTypes() != null) {
                                        r += rp.relationDetail().relationshipTypes().getText();
                                    }
                                    if (rp.relationDetail().rangeLit() != null) {
                                        r += rp.relationDetail().rangeLit().getText();
                                    }
                                    if (rp.relationDetail().properties() != null) {
                                        r += getFullTextOriginal(rp.relationDetail().properties());
                                    }
                                    identifiers.add(r + "]" + rr_id + "()");
                                    if (rp.relationDetail().rangeLit() == null) {
                                        pattern += lr_p + "[" + id + "]" + rr_p;
                                    } else {
                                        pattern += lr_p + "[" + id + rp.relationDetail().rangeLit().getText() + "]" + rr_p;
                                    }
                                    if (np.symbol() != null && np.nodeLabels() != null) {
                                        identifiers.add("(" + np.symbol().getText() + np.nodeLabels().getText() + ")");
                                    } else if (np.symbol() != null) {
                                        identifiers.add("(" + np.symbol().getText() + ")");
                                    } else if (np.nodeLabels() != null) {
                                        identifiers.add("(" + "nn" + this.ID + np.nodeLabels().getText() + ")");
                                        ID++;
                                    } else {
                                        identifiers.add("(" + "nn" + this.ID + ")");
                                        ID++;
                                    }
                                    pattern += "(";
                                    if (np.symbol() != null)
                                        pattern += np.symbol().getText();
                                    else {
                                        ID--;
                                        pattern += "nn" + this.ID;
                                        ID++;
                                    }
                                    if (np.properties() != null)
                                        pattern += getFullTextOriginal(np.properties());
                                    pattern += ")";
                                }
                                patterns.add("(" + pattern + ")");
                            }
                            // 路径双重否定，将匹配条件迁移到where子句中，并通过双重否定过滤
                            else if (randomPathMutationStrategy < 2) {
                                String pattern = "";
                                CypherParser.NodePatternContext np = pe.nodePattern();
                                String tempIdentifiers;
                                if (np.symbol() != null && np.nodeLabels() != null) {
                                    tempIdentifiers = "(" + np.symbol().getText() + np.nodeLabels().getText() + ")";
                                    identifiers.add(tempIdentifiers);
                                    doubleNegationIdentifiers.add(tempIdentifiers);
                                } else if (np.symbol() != null) {
                                    tempIdentifiers = "(" + np.symbol().getText() + ")";
                                    identifiers.add(tempIdentifiers);
                                    doubleNegationIdentifiers.add(tempIdentifiers);
                                } else if (np.nodeLabels() != null) {
                                    tempIdentifiers = "(" + "nn" + this.ID + np.nodeLabels().getText() + ")";
                                    identifiers.add(tempIdentifiers);
                                    doubleNegationIdentifiers.add(tempIdentifiers);
                                    ID++;
                                } else {
                                    tempIdentifiers = "(" + "nn" + this.ID + ")";
                                    identifiers.add(tempIdentifiers);
                                    doubleNegationIdentifiers.add(tempIdentifiers);
                                    ID++;
                                }
                                pattern += "(";
                                if (np.symbol() != null)
                                    pattern += np.symbol().getText();
                                else {
                                    ID--;
                                    pattern += "nn" + this.ID;
                                    ID++;
                                }
                                if (np.properties() != null)
                                    pattern += getFullTextOriginal(np.properties());
                                pattern += ")";
                                for (CypherParser.PatternElemChainContext pec : pe.patternElemChain()) {
                                    np = pec.nodePattern();
                                    CypherParser.RelationshipPatternContext rp = pec.relationshipPattern();
                                    String id = "";
                                    if (rp.relationDetail().symbol() != null) {
                                        id = rp.relationDetail().symbol().getText();
                                    } else {
                                        id = "m" + this.ID;
                                        this.ID++;
                                    }
                                    String lr_id = "";
                                    String rr_id = "";
                                    String lr_p = "";
                                    String rr_p = "";
                                    if (rp.LT() != null) {
                                        lr_id = "<-";
                                        lr_p = "<-";
                                        rr_id = "-";
                                        rr_p = "-";
                                    } else if (rp.GT() != null) {
                                        lr_id = "-";
                                        lr_p = "-";
                                        rr_id = "->";
                                        rr_p = "->";
                                    }
                                    //todo 模式拆分后的关系必须有方向
                                    else {
                                        lr_id = "<-";
                                        rr_id = "-";
                                        lr_p = "-";
                                        rr_p = "-";
                                    }
                                    String r = "()" + lr_id + "[" + id;
                                    if (rp.relationDetail().relationshipTypes() != null) {
                                        r += rp.relationDetail().relationshipTypes().getText();
                                    }
                                    if (rp.relationDetail().rangeLit() != null) {
                                        r += rp.relationDetail().rangeLit().getText();
                                    }
                                    if (rp.relationDetail().properties() != null) {
                                        r += getFullTextOriginal(rp.relationDetail().properties());
                                    }
                                    identifiers.add(r + "]" + rr_id + "()");
                                    if (rp.relationDetail().rangeLit() == null) {
                                        pattern += lr_p + "[" + id + "]" + rr_p;
                                    } else {
                                        pattern += lr_p + "[" + id + rp.relationDetail().rangeLit().getText() + "]" + rr_p;
                                    }
                                    if (np.symbol() != null && np.nodeLabels() != null) {
                                        tempIdentifiers = "(" + np.symbol().getText() + np.nodeLabels().getText() + ")";
                                        identifiers.add(tempIdentifiers);
                                        doubleNegationIdentifiers.add(tempIdentifiers);
                                    } else if (np.symbol() != null) {
                                        tempIdentifiers = "(" + np.symbol().getText() + ")";
                                        identifiers.add(tempIdentifiers);
                                        doubleNegationIdentifiers.add(tempIdentifiers);
                                    } else if (np.nodeLabels() != null) {
                                        tempIdentifiers = "(" + "nn" + this.ID + np.nodeLabels().getText() + ")";
                                        identifiers.add(tempIdentifiers);
                                        doubleNegationIdentifiers.add(tempIdentifiers);
                                        ID++;
                                    } else {
                                        tempIdentifiers = "(" + "nn" + this.ID + ")";
                                        identifiers.add(tempIdentifiers);
                                        doubleNegationIdentifiers.add(tempIdentifiers);
                                        ID++;
                                    }
                                    pattern += "(";
                                    if (np.symbol() != null)
                                        pattern += np.symbol().getText();
                                    else {
                                        ID--;
                                        pattern += "nn" + this.ID;
                                        ID++;
                                    }
                                    if (np.properties() != null)
                                        pattern += getFullTextOriginal(np.properties());
                                    pattern += ")";
                                }
                                patterns.add("(" + pattern + ")");
                            }
                            // 匹配拆分，将匹配条件拆分成多个mathch语句 todo
                            // 可变长度路径匹配，将匹配模式变成可变长度的形式
                            else if (randomPathMutationStrategy < 4){
                                StringBuilder pattern = new StringBuilder();
//                                boolean shortestFlag = false;
//                                if (globalState.getDatabaseName().contains("neo4j") && ctx.pattern().patternPart().size() ==1 && r.getInteger(0,100)< 20){
//                                    pattern.append("ALL SHORTEST (");
//                                    shortestFlag = true;
//                                }
                                Pair<String, Pair<List<String>, Pair<Map<String, String>, List<String>>>> integrate_pattern_with_filter = pattern_path_integrate(pe, ID, path_id
                                        , avaliableIdentifiers.stream()
                                        .filter(id -> !avaliableLocalIdentifiers.contains(id))
                                        .collect(Collectors.toList()));

                                pattern.append(integrate_pattern_with_filter.getValue0());
//                                if (shortestFlag){
//                                    pattern.append(")");
//                                }
                                identifiers.add(pattern.toString());
                                patterns.addAll(integrate_pattern_with_filter.getValue1().getValue0());
                                withClauseFilter = integrate_pattern_with_filter.getValue1().getValue1().getValue0();
                                letClause = integrate_pattern_with_filter.getValue1().getValue1().getValue1();

                            }
                            // 可变长度路径拆分，将可变长度的匹配模式变成多个固定长度的模式的Union ALL
                            else if (randomPathMutationStrategy == 4){
                                StringBuilder pattern = new StringBuilder();
                                ArrayList<String> ids = new ArrayList<>();
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
                                for (int j = 0; j < pe.patternElemChain().size(); j++) {
                                    CypherParser.PatternElemChainContext pec = pe.patternElemChain().get(j);
                                    np = pec.nodePattern();
                                    CypherParser.RelationshipPatternContext rp = pec.relationshipPattern();
                                    String id = "";
                                    if (rp.relationDetail().symbol() != null) {
                                        id = rp.relationDetail().symbol().getText();
                                    }
                                    ids.add(id);
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
                                    if (j == varLengthPatternIndex){
                                        String target_relation_type = "";
                                        if (rp.relationDetail().relationshipTypes() != null) {
                                            target_relation_type = rp.relationDetail().relationshipTypes().getText();
                                        }
                                        for (int k = lowBound.get(boundIndex); k <= upperBound.get(boundIndex); k++) {
                                            if (k==0) continue;
                                            StringBuilder tempRel = new StringBuilder();
                                            if (r.getInteger(0,3) < 2) {
                                                for (int l = 0; l < k; l++) {
                                                    String randomMixingRelationExp = randomMixingExpression
                                                            .random_mixing_relation_type_expression(target_relation_type);
                                                    String identifier = "";

                                                    if (target_relation_type != ""){
                                                        identifier = "m" + ID;
                                                        ID++;
                                                        if (branchPatternLabelFilterList.get(k) == null) {
                                                            branchPatternLabelFilterList.put(k, new ArrayList<>());
                                                            branchPatternLabelFilterList.get(k).add(randomMixingExpression
                                                                    .target_relation_type_filter(target_relation_type, identifier));
                                                        }
                                                        else {
                                                            branchPatternLabelFilterList.get(k).add(randomMixingExpression
                                                                    .target_relation_type_filter(target_relation_type, identifier));
                                                        }

                                                    }

                                                    tempRel.append(lr_id).append("[")
                                                            .append(identifier)
                                                            .append(randomMixingRelationExp)
                                                            .append("]").append(rr_id);
                                                    if (l != k - 1) {
                                                        String randomMixingLabelExp = randomMixingExpression
                                                                .random_mixing_label_expression("");
                                                        tempRel.append("(").append(randomMixingLabelExp).append(")");
                                                    }
                                                }

                                            }else {
                                                tempRel.append(lr_id).append("[")
                                                        .append(target_relation_type)
                                                        .append("*").append(k)
                                                        .append("]").append(rr_id);
                                            }
                                            if (np.symbol() != null && np.nodeLabels() != null) {
                                                tempRel.append("(" + np.symbol().getText() + format_full_text(np.nodeLabels().getText()));
                                            } else if (np.symbol() != null) {
                                                tempRel.append("(" + np.symbol().getText());
                                            } else if (np.nodeLabels() != null) {
                                                String format_labels = format_full_text(np.nodeLabels().getText());

                                                tempRel.append("(" + format_labels);
                                            }
                                            if (np.properties() != null)
                                                tempRel.append(getFullTextOriginal(np.properties()));
                                            tempRel.append(")");
                                            varLengthPartitionIdentifierBuilders.add(new StringBuilder().append(pattern).append(tempRel));
                                        }
                                        pattern = new StringBuilder();
                                    }
                                    else {
                                        pattern.append(lr_id).append("[").append(id);
                                        if (rp.relationDetail().relationshipTypes() != null) {
                                            String format_relations = format_full_text(rp.relationDetail().relationshipTypes().getText());
                                            pattern.append(format_relations);
                                        }

                                        if (rp.relationDetail().rangeLit() != null) {
                                            pattern.append(rp.relationDetail().rangeLit().getText());
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
                                            String format_labels = format_full_text(np.nodeLabels().getText());
                                            pattern.append("(" + format_labels);
                                        }
                                        if (np.properties() != null)
                                            pattern.append(getFullTextOriginal(np.properties()));
                                        pattern.append(")");
                                    }
                                }
                                for (int j = 0; j <= upperBound.get(boundIndex)-lowBound.get(boundIndex); j++) {
                                    varLengthPartitionIdentifiers.add(varLengthPartitionIdentifierBuilders.get(j).append(pattern).toString());
                                    StringBuilder branchFilterBuilder = new StringBuilder();
                                    String branchFilter = "";
                                    int k = j + lowBound.get(boundIndex);
                                    if (branchPatternLabelFilterList.get(k) != null) {
                                        branchFilterBuilder.append("WHERE ");
                                        for (String branchLabelFilter: branchPatternLabelFilterList.get(k)){
                                            branchFilterBuilder.append(branchLabelFilter);
                                        }

                                        if (ctx.where() != null) {
                                            branchFilterBuilder.append("(")
                                                    .append(getFullTextOriginal(ctx.where().expression()))
                                                    .append(")");
                                            varBranchFilter.add(branchFilterBuilder.toString());
                                        }else {
                                            branchFilter = branchFilterBuilder.substring(0, branchFilterBuilder.length()-4);
                                            varBranchFilter.add(branchFilter);

                                        }
                                    }else if (ctx.where() != null) {
                                        branchFilterBuilder.append(getFullTextOriginal(ctx.where()));
                                        varBranchFilter.add(branchFilterBuilder.toString());
                                    }else if (r.getInteger(0, 9) == 0 && !(globalState.getDatabaseName().contains("nebula") && ((CypherParser.MatchStContext) ctx.parent).OPTIONAL() != null)) {
                                        varBranchFilter.add(where_operator.get(0));
                                    }else {
                                        varBranchFilter.add("");
                                    }


                                }
                                identifiers.add(pattern.toString());
                            }
                            // 没有可用的变异策略，不进行任何变异
                            else{
                                identifiers.add(format_full_text(p));
                            }
                        }
                    }
                }
                //todo 不拆分
                else {
                    identifiers.add(format_full_text(p));
                }
            }

            if (varLengthPartition){
                if (inUnionPartition){
                    String branchPatternClause = "";
                    unionPartitionIndex = result_qurey.length();
                    int branchPartitionIndex = 0;
                    int branchOrigLength = 0;
                    for (int j = 0; j < identifiers.size(); j++) {
                        if (j != random){
                            branchPatternClause += identifiers.get(j) + ",";
                        }else {
                            branchPartitionIndex = branchPatternClause.length();
                            branchOrigLength = varLengthPartitionIdentifiers.get(0).length();
                            branchPatternClause += varLengthPartitionIdentifiers.get(0) + ",";;
                        }
                    }
                    branchPatternClause = branchPatternClause.substring(0, branchPatternClause.length()-1) + " ";
                    result_qurey += branchPatternClause + varBranchFilter.get(0) + " ";

                    result_qurey += "WITH ";

                    for (String avaliableIdentifier : avaliableIdentifiers) {
                        result_qurey += avaliableIdentifier + ", ";
                    }
                    result_qurey = result_qurey.substring(0, result_qurey.length()-2)+ " ";;
                    isUnionPartition = false;
                    for (int j = 0; j < varLengthPartitionIdentifiers.size(); j++) {
                        unionPartitionPattern.add(branchPatternClause.substring(0, branchPartitionIndex) +
                                varLengthPartitionIdentifiers.get(j) +
                                branchPatternClause.substring(branchPartitionIndex + branchOrigLength) +
                                varBranchFilter.get(j));

                    }

                }else {
                    for (int i = 0; i <= upperBound.get(boundIndex) - lowBound.get(boundIndex); i++) {
                        if (i != 0){
                            result_qurey += "MATCH ";
                        }
                        for (int j = 0; j < identifiers.size(); j++) {
                            if (j != random){
                                result_qurey += identifiers.get(j) + ",";
                            }else {
                                result_qurey += varLengthPartitionIdentifiers.get(i) + ",";;
                            }
                        }
                        result_qurey = result_qurey.substring(0, result_qurey.length()-1) + " ";
                        if (r.getInteger(0, 6) == 1 && ((CypherParser.MatchStContext) ctx.parent).OPTIONAL() == null && globalState.getOptions().getRelation_removed() != 2)
                            result_qurey += with_operator.get(0);

                        result_qurey += varBranchFilter.get(i) + " ";

                        result_qurey += "RETURN ";
                        for (int k = 0; k < avaliableLocalIdentifiers.size(); k++){
                            result_qurey += avaliableLocalIdentifiers.get(k) + ",";
                        }
                        if (avaliableLocalIdentifiers.isEmpty()){
                            result_qurey += call_return_operator.get(0) + call_return_id + " ";
                        }
                        result_qurey = result_qurey.substring(0, result_qurey.length()-1) + " ";
                        if (i != upperBound.get(boundIndex) - lowBound.get(boundIndex)){
                            result_qurey += "UNION ALL ";
                        }
                    }

                    if (avaliableLocalIdentifiers.isEmpty()){
                        call_return_id++;
                    }
                }
            }
            else {
                if (identifiers.isEmpty())
                    result_qurey += "() ";
                else {
                    for (String i : identifiers)
                        result_qurey += i + ",";
                    result_qurey = result_qurey.substring(0, result_qurey.length() - 1) + " ";
                }
                //todo 延迟过滤策略只能应用于MATCH而不能用于OPTIONAL MATCH，详情见OPTIONAL MATCH文档
                if (r.getInteger(0, 6) == 1 && ((CypherParser.MatchStContext) ctx.parent).OPTIONAL() == null && globalState.getOptions().getRelation_removed() != 2)
                    result_qurey += with_operator.get(0);
                if (!patterns.isEmpty() || !single_node_patterns.isEmpty()) {
                    if (!letClause.isEmpty()){
                        if (globalState.getDatabaseName().contains("neo4j")){
                            result_qurey += "LET ";
                            for (String letExp: letClause){
                                result_qurey += letExp + ",";
                            }
                            result_qurey = result_qurey.substring(0, result_qurey.length()-1) + " ";
                            result_qurey += "FILTER ";
                            isFilterUsed = true;
                        }else {
                            result_qurey += "WITH *, ";
                            for (String letExp: letClause){
                                result_qurey += letExp + ",";
                            }
                            result_qurey = result_qurey.substring(0, result_qurey.length()-1) + " ";
                            result_qurey += "WHERE ";
                        }

                    }else {
                        result_qurey += "WHERE ";
                    }

                    for (String p : patterns) {
                        if (randomPathMutationStrategy < 1) {
                            result_qurey += "exists" + p + " AND ";
                        } else if (randomPathMutationStrategy < 2) {
                            result_qurey += "NOT exists{";
                            for (String i : doubleNegationIdentifiers) {
                                result_qurey += i + ",";
                            }
                            result_qurey = result_qurey.substring(0, result_qurey.length() - 1) + " ";
                            result_qurey += "WHERE NOT " + p + "} AND ";

                        } else if (randomPathMutationStrategy < 5) {
                            result_qurey += p;
                        }
                    }
                    for (String s : single_node_patterns)
                        result_qurey += s + " AND ";

                    if (ctx.where() != null){
                        result_qurey += "(" +
                                replaceAllSymbolWithNew(getFullTextOriginal(ctx.where().expression()), withClauseFilter) +
                                ") ";

                    }else {
                        result_qurey = result_qurey.substring(0, result_qurey.length() - 4);
                    }

                    result_qurey += "WITH ";
                    List<String> usedIdentifier = new ArrayList<>();
                    for (String withFilter: withClauseFilter.keySet()){
                        result_qurey += withClauseFilter.get(withFilter) + " AS " + withFilter + ", ";
                        usedIdentifier.add(withFilter);
                    }
                    for (String avaliableIdentifier : avaliableIdentifiers){
                        if (!usedIdentifier.contains(avaliableIdentifier)){
                            result_qurey += avaliableIdentifier + ", ";
                        }
                    }
                    result_qurey = result_qurey.substring(0, result_qurey.length() - 2) + " ";

                } else if (ctx.where() != null) {
                    visitChildren(ctx.where());
                } else if (r.getInteger(0, 9) == 0 && !(globalState.getDatabaseName().contains("nebula") && ((CypherParser.MatchStContext) ctx.parent).OPTIONAL() != null))
                    result_qurey += where_operator.get(0);
            }
        }
        //不进行模式拆分
        else {
            for (int i = 0; i < ctx.pattern().patternPart().size(); i++) {
                CypherParser.PatternPartContext patternPartContext = ctx.pattern().patternPart(i);
                result_qurey += format_full_text(patternPartContext);
                if (i < ctx.pattern().patternPart().size() - 1)
                    result_qurey += ",";
            }
            //todo 延迟过滤策略
            if (r.getInteger(0, 6) == 1 && ((CypherParser.MatchStContext) ctx.parent).OPTIONAL() == null && globalState.getOptions().getRelation_removed() != 2)
                result_qurey += with_operator.get(0);
            if (ctx.where() != null)
                visitChildren(ctx.where());
                //变异where true
            else if (r.getInteger(0, 9) == 0 && !(globalState.getDatabaseName().contains("nebula") && ((CypherParser.MatchStContext) ctx.parent).OPTIONAL() != null)) {
                result_qurey += where_operator.get(0);
            }
        }
        return null;
    }

    private String replaceAllSymbolWithNew(String expression, Map<String, String> symbolMap) {
        for (Map.Entry<String, String> entry : symbolMap.entrySet()) {
            String oldSymbol = entry.getKey();
            String newSymbol = entry.getValue();

            // Cypher identifier boundary
            String pattern = "(?<![A-Za-z0-9_])"
                    + Pattern.quote(oldSymbol)
                    + "(?![A-Za-z0-9_])";

            expression = expression.replaceAll(pattern, newSymbol);
        }
        return expression;
    }

    @Override
    public String visitMatchSt(CypherParser.MatchStContext ctx) {
        // update global and local identifier list
        updateIdentifiers(ctx.patternWhere());

        // todo temporal index creation
        int varPartitionStrategy = r.getInteger(0,6);
        if (varPartitionStrategy < 4) {
            if (isUnionPartition && ctx.OPTIONAL() == null){
                varLengthPartition = true;
                inUnionPartition = true;

                visitChildren(ctx);

                varLengthPartition = false;
                inUnionPartition = false;
            }
            // janusgraph 和 agensgraph、nebula不支持call subquery
            else if (!globalState.getDatabaseName().contains("janus") && !globalState.getDatabaseName().contains("agens") && !globalState.getDatabaseName().contains("nebula")){
                varLengthPartition = true;
                if (ctx.OPTIONAL() == null){
                    result_qurey += globalState.getDatabaseName().contains("mem")?"CALL{":"CALL(*){";
                }else {
                    // memgraph 不支持 OPTIONAL CALL
                    if (globalState.getDatabaseName().contains("mem")){
                        varLengthPartition = false;
                    }else{
                        result_qurey += "OPTIONAL CALL(*){";
                        optionalMatch = true;
                    }
                }
                visitChildren(ctx);
                if (varLengthPartition){
                    result_qurey += "}";
                    // 是否变成transaction
                    if (globalState.getDatabaseName().contains("neo4j") && r.getInteger(0,2) == 1 && !isUnionPartition && unionPartitionPattern.isEmpty()) {
                        if (r.getInteger(0,2) < 1) {
                            result_qurey += " IN TRANSACTIONS ";
                        }else {
                            result_qurey += " IN TRANSACTIONS OF " + r.getInteger(1, 5) + " ROW ";
                        }
                    }
                    varLengthPartition = false;
                    optionalMatch = false;
                }
            }else {
                visitChildren(ctx);
            }
        }
        else{
            visitChildren(ctx);
        }
        // clear local identifiers
        avaliableLocalIdentifiersClear();
        return null;
    }

    @Override
    public String visitUnwindSt(CypherParser.UnwindStContext ctx) {
        visitChildren(ctx);
        updateIdentifiers(ctx);
        return null;
    }


    @Override
    public String visitReadingStatement(CypherParser.ReadingStatementContext ctx) {
        Boolean next_reading = false;
        //标识下一个子句是否是unwind.match,call
        if (ctx.parent instanceof CypherParser.SingleQueryContext) {
            List<ParseTree> l = ((CypherParser.SingleQueryContext) ctx.parent).children;
            int size = l.size();
            for (int i = 0; i < size - 1; i++) {
                if (l.get(i).equals(ctx) && l.get(i + 1) instanceof CypherParser.ReadingStatementContext) {
                    next_reading = true;
                    break;
                }
            }
        }
        visitChildren(ctx);
        if (true) {
            //with mutation
            if (r.getInteger(0, 6) == 0 && !(ctx.parent instanceof CypherParser.ForeachStContext) && globalState.getOptions().getRelation_removed() != 2) {
                result_qurey += with_operator.get(0);
            }
            //unwind
            else if (r.getInteger(0, 6) == 1 && !(ctx.parent instanceof CypherParser.ForeachStContext) && globalState.getOptions().getRelation_removed() != 2) {
                if (globalState.getDatabaseName().contains("mem") || globalState.getDatabaseName().contains("agens") || globalState.getDatabaseName().contains("janus"))
                    result_qurey += Randomly.fromList(unwind_operator_mem) + unwind_id + " ";
                else
                    result_qurey += Randomly.fromList(unwind_operator) + unwind_id + " ";
                unwind_id++;
            }
        }
        return null;
    }

    @Override
    public String visitWithSt(CypherParser.WithStContext ctx) {
        Boolean next_reading = false;
        //标识下一个子句是否是match,unwind,call
        if (ctx.parent instanceof CypherParser.SingleQueryContext) {
            List<ParseTree> l = ((CypherParser.SingleQueryContext) ctx.parent).children;
            int size = l.size();
            for (int i = 0; i < size - 1; i++) {
                if (l.get(i).equals(ctx) && l.get(i + 1) instanceof CypherParser.ReadingStatementContext) {
                    next_reading = true;
                    break;
                }
            }
        }
        result_qurey += "WITH ";
        updateIdentifiers(ctx);
        visitChildren(ctx.projectionBody());
        if (ctx.where() != null)
            visitChildren(ctx.where());
        else {
            if (r.getInteger(0, 9) == 0 && !(ctx.parent.parent instanceof CypherParser.QueryCallStContext))
                result_qurey += where_operator.get(0);
        }
        if (true) {
            //with mutation
            if (r.getInteger(0, 9) == 0 && !(ctx.parent instanceof CypherParser.ForeachStContext) && globalState.getOptions().getRelation_removed() != 2) {
                result_qurey += with_operator.get(0);
            }
            //unwind mutation
            else if (r.getInteger(0, 9) == 0 && !(ctx.parent instanceof CypherParser.ForeachStContext) && globalState.getOptions().getRelation_removed() != 2) {
                if (globalState.getDatabaseName().contains("mem") || globalState.getDatabaseName().contains("agens") || globalState.getDatabaseName().contains("janus"))
                    result_qurey += Randomly.fromList(unwind_operator_mem) + unwind_id + " ";
                else
                    result_qurey += Randomly.fromList(unwind_operator) + unwind_id + " ";
                unwind_id++;
            }
        }
        return null;
    }

    @Override
    public String visitWhere(CypherParser.WhereContext ctx) {
        in_where = true;
        visitChildren(ctx);
        in_where = false;
        return null;
    }

    @Override
    public String visitProjectionBody(CypherParser.ProjectionBodyContext ctx) {
        if (ctx.DISTINCT() != null)
            result_qurey += "DISTINCT ";
            //distinct mutation, todo:gui 暂时不考虑改变执行结果数量的情况；
//        else if (r.getInteger(0, 9) == 0 && globalState.getOptions().getRelation_removed() != 3 && !(ctx.parent instanceof CypherParser.WithStContext)) {
//            result_qurey += distinct_operator.get(0);
//            DifferentialNonEmptyBranchOracle.result_changed = true;
//        }
        visitChildren(ctx.projectionItems());
        //order by mutation
        if (ctx.orderSt() == null && r.getInteger(0, 9) == 0 && globalState.getOptions().getRelation_removed() != 3 && !globalState.getDatabaseName().contains("agens")) {
            result_qurey += Randomly.fromList(order_operator);
            //desc mutation
            if (r.getInteger(0, 9) == 0)
                result_qurey += desc_operator.get(0);
        }
        if (ctx.orderSt() != null) {
            result_qurey += getFullTextOriginal(ctx.orderSt()) + " ";
            if (r.getInteger(0, 9) == 0 && globalState.getOptions().getRelation_removed() != 3 && !globalState.getDatabaseName().contains("agens")) {
                result_qurey += Randomly.fromList(order_operator_add);
                //desc mutation
                if (r.getInteger(0, 9) == 0)
                    result_qurey += desc_operator.get(0);
            } else if (ctx.orderSt().orderItem().get(ctx.orderSt().orderItem().size() - 1).DESC() == null && r.getInteger(0, 9) == 0)
                result_qurey += desc_operator.get(0);
        }
        //skip mutation
        if (ctx.skipSt() != null)
            visitChildren(ctx.skipSt());
//        else if (r.getInteger(0, 9) == 0 && globalState.getOptions().getRelation_removed() != 3) {
//            result_qurey += Randomly.fromList(skip_operator);
//            DifferentialNonEmptyBranchOracle.result_changed = true;
//        }
        //limit mutation
        if (ctx.limitSt() != null)
            visitChildren(ctx.limitSt());
//        else if (r.getInteger(0, 9) == 0 && globalState.getOptions().getRelation_removed() != 3) {
//            result_qurey += Randomly.fromList(limit_operator);
//            DifferentialNonEmptyBranchOracle.result_changed = true;
//        }
        return null;
    }

    @Override
    public String visitExpression(CypherParser.ExpressionContext ctx) {
        //排除exists()函数的参数
        if (ctx.parent.parent instanceof CypherParser.FunctionInvocationContext && ((CypherParser.FunctionInvocationContext) ctx.parent.parent).functionname().EXISTSF() != null)
            return visitChildren(ctx);
        else
            return visitChildren(ctx);
    }

    //list add、string add、boolean add变异
    @Override
    public String visitFunctionInvocation(CypherParser.FunctionInvocationContext ctx) {
        if (list_function.contains(ctx.functionname().getText()) && r.getInteger(0, 9) == 0) {
            Boolean b = Randomly.getBoolean();
            if (b) {
                result_qurey += list_add_operator.get(0);
                visitChildren(ctx);
                result_qurey += ")";
            }
            if (!b) {
                result_qurey += "(";
                visitChildren(ctx);
                result_qurey += list_add_operator.get(1);
            }
            return null;
        } else if (string_function.contains(ctx.functionname().getText()) && r.getInteger(0, 9) == 0) {
            Boolean b = Randomly.getBoolean();
            if (b) {
                result_qurey += string_add_operator.get(0);
                visitChildren(ctx);
                result_qurey += ")";
            }
            if (!b) {
                result_qurey += "(";
                visitChildren(ctx);
                result_qurey += string_add_operator.get(1);
            }
            return null;
        } else if (boolean_function.contains(ctx.functionname().getText()) && r.getInteger(0, 9) == 0) {
            Boolean b = Randomly.getBoolean();
            if (b)
                result_qurey += Randomly.fromList(bool_add_operator.subList(0, 2));
            visitChildren(ctx);
            if (!b)
                result_qurey += Randomly.fromList(bool_add_operator.subList(2, 4));
            return null;
        }
        return visitChildren(ctx);
    }

    //string add变异
    @Override
    public String visitStringLit(CypherParser.StringLitContext ctx) {
        int b = r.getInteger(0, 9);
        if (b == 1) {
            result_qurey += string_add_operator.get(0);
            visitChildren(ctx);
            result_qurey += ")";
        } else if (b == 0) {
            result_qurey += "(";
            visitChildren(ctx);
            result_qurey += string_add_operator.get(1);
        } else
            visitChildren(ctx);
        return null;
    }

    //boolean add变异
    @Override
    public String visitBoolLit(CypherParser.BoolLitContext ctx) {
        int b = r.getInteger(0, 9);
        if (b == 1) {
            result_qurey += Randomly.fromList(bool_add_operator.subList(0, 2));
            visitChildren(ctx);
        } else if (b == 0) {
            visitChildren(ctx);
            result_qurey += Randomly.fromList(bool_add_operator.subList(2, 4));
        } else
            visitChildren(ctx);
        return null;
    }

    @Override
    public String visitSingleQuery(CypherParser.SingleQueryContext ctx) {
        visitChildren(ctx);
        //进行return 变异
        if (r.getInteger(0, 6) == 0 && ctx.returnSt() == null && globalState.getOptions().getRelation_removed() != 3)
            result_qurey += Randomly.fromList(return_operator);
        return null;
    }

    @Override
    public String visitSubqueryExist(CypherParser.SubqueryExistContext ctx) {
        //todo 进行子查询转化变异
        if (r.getInteger(0, 6) == 0) {
            result_qurey += "(COUNT" + getFullTextOriginal(ctx).substring(6) + ">0) ";
            return null;
        } else {
            in_subquery = true;
            int b = r.getInteger(0, 9);
            //进行boolean add 变异
            if (b == 0)
                result_qurey += Randomly.fromList(bool_add_operator.subList(0, 2));
            for (int i = 0; i < ctx.children.size() - 1; i++)
                ctx.getChild(i).accept(this);
            //进行return 变异
            if (r.getInteger(0, 9) == 0 && ctx.returnSt() == null && globalState.getOptions().getRelation_removed() != 3) {
                result_qurey += Randomly.fromList(return_operator);
            }
            result_qurey += "}";
            if (b == 1)
                result_qurey += Randomly.fromList(bool_add_operator.subList(2, 4));
            in_subquery = false;
            return null;
        }
    }

    @Override
    public String visitSubqueryCount(CypherParser.SubqueryCountContext ctx) {
        in_subquery = true;
        for (int i = 0; i < ctx.children.size() - 1; i++)
            ctx.getChild(i).accept(this);
        //进行return 变异
        if (r.getInteger(0, 9) == 0 && ctx.returnSt() == null && globalState.getOptions().getRelation_removed() != 3) {
            result_qurey += Randomly.fromList(return_operator);
        }
        result_qurey += "}";
        in_subquery = false;
        return null;
    }



    //todo 其他不需要重载的函数
//    @Override public String visitQuery(CypherParser.QueryContext ctx) {return visitChildren(ctx);}
//    @Override public String visitRegularQuery(CypherParser.RegularQueryContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSkipSt(CypherParser.SkipStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitLimitSt(CypherParser.LimitStContext ctx) { return visitChildren(ctx); }
    @Override public String visitScript(CypherParser.ScriptContext ctx) {
        avaliableIdentifiers.clear();
        if (ctx.query().regularQuery().singleQuery().returnSt().projectionBody().DISTINCT() != null) {
            this.isDistinctReturn = true;
        }
        return visitChildren(ctx) ;
    }

//    @Override public String visitProjectionBody(CypherParser.ProjectionBodyContext ctx) { return visitChildren(ctx); }
//    @Override public String visitProjectionItems(CypherParser.ProjectionItemsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitProjectionItem(CypherParser.ProjectionItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitOrderItem(CypherParser.OrderItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitOrderSt(CypherParser.OrderStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRemoveSt(CypherParser.RemoveStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRemoveItem(CypherParser.RemoveItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitParenExpressionChain(CypherParser.ParenExpressionChainContext ctx) { return visitChildren(ctx); }
//    @Override public String visitYieldItems(CypherParser.YieldItemsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitYieldItem(CypherParser.YieldItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMergeSt(CypherParser.MergeStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMergeAction(CypherParser.MergeActionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSetSt(CypherParser.SetStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSetItem(CypherParser.SetItemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNodeLabels(CypherParser.NodeLabelsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCreateSt(CypherParser.CreateStContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPattern(CypherParser.PatternContext ctx) { return visitChildren(ctx); }
//    @Override public String visitXorExpression(CypherParser.XorExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitAndExpression(CypherParser.AndExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNotExpression(CypherParser.NotExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitComparisonExpression(CypherParser.ComparisonExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitComparisonSigns(CypherParser.ComparisonSignsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitAddSubExpression(CypherParser.AddSubExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMultDivExpression(CypherParser.MultDivExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPowerExpression(CypherParser.PowerExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitUnaryAddSubExpression(CypherParser.UnaryAddSubExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitAtomicExpression(CypherParser.AtomicExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitListExpression(CypherParser.ListExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitStringExpression(CypherParser.StringExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitStringExpPrefix(CypherParser.StringExpPrefixContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNullExpression(CypherParser.NullExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPropertyOrLabelExpression(CypherParser.PropertyOrLabelExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPropertyExpression(CypherParser.PropertyExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPatternElem(CypherParser.PatternElemContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPatternElemChain(CypherParser.PatternElemChainContext ctx) { return visitChildren(ctx); }
//    @Override public String visitProperties(CypherParser.PropertiesContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNodePattern(CypherParser.NodePatternContext ctx) { return visitChildren(ctx); }
//    @Override public String visitAtom(CypherParser.AtomContext ctx) { return visitChildren(ctx); }
//    @Override public String visitLhs(CypherParser.LhsContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationshipPattern(CypherParser.RelationshipPatternContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationDetail(CypherParser.RelationDetailContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationshipTypes(CypherParser.RelationshipTypesContext ctx) { return visitChildren(ctx); }
//    @Override public String visitFunctionname(CypherParser.FunctionnameContext ctx) { return visitChildren(ctx); }
//    @Override public String visitParenthesizedExpression(CypherParser.ParenthesizedExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitFilterWith(CypherParser.FilterWithContext ctx) { return visitChildren(ctx); }
//    @Override public String visitPatternComprehension(CypherParser.PatternComprehensionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRelationshipsChainPattern(CypherParser.RelationshipsChainPatternContext ctx) { return visitChildren(ctx); }
//    @Override public String visitListComprehension(CypherParser.ListComprehensionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitFilterExpression(CypherParser.FilterExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCountAll(CypherParser.CountAllContext ctx) { return visitChildren(ctx); }
//    @Override public String visitExpressionChain(CypherParser.ExpressionChainContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCaseExpression(CypherParser.CaseExpressionContext ctx) { return visitChildren(ctx); }
//    @Override public String visitParameter(CypherParser.ParameterContext ctx) { return visitChildren(ctx); }
//    @Override public String visitLiteral(CypherParser.LiteralContext ctx) { return visitChildren(ctx); }
//    @Override public String visitRangeLit(CypherParser.RangeLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitNumLit(CypherParser.NumLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitCharLit(CypherParser.CharLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMapLit(CypherParser.MapLitContext ctx) { return visitChildren(ctx); }
//    @Override public String visitMapPair(CypherParser.MapPairContext ctx) { return visitChildren(ctx); }
//    @Override public String visitName(CypherParser.NameContext ctx) { return visitChildren(ctx); }
//    @Override public String visitSymbol(CypherParser.SymbolContext ctx) { return visitChildren(ctx); }
//    @Override public String visitReservedWord(CypherParser.ReservedWordContext ctx) { return visitChildren(ctx); }
    private void refresh_schema_info(){
        S schema = globalState.getSchema();
        randomMixingExpression = new RandomMixingExpression(schema.getLabelInfos(), schema.getRelationshipTypeInfos());
        randomMixingExpression.setdatabasename(globalState.getDatabaseName());
        PatternPathIntegrator.setDatabaseName(globalState.getDatabaseName());
        PatternFormater.setDatabaseName(globalState.getDatabaseName());
    }





    public void variable_length_relation_filter(){
        // todo: do something to generate a condition expression, which filter the variable-length relation's label in the path
    }

    public static String transform(CypherVisitorImp cv, String input) {
        CypherLexer lexer = new CypherLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CypherParser parser = new CypherParser(tokens);
        ParseTree tree = parser.script();

        parser.setBuildParseTree(true);
        cv.visit(tree);
        return cv.Getresult_qurey();
    }

    public static void main(String[] args) {
        String input = "MATCH (a:User {name: \"Alice\"}) -[]->(b:n{name:1}), (b) WHERE a.age>0 AND 1 + a RETURN [b IN [(a)-[:FOLLOWS]->(b) | b] WHERE b:User | b.name] AS following";
        System.out.println(transform(new CypherVisitorImp(), input));
        if (1 > 0){
            int a = 0;
        }
        int b = 0;
    }
}
