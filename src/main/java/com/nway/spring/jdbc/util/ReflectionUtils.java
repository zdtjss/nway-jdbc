package com.nway.spring.jdbc.util;

import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class ReflectionUtils {

    private static final Map<Class<?>, Field[]> DECLARED_FIELD_CACHE = new ConcurrentReferenceHashMap<>(256);

    public static Field[] getAllFields(Class<?> clazz) {

        Field[] fieldArr = DECLARED_FIELD_CACHE.get(clazz);
        if (fieldArr != null) {
            return fieldArr;
        }

        List<Field> fieldList = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = getDeclaredFields(searchType);
            fieldList.addAll(Arrays.asList(fields));
            searchType = searchType.getSuperclass();
        }
        fieldArr = fieldList.toArray(new Field[0]);
        DECLARED_FIELD_CACHE.put(clazz, fieldArr);

        return fieldArr;
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        try {
            return clazz.getDeclaredFields();
        } catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                    "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
        }
    }
}
