package com.github.malitsplus.shizurunotes.common;

public class Statics {
    //  API URL
    public static final String API_URL_ABROAD = "https://redive.estertion.win";
    public static final String API_URL = "https://raw.githubusercontent.com/HerDataSam/herdatasam.github.io/master";

    //  database string for use
    public static String DB_FILE_NAME_COMPRESSED = "redive_kr.db.br";
    public static String DB_FILE_NAME = "redive_kr.db";
    public static String LATEST_VERSION_URL = API_URL + "/last_version_kr.json";
    public static String DB_FILE_URL = API_URL + "/db/" + DB_FILE_NAME_COMPRESSED;
    public static String CONTENTS_MAX_URL = API_URL + "/contents_max_kr.json";
    public static String PREFAB_FILE_NAME = "prefabs.zip";
    public static String PREFAB_FILE_URL = API_URL + "/db/" + PREFAB_FILE_NAME;

    //  JP database
    public static final String DB_FILE_NAME_COMPRESSED_JP = "redive_jp.db.br";
    public static final String DB_FILE_NAME_JP = "redive_jp.db";
    public static final String LATEST_VERSION_URL_JP = API_URL_ABROAD + "/last_version_jp.json";
    public static final String DB_FILE_URL_JP = API_URL_ABROAD + "/db/" + DB_FILE_NAME_COMPRESSED_JP;

    //  CN database
    public static final String DB_FILE_NAME_COMPRESSED_CN = "redive_cn.db.br";
    public static final String DB_FILE_NAME_CN = "redive_cn.db";
    public static final String LATEST_VERSION_URL_CN = API_URL_ABROAD + "/last_version_cn.json";
    public static final String DB_FILE_URL_CN = API_URL_ABROAD + "/db/" + DB_FILE_NAME_COMPRESSED_CN;

    //  KR database
    public static final String DB_FILE_NAME_COMPRESSED_KR = "redive_kr.db.br";
    public static final String DB_FILE_NAME_KR = "redive_kr.db";
    public static final String LATEST_VERSION_URL_KR = API_URL + "/last_version_kr.json";
    public static final String DB_FILE_URL_KR = API_URL + "/db/" + DB_FILE_NAME_COMPRESSED_KR;
    public static final String CONTENTS_MAX_URL_KR = API_URL + "/contents_max_kr.json";

    //  Resource URL
    public static final String ICON_URL = API_URL + "/icon/unit/%d.webp";
    public static final String SHADOW_ICON_URL = API_URL + "/icon/unit_shadow/%d.webp";
    public static final String SKILL_ICON_URL = API_URL + "/icon/skill/%d.webp";
    public static final String IMAGE_URL = API_URL + "/card/full/%d.webp";
    public static final String EQUIPMENT_ICON_URL = API_URL + "/icon/equipment/%d.webp";
    public static final String ITEM_ICON_URL = API_URL + "/icon/item/%d.webp";
    public static final String EVENT_BANNER_URL = API_URL + "/event/%d.webp";
    public static final String SRT_PANEL_URL = API_URL + "/icon/srt/%d.webp";
    public static final String UNKNOWN_ICON = API_URL + "/icon/equipment/999999.webp";

    //  App URL
    public static final String APP_RAW = "https://raw.githubusercontent.com/HerDataSam/KasumiNotes/master";
    public static final String APP_UPDATE_LOG = APP_RAW + "/update_log.json";
    public static final String APP_PACKAGE = "https://github.com/HerDataSam/KasumiNotes/releases/latest/download/kasuminotes-release.apk";
    public static final String APK_NAME = "kasuminotes-release.apk";
}
