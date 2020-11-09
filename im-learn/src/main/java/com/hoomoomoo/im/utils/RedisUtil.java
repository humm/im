package com.hoomoomoo.im.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @author: humm23693
 * @date: 2020/11/07
 * @return:
 */
@Service
public class RedisUtil {

    static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String namespace = "com.hoomoomoo.im";

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String namespace, String key, long time) {
        if (time > 0) {
            redisTemplate.expire(namespace + key, time, TimeUnit.SECONDS);
        }
        return true;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String namespace, String key) {
        return redisTemplate.getExpire(namespace + key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String namespace, String key) {
        return redisTemplate.hasKey(namespace + key);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String namespace, String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(namespace + key[0]);
            } else {
                List<String> keys = new ArrayList<>();
                CollectionUtils.arrayToList(key).forEach((id) -> {
                    keys.add(namespace + id);
                });
                redisTemplate.delete(keys);
            }
        }
    }

    public void deleteByNamespace(String namespace) {
        Set<String> keys = redisTemplate.keys(namespace + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String namespace, String key) {
        return key == null ? null : redisTemplate.opsForValue().get(namespace + key);
    }

    public Object getAndResetExpire(String namespace, String key, Long expire) {
        Object value = redisTemplate.opsForValue().get(namespace + key);
        if (value == null) {
            return null;
        } else {
            long oldExpire = this.getExpire(namespace, key);
            if (logger.isDebugEnabled()) {
                logger.debug(key + "的超时时间从" + oldExpire + "重置为" + expire);
            }
            if (expire > 0) {
                this.expire(namespace, key, expire);
            }
        }
        return value;
    }

    /**
     * 一次获取多个key, 推荐集合使用
     *
     * @param namespace
     * @param keys
     * @return
     */
    public List<Object> mget(String namespace, List<String> keys) {
        for (String key : keys) {
            key = namespace + key;
        }
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String namespace, String key, Object value) {
        redisTemplate.opsForValue().set(namespace + key, value);
        return true;
    }

    public boolean mset(String namespace, Map<? extends String, ? extends Object> entries) {
        Map<String, Object> tmpEntries = new HashMap<>(16);
        for (Entry<? extends String, ? extends Object> entry : entries.entrySet()) {
            tmpEntries.put(namespace + entry.getKey(), entry.getValue());
        }
        redisTemplate.opsForValue().multiSet(tmpEntries);
        return true;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String namespace, String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(namespace + key, value, time, TimeUnit.SECONDS);
        } else {
            set(namespace, key, value);
        }
        return true;
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String namespace, String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(namespace + key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String namespace, String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(namespace + key, -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String namespace, String key, String item) {
        return redisTemplate.opsForHash().get(namespace + key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String namespace, String key) {
        return redisTemplate.opsForHash().entries(namespace + key);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public List<String> hmget(String namespace, String key, List hashKeys) {
        return redisTemplate.opsForHash().multiGet(namespace + key, hashKeys);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String namespace, String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(namespace + key, map);
        return true;
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String namespace, String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(namespace + key, map);
        if (time > 0) {
            expire(namespace, key, time);
        }
        return true;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String namespace, String key, String item, Object value) {
        redisTemplate.opsForHash().put(namespace + key, item, value);
        return true;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String namespace, String key, String item, Object value, long time) {
        redisTemplate.opsForHash().put(namespace + key, item, value);
        if (time > 0) {
            expire(namespace, key, time);
        }
        return true;
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String namespace, String key, Object... item) {
        redisTemplate.opsForHash().delete(namespace + key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String namespace, String key, String item) {
        return redisTemplate.opsForHash().hasKey(namespace + key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String namespace, String key, String item, double by) {
        return redisTemplate.opsForHash().increment(namespace + key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String namespace, String key, String item, double by) {
        return redisTemplate.opsForHash().increment(namespace + key, item, -by);
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String namespace, String key) {
        return redisTemplate.opsForSet().members(namespace + key);
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String namespace, String key, Object value) {
        return redisTemplate.opsForSet().isMember(namespace + key, value);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String namespace, String key, Object... values) {
        return redisTemplate.opsForSet().add(namespace + key, values);
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String namespace, String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(namespace + key, values);
        if (time > 0) {
            expire(namespace, key, time);
        }
        return count;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String namespace, String key) {
        return redisTemplate.opsForSet().size(namespace + key);
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String namespace, String key, Object... values) {
        Long count = redisTemplate.opsForSet().remove(namespace + key, values);
        return count;
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String namespace, String key, long start, long end) {
        return redisTemplate.opsForList().range(namespace + key, start, end);
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String namespace, String key) {
        return redisTemplate.opsForList().size(namespace + key);
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String namespace, String key, long index) {
        return redisTemplate.opsForList().index(namespace + key, index);
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String namespace, String key, Object value) {
        redisTemplate.opsForList().rightPush(namespace + key, value);
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String namespace, String key, Object value, long time) {
        redisTemplate.opsForList().rightPush(namespace + key, value);
        if (time > 0) {
            expire(namespace, key, time);
        }
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String namespace, String key, List<Object> value) {
        redisTemplate.opsForList().rightPushAll(namespace + key, value);
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String namespace, String key, List<Object> value, long time) {
        redisTemplate.opsForList().rightPushAll(namespace + key, value);
        if (time > 0) {
            expire(namespace, key, time);
        }
        return true;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String namespace, String key, long index, Object value) {
        redisTemplate.opsForList().set(namespace + key, index, value);
        return true;
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String namespace, String key, long count, Object value) {
        Long remove = redisTemplate.opsForList().remove(namespace + key, count, value);
        return remove;
    }
}