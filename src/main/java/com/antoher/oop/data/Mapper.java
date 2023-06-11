package com.antoher.oop.data;

import com.antoher.oop.annotations.ColumnName;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Mapper {
    public static <T> T mapToObject(Map<String, Object> data, Class<T> clazz) throws RuntimeException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        T object = null;
            object = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ColumnName.class)) {
                    ColumnName columnName = field.getAnnotation(ColumnName.class);
                    String fieldName = columnName.name();

                    if (data.containsKey(fieldName)) {
                        Object value = data.get(fieldName);
                        Method setter = findSetter(clazz, field.getName(), field.getType());
                        if (setter != null) {
                            setter.invoke(object, convertValue(value, field.getType()));
                        } else {
                            throw new IllegalArgumentException("No setter found for field: " + field.getName());
                        }
                    }
                    else throw new RuntimeException();
                }
            }
        return object;
    }

    public static Map<String, Object> objectToMap(Object object) throws IllegalAccessException {
        Map<String, Object> data = new HashMap<>();

        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ColumnName.class)) {
                ColumnName columnName = field.getAnnotation(ColumnName.class);
                String fieldName = columnName.name();

                field.setAccessible(true);
                Object value = field.get(object);
                data.put(fieldName, value);
            }
        }

        return data;
    }

    private static Method findSetter(Class<?> clazz, String fieldName, Class<?> fieldType) {
        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(fieldType)) {
                return method;
            }
        }
        return null;
    }
    public static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (targetType == String.class) {
            return value.toString();
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf((String) value);
        }
        if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value.toString());
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value.toString());
        }
        throw new IllegalArgumentException("Невозможно выполнить преобразование в тип " + targetType.getName());
    }
}
