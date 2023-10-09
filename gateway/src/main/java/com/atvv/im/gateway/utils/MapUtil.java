package com.atvv.im.gateway.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MapUtil {

	/**
	 * 将对象装换为map
	 * @param obj obj
	 * @return Map
	 */
	public static <T> Map<String, Object> beanToMap(T obj) {
		Map<String, Object> map = new HashMap<>();
		Field[] declaredFields = obj.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(obj));
			} catch (IllegalAccessException e) {
				log.error("MapUtil解析 {} 类： {} 属性出错", obj.getClass(), field.getName());
				map.put(field.getName(), null);
			}
		}
		return map;
	}
}