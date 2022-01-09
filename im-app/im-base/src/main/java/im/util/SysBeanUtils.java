package im.util;

import im.model.base.BaseModel;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.collections.CollectionUtils;
import java.util.*;


/**
 * @author hoomoomoo
 * @description bean map 转换工具类
 * @package im.util
 * @date 2019/08/08
 */

public class SysBeanUtils {

    /**
     * map转换成bean
     *
     * @param clazz
     * @param map
     */
    public static BaseModel mapToBean(Class clazz, Map map) {
        BaseModel baseModel = null;
        try {
            baseModel = (BaseModel) clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        BeanMap beanMap = BeanMap.create(baseModel);
        beanMap.putAll(map);
        return baseModel;
    }

    /**
     * List转换成bean
     *
     * @param clazz
     * @param list
     */
    public static List<BaseModel> mapToBean(Class clazz, List<Map> list) {
        List<BaseModel> baseModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Map single : list) {
                baseModelList.add(mapToBean(clazz, single));
            }
        }
        return baseModelList;
    }

    /**
     * bean转换成map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> target = new HashMap(16);
            BeanMap beanMap = BeanMap.create(obj);
            for (Object key : beanMap.keySet()) {
                Object value = beanMap.get(key);
                if (value != null) {
                    target.put(String.valueOf(key), value);
                }
            }
        return target;
    }

    /**
     * bean转换成map
     *
     * @param list
     * @return
     */
    public static List<Map<String, Object>> beanToMap(List<Object> list){
        List<Map<String, Object>> map = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            for (Object obj: list) {
                map.add(beanToMap(obj));
            }
        }
        return map;
    }

    /**
     * map转换成linkedHashMap
     *
     * @param map
     * @return
     */
    public static LinkedHashMap<String, Object> mapToLinkedHashMap(Map map) {
        LinkedHashMap linkedHashMap = new LinkedHashMap(16);
        if (map != null) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = map.get(key);
                linkedHashMap.put(key, value);
            }
        }
        return linkedHashMap;
    }

    /**
     * linkedHashMap转换成map
     *
     * @param linkedHashMap
     * @return
     */
    public static Map<String, Object> linkedHashMapToMap(LinkedHashMap linkedHashMap) {
        Map map = new HashMap(16);
        if (linkedHashMap != null) {
            Iterator<String> iterator = linkedHashMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                map.put(key, linkedHashMap.get(key));
            }
        }
        return map;
    }
}
