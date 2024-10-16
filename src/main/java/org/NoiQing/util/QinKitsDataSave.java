package org.NoiQing.util;

import org.NoiQing.api.QinKit;

import java.util.HashMap;
import java.util.Map;

public class QinKitsDataSave {
    private static final Map<String, QinKit> kitsStorage = new HashMap<>();
    public static Map<String, QinKit> getKitCache() { return kitsStorage; }
    public static void clearQinKitsDataSave() {
        kitsStorage.clear();
    }
}
