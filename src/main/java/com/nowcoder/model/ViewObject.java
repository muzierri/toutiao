package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 21:35 2019/6/26
 * @Modified By
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();

    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
