package dynamite.exp;

import java.util.HashSet;
import java.util.Set;

import dynamite.core.IInstance;
import dynamite.util.SetMultiMap;

public final class InstanceFilter {

    public static boolean isSatisfactory(String benchmark, IInstance instance) {
        if (benchmark.equals("benchmark22_1")) {
            return checkBenchmark22_1(instance);
        } else if (benchmark.equals("benchmark25_1")) {
            return checkBenchmark25_1(instance);
        } else if (benchmark.equals("benchmark25_2")) {
            return checkBenchmark25_2(instance);
        } else if (benchmark.equals("benchmark26_1")) {
            return checkBenchmark26_1(instance);
        } else if (benchmark.equals("benchmark27_2")) {
            return checkBenchmark27_2(instance);
        } else if (benchmark.equals("benchmark27_4")) {
            return checkBenchmark27_4(instance);
        } else if (benchmark.equals("benchmark28_3")) {
            return checkBenchmark28_3(instance);
        } else {
            return true;
        }
    }

    public static boolean checkBenchmark22_1(IInstance instance) {
        SetMultiMap<String, Object> attrToValuesMap = instance.toDocumentInstance().collectValuesByAttr();
        Set<Object> calendarListingIds = attrToValuesMap.get("Calendar?listing_id");
        Set<Object> listingsIds = attrToValuesMap.get("Listings?id");
        if (calendarListingIds.size() > 2) {
            Set<Object> overlap = new HashSet<>(listingsIds);
            boolean changed = overlap.removeAll(calendarListingIds);
            return changed && !overlap.isEmpty();
        }
        return true;
    }

    public static boolean checkBenchmark25_1(IInstance instance) {
        SetMultiMap<String, Object> attrToValuesMap = instance.toDocumentInstance().collectValuesByAttr();
        Set<Object> pitcherIds = attrToValuesMap.get("AtBats?pitcher_id");
        Set<Object> batterIds = attrToValuesMap.get("AtBats?batter_id");
        Set<Object> playerIds = attrToValuesMap.get("PlayerNames?id");
        if (playerIds.size() > 1) {
            return playerIds.containsAll(pitcherIds) && playerIds.containsAll(batterIds);
        }
        return true;
    }

    public static boolean checkBenchmark25_2(IInstance instance) {
        SetMultiMap<String, Object> attrToValuesMap = instance.toDocumentInstance().collectValuesByAttr();
        Set<Object> ejectionPlayerIds = attrToValuesMap.get("Ejections?player_id");
        Set<Object> playerIds = attrToValuesMap.get("PlayerNames?id");
        if (playerIds.size() > 1) {
            return playerIds.containsAll(ejectionPlayerIds);
        }
        return true;
    }

    public static boolean checkBenchmark26_1(IInstance instance) {
        SetMultiMap<String, Object> attrToValuesMap = instance.toDocumentInstance().collectValuesByAttr();
        Set<Object> reviewListingIds = attrToValuesMap.get("Reviews?listing_id");
        Set<Object> listingsIds = attrToValuesMap.get("Listings?id");
        if (reviewListingIds.size() > 1) {
            return listingsIds.containsAll(reviewListingIds);
        }
        return true;
    }

    public static boolean checkBenchmark27_2(IInstance instance) {
        SetMultiMap<String, Object> attrToValuesMap = instance.toDocumentInstance().collectValuesByAttr();
        Set<Object> documentCaseIds = attrToValuesMap.get("Documents?case_row_id");
        Set<Object> caseIds = attrToValuesMap.get("Cases?case_row_id");
        if (caseIds.size() > 1) {
            return isProperSubset(documentCaseIds, caseIds);
        }
        return true;
    }

    public static boolean checkBenchmark27_4(IInstance instance) {
        SetMultiMap<String, Object> attrToValuesMap = instance.toDocumentInstance().collectValuesByAttr();
        Set<Object> nameCaseIds = attrToValuesMap.get("Names?case_row_id");
        Set<Object> caseIds = attrToValuesMap.get("Cases?case_row_id");
        if (caseIds.size() > 1) {
            return isProperSubset(nameCaseIds, caseIds);
        }
        return true;
    }

    public static boolean checkBenchmark28_3(IInstance instance) {
        SetMultiMap<String, Object> attrToValuesMap = instance.toDocumentInstance().collectValuesByAttr();
        Set<Object> startIds = attrToValuesMap.get("Trips?start_station_id");
        Set<Object> endIds = attrToValuesMap.get("Trips?end_station_id");
        Set<Object> stationIds = attrToValuesMap.get("Stations?id");
        if (stationIds.size() > 1) {
            return isProperSubset(startIds, stationIds) && isProperSubset(endIds, stationIds) && notSubset(startIds, endIds);
        }
        return true;
    }

    private static <T> boolean isProperSubset(Set<T> lhs, Set<T> rhs) {
        return rhs.containsAll(lhs) && !rhs.equals(lhs);
    }

    private static <T> boolean notSubset(Set<T> lhs, Set<T> rhs) {
        return !lhs.containsAll(rhs) && !rhs.containsAll(lhs);
    }

}
