package kr.fatos.tnavi.Code;

public class SettingsCode {
    //sharedpreferences 에 key로 사용되기 때문에 final
    private final static String KEY_LANGUAGE = "LANGUAGE";
    private final static String KEY_COUNTRY = "COUNTRY";
    private final static String KEY_INDEX = "INDEX";
    private static String KEY_CATEGORY = "CATEGORY";
    private static String KEY_CATEGORYINDEX = "CATEGORY_INDEX";

    private static String VALUE_LANGUAGE = "English";
    private static String VALUE_COUNTRY = "Thailand";
    private static int VALUE_INDEX = 0; //for country

    private static int VALUE_CATEGORY_INDEX = 0; //for category
    private static String VALUE_CATEGORY = "All Category";

    private static int KEY_RESULT_COUNT = 20; // 노스트라 검색 결과 표출 개수
    private static int KEY_RECENT_POI_COUNT = 20; //최근 목적지 저장 개수

    private static int DISTANCE_UNIT = 0; //0 : km, 1 : mile

    public static String getKeyLanguage() {
        return KEY_LANGUAGE;
    }

    public static int getKeyResultCount() {
        return KEY_RESULT_COUNT;
    }

    public static void setKeyResultCount(int keyResultCount) {
        KEY_RESULT_COUNT = keyResultCount;
    }

    public static String getKeyCountry() {
        return KEY_COUNTRY;
    }
    public static String getValueLanguage() {
        return VALUE_LANGUAGE;
    }

    public static void setValueLanguage(String valueLanguage) {
        VALUE_LANGUAGE = valueLanguage;
    }

    public static String getValueCountry() {
        return VALUE_COUNTRY;
    }

    public static void setValueCountry(String valueCountry) {
        VALUE_COUNTRY = valueCountry;
    }
    public static int getValueIndex() {
        return VALUE_INDEX;
    }

    public static void setValueIndex(int valueIndex) {
        VALUE_INDEX = valueIndex;
    }
    public static String getKeyIndex() {
        return KEY_INDEX;
    }


    public static String getValueCategory() {
        return VALUE_CATEGORY;
    }

    public static void setValueCategory(String valueCategory) {
        VALUE_CATEGORY = valueCategory;
    }

    public static int getValueCategoryIndex() {
        return VALUE_CATEGORY_INDEX;
    }

    public static void setValueCategoryIndex(int valueCategoryIndex) {
        VALUE_CATEGORY_INDEX = valueCategoryIndex;
    }

    public static void setKeyRecentPoiCount(int keyRecentPoiCount) {
        KEY_RECENT_POI_COUNT = keyRecentPoiCount;
    }
    public static String getKeyCategory() {
        return KEY_CATEGORY;
    }

    public static void setKeyCategory(String keyCategory) {
        KEY_CATEGORY = keyCategory;
    }


    public static String getKeyCategoryindex() {
        return KEY_CATEGORYINDEX;
    }

    public static void setKeyCategoryindex(String keyCategoryindex) {
        KEY_CATEGORYINDEX = keyCategoryindex;
    }

    public static int getKeyRecentPoiCount() {
        return KEY_RECENT_POI_COUNT;
    }

    public static int getDistanceUnit() {
        return DISTANCE_UNIT;
    }

    public static void setDistanceUnit(int distanceUnit) {
        DISTANCE_UNIT = distanceUnit;
    }
}
