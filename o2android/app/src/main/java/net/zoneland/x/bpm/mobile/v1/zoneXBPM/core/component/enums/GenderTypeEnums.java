package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

/**
 * Created by FancyLou on 2015/10/20.
 */
public enum GenderTypeEnums {

    MALE("m", "男"),
    FEMALE("f", "女"),
    DEFAULT("d", "未知");

    private final String key;
    private final String name;

    GenderTypeEnums(String key, String name){
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static String getNameByKey(String key) {
        for(GenderTypeEnums en: GenderTypeEnums.values()){
            if(en.getKey().equals(key)){
                return en.getName();
            }
        }
        return DEFAULT.getName();
    }

    public static String getKeyByName(String name){
        for(GenderTypeEnums en: GenderTypeEnums.values()){
            if(en.getName().equals(name)){
                return en.getKey();
            }
        }
        return MALE.getKey();
    }
}
