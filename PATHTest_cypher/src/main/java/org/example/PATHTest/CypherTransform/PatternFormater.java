package org.example.PATHTest.CypherTransform;

import org.example.PATHTest.parsercypher.CypherParser;

import java.util.ArrayList;
import java.util.List;

import static org.example.PATHTest.parserutil.parserUtils.getFullTextOriginal;

/**
 * @ClassName PatternFormater
 * @Description TODO
 * date 2025/12/19 14:19
 * version V1.0
 **/
public class PatternFormater {
    private static String databaseName;

    public static void setDatabaseName(String databaseName){
        PatternFormater.databaseName = databaseName;
    }

    public static String format_full_text(CypherParser.PatternPartContext patternPartContext){
        if (!databaseName.contains("neo4j")){
            return getFullTextOriginal(patternPartContext);
        }
        StringBuffer sb = new StringBuffer();
        if (patternPartContext.ASSIGN() != null) {
            sb.append(patternPartContext.symbol().getText()).append("=");
        }
        sb.append(format_full_text(patternPartContext.patternElem()));
        return sb.toString();


    }

    public static String format_full_text(CypherParser.PatternElemContext patternElemContext){
        if (!databaseName.contains("neo4j")){
            return getFullTextOriginal(patternElemContext);
        }
        StringBuffer sb = new StringBuffer();
        CypherParser.NodePatternContext np = patternElemContext.nodePattern();

        if (np.nodeLabels() != null && np.nodeLabels().COLON().size() > 1) {
            if (np.symbol() != null && np.nodeLabels() != null) {
                sb.append("(").append(np.symbol().getText()).append(format_full_text(np.nodeLabels().getText()));
            } else if (np.symbol() != null) {
                sb.append("(").append(np.symbol().getText());
            } else if (np.nodeLabels() != null) {
                sb.append("(").append(format_full_text(np.nodeLabels().getText()));
            }
            sb.append(")");
        }else {
            sb.append(getFullTextOriginal(np));
        }

        for (CypherParser.PatternElemChainContext patternElemChainContext : patternElemContext.patternElemChain()) {
            sb.append(format_full_text(patternElemChainContext));
        }
        return sb.toString();
    }

    public static String format_full_text(CypherParser.PatternElemChainContext patternElemChainContext){
        if (!databaseName.contains("neo4j")){
            return getFullTextOriginal(patternElemChainContext);
        }
        StringBuffer sb = new StringBuffer();
        CypherParser.NodePatternContext np = patternElemChainContext.nodePattern();
        CypherParser.RelationshipPatternContext rp = patternElemChainContext.relationshipPattern();
        sb.append(getFullTextOriginal(rp));

        if (np.nodeLabels() != null && np.nodeLabels().COLON().size() > 1) {
            if (np.symbol() != null && np.nodeLabels() != null) {
                sb.append("(").append(np.symbol().getText()).append(format_full_text(np.nodeLabels().getText()));
            } else if (np.symbol() != null) {
                sb.append("(").append(np.symbol().getText());
            } else if (np.nodeLabels() != null) {
                sb.append("(").append(format_full_text(np.nodeLabels().getText()));
            }
            sb.append(")");

        }else {
            sb.append(getFullTextOriginal(np));
        }

        return sb.toString();
    }

    public static String format_full_text(String orig_label_exp){
        if (!databaseName.contains("neo4j")){
            return orig_label_exp;
        }
        if (orig_label_exp == null) {
            return orig_label_exp;
        }

        String trimmed = orig_label_exp.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }

        // Fast path: only one colon -> already single label
        if (trimmed.indexOf(':') == trimmed.lastIndexOf(':')) {
            return trimmed;
        }

        // Split by colon, collect labels
        List<String> labels = new ArrayList<>();
        for (String part : trimmed.split(":")) {
            String label = part.trim();
            if (!label.isEmpty()) {
                labels.add(label);
            }
        }

        // Build :L0&L1&L2
        StringBuilder sb = new StringBuilder();
        sb.append(':');

        boolean first = true;
        for (String label : labels) {
            if (!first) {
                sb.append('&');
            }
            sb.append(label);
            first = false;
        }

        return sb.toString();
    }


}
