package org.NoiQing.util;

import org.NoiQing.api.QinSkill;

import java.util.HashMap;
import java.util.Map;

public class QinSkillsDataSava {
    private static final Map<String, QinSkill> skillsStorage = new HashMap<>();
    public static Map<String, QinSkill> getSkillsCache() { return skillsStorage; }
    public static void clearQinSkillsDataSave() {
        skillsStorage.clear();
    }
}
