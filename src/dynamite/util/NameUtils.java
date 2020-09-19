package dynamite.util;

public final class NameUtils {

    /**
     * Compute the record type name (relation name in Datalog) given
     * a canonical attribute name.
     *
     * @param canonicalName the specified canonical name of an attribute
     * @return name of the record type that contains the attribute
     */
    public static String computeRecordTypeName(String canonicalName) {
        int index = canonicalName.lastIndexOf('?');
        if (index == -1) {
            throw new IllegalArgumentException("Invalid canonical name: " + canonicalName);
        }
        return canonicalName.substring(0, index);
    }

    /**
     * Compute the simple attribute name given a canonical attribute name.
     *
     * @param canonicalName the specified canonical name of an attribute
     * @return the simple attribute name
     */
    public static String computeSimpleAttrName(String canonicalName) {
        int index = canonicalName.lastIndexOf('?');
        if (index == -1) {
            throw new IllegalArgumentException("Invalid canonical name: " + canonicalName);
        }
        return canonicalName.substring(index + 1);
    }

}
