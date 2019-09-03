package com.nowcoder.async;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 22:24 2019/7/22
 * @Modified By
 */
public enum EventType {
    //枚举类的所有实例都必须放在第一行展示，不需使用new关键字，不需显式调用构造器。自动添加public static final修饰
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);
    //属性
    private int value;
    //私有化构造器
    EventType(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }

}
