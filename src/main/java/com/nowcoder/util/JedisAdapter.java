package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ListPosition;
import redis.clients.jedis.Tuple;

import java.util.List;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 22:30 2019/7/16
 * @Modified By
 */
@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

    //测试
    public static void mainx(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.flushAll();
        //数值操作
        jedis.set("a", "b");
        print(1, jedis.get("a"));
        print(2, jedis.keys("*"));
        jedis.rename("a", "c");
        print(3, jedis.keys("*"));
        //设置过期时间
        jedis.setex("hello", 15, "world");
        //浏览量pv
        jedis.set("pv", "100");
        jedis.incr("pv");//加一
        print(4, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(5, jedis.get("pv"));
        //列表操作，最近来访， 粉丝列表，消息队列
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + i);
        }
        print(6, jedis.lrange(listName, 0, 9));
        print(7, jedis.lrange(listName, -10, -1));
        print(8, jedis.lrange(listName, 0, -1));
        print(9, jedis.lrange(listName, -1, 0));//没有从右往左的！
        print(10, jedis.llen(listName));
        print(11, jedis.lpop(listName));
        print(12, jedis.lindex(listName, 0));
        print(13, jedis.linsert(listName, ListPosition.AFTER, "a4", "xx"));
        print(14, jedis.linsert(listName, ListPosition.BEFORE, "a4", "yy"));
        print(15, jedis.lrange(listName, 0, 100));
        //hash，可变字段
        String userKey = "userxx";
        jedis.hset(userKey, "name", "Jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "18656234663");
        print(16, jedis.hget(userKey, "name"));
        print(17, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(18, jedis.hgetAll(userKey));
        print(19, jedis.hkeys(userKey));
        print(20, jedis.hvals(userKey));
        print(21, jedis.hexists(userKey, "name"));
        print(22, jedis.hexists(userKey, "email"));
        jedis.hsetnx(userKey, "school", "zj");//not exit,不存在就设置
        jedis.hsetnx(userKey, "name", "Tom");
        print(23, jedis.hgetAll(userKey));
        //set，点赞用户群，共同好友
        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(i * 2));
        }
//        jedis.srem(likeKeys1,"10");
//        jedis.sadd(likeKeys2,"10");
        print(24, jedis.smembers(likeKeys1));
        print(25, jedis.smembers(likeKeys2));
        print(26, jedis.sinter(likeKeys1, likeKeys2));//求交集
        print(27, jedis.sunion(likeKeys1, likeKeys2));//求并集
        print(28, jedis.sdiff(likeKeys1, likeKeys2));//求不同
        print(29, jedis.sismember(likeKeys1, "5"));
        jedis.srem(likeKeys1, "5");
        print(30, jedis.smembers(likeKeys1));
        print(31, jedis.scard(likeKeys1));//元素个数
        jedis.smove(likeKeys2, likeKeys1, "14");//2移到1
        print(32, jedis.smembers(likeKeys1));
        //zset，排序集合,用于排名
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 80, "Mei");
        jedis.zadd(rankKey, 75, "Lucy");
        print(33, jedis.zcard(rankKey));
        print(34, jedis.zcount(rankKey, 61, 100));
        print(35, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Lucy");
        jedis.zincrby(rankKey, 2, "Luc");//改错卷，会直接新增一个人
        print(36, jedis.zscore(rankKey, "Lucy"));

        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "0", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }
        print(38, jedis.zrange(rankKey, 1, 3));//从小到大排序1-3名（第一个是0名）
        print(39, jedis.zrevrange(rankKey, 1, 3));//从大到小排序1-3名（第一个是0名）
        print(40, jedis.zrank(rankKey, "Ben"));//Ben的名次
        print(41, jedis.zrevrank(rankKey, "Ben"));

        //连接池（默认有8条线程）
        JedisPool jedisPool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j = jedisPool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            j.close();//放回去
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    //redis中存储对象
    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));//JSON的序列化
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);//JSON的反序列化
        } else
            return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("出现异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("出现异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
