package cn.ljaer.ssm.datasource;

public class DataSourceTypeManager {

    public static final String MASTER = "master";

    public static final String SLAVE = "slave";

    private static final ThreadLocal<String> dataSourceTypes = new ThreadLocal<>();

    public static String get() {
        return dataSourceTypes.get();
    }

    public static void set(String dataSourceType) {
        dataSourceTypes.set(dataSourceType);
    }

    public static void reset() {
        dataSourceTypes.remove();
    }

    public static boolean isChoiceWrite() {
        return (dataSourceTypes.get() != null && dataSourceTypes.get().contains(MASTER));
    }

    public static void markRead(String lookupKey) {
        set(lookupKey);
    }

    public static void markWrite(String lookupKey) {
        set(lookupKey);
    }
}
