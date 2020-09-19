package dynamite.exp;

import java.util.ArrayList;
import java.util.List;

public final class IntegrityConstraintParser {

    /**
     * Parse integrity constraints from given text.
     *
     * @param text the given text
     * @return the parsed integrity constraint
     */
    public static IntegrityConstraint parse(String text) {
        List<ForeignKey> foreignKeys = new ArrayList<>();
        List<CompoundFK> compoundFKs = new ArrayList<>();
        List<Selection> selections = new ArrayList<>();
        List<Equality> equalities = new ArrayList<>();
        String[] lines = text.split("\\R+");
        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            } else if (isCompoundFK(line)) {
                // NOTE: this must appear before `isForeignKey`
                compoundFKs.add(parseCompoundFK(line));
            } else if (isForeignKey(line)) {
                foreignKeys.add(parseForeignKey(line));
            } else if (isSelection(line)) {
                selections.add(parseSelection(line));
            } else if (isEquality(line)) {
                equalities.add(parseEquality(line));
            } else {
                throw new RuntimeException("Unknown integrity constraint: " + line);
            }
        }
        return new IntegrityConstraint(foreignKeys, compoundFKs, selections, equalities);
    }

    public static boolean isForeignKey(String text) {
        return text.contains("->");
    }

    public static ForeignKey parseForeignKey(String text) {
        String[] tokens = text.split("->");
        String from = tokens[0].trim();
        String to = tokens[1].trim();
        return new ForeignKey(from, to);
    }

    public static boolean isCompoundFK(String text) {
        return text.contains("-->");
    }

    public static CompoundFK parseCompoundFK(String text) {
        String[] tokens = text.split("-->");
        String from = tokens[0].trim();
        String to = tokens[1].trim();
        List<String> fromAttrs = parseStringList(from);
        List<String> toAttrs = parseStringList(to);
        return new CompoundFK(fromAttrs, toAttrs);
    }

    public static boolean isSelection(String text) {
        return text.contains("<-");
    }

    public static Selection parseSelection(String text) {
        String[] tokens = text.split("<-");
        String attribute = tokens[0].trim();
        String valueText = tokens[1].trim();
        List<String> values = parseStringList(valueText);
        return new Selection(attribute, values);
    }

    public static boolean isEquality(String text) {
        return text.contains("=");
    }

    public static Equality parseEquality(String text) {
        String[] tokens = text.split("=");
        String from = tokens[0].trim();
        String other = tokens[1].trim();
        String[] otherTokens = other.split(" ");
        String to = otherTokens[0].trim();
        String replace = otherTokens[1].trim();
        return new Equality(from, to, Boolean.parseBoolean(replace));
    }

    private static List<String> parseStringList(String text) {
        assert text.charAt(0) == '[';
        assert text.charAt(text.length() - 1) == ']';
        text = text.substring(1, text.length() - 1);
        List<String> values = new ArrayList<>();
        for (String value : text.split(",")) {
            values.add(value);
        }
        return values;
    }

}
