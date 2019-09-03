package com.nowcoder.util;

/**
 * @Description:用于获取一个规范的key
 * @Author: liyang
 * @Date: Create in 22:00 2019/7/19
 * @Modified By
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";//分隔符
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENT = "EVENT";

    public static String getEventQueueKey(){
        return BIZ_EVENT;
    }

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
