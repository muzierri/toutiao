package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:触发事件的现场信息都保存在这
 * @Author: liyang
 * @Date: Create in 22:32 2019/7/22
 * @Modified By
 */
public class EventModel {
    private EventType type;//事件类型
    private int actorId;//事件触发者
    private int entityType;
    private int entityId;
    private int entityOwnerId;
    private Map<String, String> exts = new HashMap<>();

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventModel() {

    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "type=" + type +
                ", actorId=" + actorId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityOwnerId=" + entityOwnerId +
                ", exts=" + exts +
                '}';
    }
}
