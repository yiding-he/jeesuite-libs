package com.jeesuite.common.util;

import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean复制<br>
 * Copyright (c) 2015, vakinge@gmail.com.
 */
public class BeanUtils {

    private static final String CLASS_PROP_NAME = "class";

    private static Map<String, Map<String, PropertyDescriptor>> cache = new ConcurrentHashMap<>();

    private static Map<String, List<String>> fieldCache = new HashMap<>();

    /**
     * 值复制
     *
     * @param setDefaultValForNull 是否为null值属性设置默认值（null=>0,null=>""）
     */
    public static <T> T copy(Object src, T dest, boolean setDefaultValForNull) throws BeanConverterException {
        if (src == null)
            return null;

        try {
            Class<? extends Object> destClass = dest.getClass();
            Map<String, PropertyDescriptor> srcDescriptors = getCachePropertyDescriptors(src.getClass());
            Map<String, PropertyDescriptor> destDescriptors = getCachePropertyDescriptors(destClass);

            Set<String> keys = destDescriptors.keySet();
            for (String key : keys) {
                PropertyDescriptor srcDescriptor = srcDescriptors.get(key);

                if (srcDescriptor == null)
                    continue;

                PropertyDescriptor destDescriptor = destDescriptors.get(key);

                Object value = srcDescriptor.getReadMethod().invoke(src);

                Class<?> propertyType = destDescriptor.getPropertyType();

                Method writeMethod = destDescriptor.getWriteMethod();
                if (writeMethod == null) {
                    String name = destDescriptor.getName();
                    try {
                        writeMethod = destClass.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), destDescriptor.getPropertyType());

                        destDescriptor.setWriteMethod(writeMethod);
                    } catch (Exception e) {
                    }
                }
                if (writeMethod != null) {
                    //类型匹配
                    boolean matched = propertyType == srcDescriptor.getPropertyType();
                    if (!matched) {

                        if (value != null || setDefaultValForNull) {
                            if (isSimpleDataType(srcDescriptor.getPropertyType())) {
                                value = toValue(srcDescriptor.getPropertyType(), value, propertyType);
                            } else {
                                value = copy(value, propertyType);
                            }
                        }
                    }
                    //设置默认值
                    if (value == null && setDefaultValForNull) {
                        if (destDescriptor.getPropertyType() == Long.class || destDescriptor.getPropertyType() == Integer.class || destDescriptor.getPropertyType() == Short.class || destDescriptor.getPropertyType() == Double.class || destDescriptor.getPropertyType() == Float.class) {
                            value = 0;
                        } else if (destDescriptor.getPropertyType() == String.class) {
                            value = StringUtils.EMPTY;
                        } else if (destDescriptor.getPropertyType() == BigDecimal.class) {
                            value = BigDecimal.ZERO;
                        }
                    }

                    if (value != null) {
                        writeMethod.invoke(dest, value);
                    }
                }
            }

            return dest;
        } catch (Exception e) {
            throw new BeanConverterException(e);
        }
    }

    public static <T> T copy(Object src, T dest) throws BeanConverterException {
        return copy(src, dest, false);
    }

    public static <T> List<T> copy(List<?> srcs, Class<T> destClass, boolean setDefaultValForNull) {
        if (srcs == null)
            return new ArrayList<T>();

        List<T> dests = new ArrayList<T>();
        for (Object src : srcs) {
            dests.add(copy(src, destClass, setDefaultValForNull));
        }

        return dests;
    }

    public static <T> List<T> copy(List<?> srcs, Class<T> destClass) {
        return copy(srcs, destClass, false);
    }

    public static <T> T copy(Object src, Class<T> destClass, boolean setDefaultValForNull) throws BeanConverterException {
        if (src == null)
            return null;

        try {
            T dest = destClass.newInstance();
            copy(src, dest, setDefaultValForNull);
            return dest;
        } catch (Exception e) {
            throw new BeanConverterException(e);
        }
    }

    public static <T> T copy(Object src, Class<T> destClass) throws BeanConverterException {
        return copy(src, destClass, false);
    }

    /**
     * 把对象值为0的包装类型属性转为null
     *
     * @param excludeFields 排除不处理的字段
     */
    public static void zeroWrapPropertiesToNull(Object bean, String... excludeFields) throws BeanConverterException {
        try {
            Map<String, PropertyDescriptor> srcDescriptors = getCachePropertyDescriptors(bean.getClass());
            Set<String> keys = srcDescriptors.keySet();

            List<String> excludeFieldsList = null;
            if (excludeFields != null && excludeFields.length > 0 && StringUtils.isNotBlank(excludeFields[0])) {
                excludeFieldsList = Arrays.asList(excludeFields);
            }

            for (String key : keys) {
                PropertyDescriptor srcDescriptor = srcDescriptors.get(key);
                if (srcDescriptor == null) continue;
                if (excludeFieldsList != null && excludeFieldsList.contains(key)) continue;
                Object value = srcDescriptor.getReadMethod().invoke(bean);

                boolean isWrapType = srcDescriptor.getPropertyType() == Long.class || srcDescriptor.getPropertyType() == Integer.class || srcDescriptor.getPropertyType() == Short.class || srcDescriptor.getPropertyType() == Double.class || srcDescriptor.getPropertyType() == Float.class;
                if (isWrapType && value != null && Integer.parseInt(value.toString()) == 0) {
                    value = null;
                    Method writeMethod = srcDescriptor.getWriteMethod();
                    if (writeMethod != null) writeMethod.invoke(bean, value);
                }

            }
        } catch (Exception e) {
            throw new BeanConverterException(e);
        }
    }


    private static Object toValue(Class<?> srcpropertyType, Object value, Class<?> propertyType) {

        if (propertyType == BigDecimal.class) {
            value = (value == null) ? BigDecimal.ZERO : new BigDecimal(value.toString());
        } else if (propertyType == byte.class || propertyType == Byte.class) {
            value = (value == null) ? Byte.valueOf("0") : Byte.valueOf(value.toString());
        } else if (propertyType == short.class || propertyType == Short.class) {
            value = (value == null) ? Short.valueOf("0") : Short.valueOf(value.toString());
        } else if (propertyType == int.class || propertyType == Integer.class) {
            if (srcpropertyType == boolean.class || srcpropertyType == Boolean.class) {
                value = Boolean.parseBoolean(value.toString()) ? 1 : 0;
            } else {
                value = (value == null) ? Integer.valueOf("0") : Integer.valueOf(value.toString());
            }
        } else if (propertyType == double.class || propertyType == Double.class) {
            value = (value == null) ? Double.valueOf("0") : Double.valueOf(value.toString());
        } else if (propertyType == Date.class) {
            if (value != null) {
                if (srcpropertyType == String.class) {
                    value = DateUtils.parseDate(value.toString());
                } else if (srcpropertyType == Long.class || srcpropertyType == Integer.class || srcpropertyType == long.class || srcpropertyType == int.class) {
                    Long val = Long.valueOf(value.toString());
                    if (val.longValue() != 0)
                        value = new Date(val);
                    else
                        value = null;
                }
            }

        } else if (propertyType == String.class && srcpropertyType != String.class) {
            if (value != null) {
                if (srcpropertyType == Date.class) {
                    value = DateUtils.format((Date) value);
                } else {
                    value = value.toString();
                }
            }
        } else if (propertyType == boolean.class || propertyType == Boolean.class) {
            if (value.toString().matches("[0|1]")) {
                value = "1".equals(value.toString());
            }
        }
        return value;
    }

    private static Object stringConvertTo(String value, Class<?> propertyType) {
        Object result = value;
        if (propertyType == BigDecimal.class) {
            result = new BigDecimal(value);
        } else if (propertyType == byte.class || propertyType == Byte.class) {
            result = Byte.valueOf(value);
        } else if (propertyType == short.class || propertyType == Short.class) {
            result = Short.valueOf(value.toString());
        } else if (propertyType == int.class || propertyType == Integer.class) {
            result = Integer.parseInt(value);
        } else if (propertyType == double.class || propertyType == Double.class) {
            result = Double.valueOf(value.toString());
        } else if (propertyType == long.class || propertyType == Long.class) {
            result = Long.valueOf(value.toString());
        } else if (propertyType == Date.class) {
            if (value != null) {
                result = DateUtils.parseDate(value);
            }
        } else if (propertyType == LocalDate.class) {
            if (value != null) {
                result = LocalDate.parse(value);
            }
        } else if (propertyType == boolean.class || propertyType == Boolean.class) {
            result = Boolean.parseBoolean(value) || "1".equals(value);
        }
        return result;
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        try {
            T bean = clazz.newInstance();
            Map<String, PropertyDescriptor> descriptors = getCachePropertyDescriptors(clazz);
            for (PropertyDescriptor descriptor : descriptors.values()) {
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    Object object = map.get(propertyName);
                    if (object == null) continue;
                    if (descriptor.getPropertyType() != object.getClass()) {
                        object = stringConvertTo(object.toString(), descriptor.getPropertyType());
                    }
                    descriptor.getWriteMethod().invoke(bean, object);
                }
            }
            return bean;
        } catch (Exception e) {
            throw new BeanConverterException(e);
        }
    }

    public static Map<String, Object> beanToMap(Object bean) {
        return beanToMap(bean, false);
    }

    public static Map<String, Object> beanToMap(Object bean, boolean recursive) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        try {
            Map<String, PropertyDescriptor> descriptors = getCachePropertyDescriptors(bean.getClass());
            for (PropertyDescriptor descriptor : descriptors.values()) {
                String propertyName = descriptor.getName();
                if (CLASS_PROP_NAME.equalsIgnoreCase(propertyName)) continue;
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    if (isSimpleDataType(result) || result instanceof Iterable) {
                        returnMap.put(propertyName, result);
                    } else {
                        if (recursive) {
                            returnMap.put(propertyName, beanToMap(result, recursive));
                        } else {
                            returnMap.put(propertyName, result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BeanConverterException(e);
        }


        return returnMap;

    }

    public static void copy(Map<String, Object> src, Object dist) {
        try {
            Map<String, PropertyDescriptor> descriptors = getCachePropertyDescriptors(dist.getClass());
            for (PropertyDescriptor descriptor : descriptors.values()) {
                String propertyName = descriptor.getName();
                if (CLASS_PROP_NAME.equalsIgnoreCase(propertyName)) continue;
                if (!src.containsKey(propertyName)) continue;
                Object value = src.get(propertyName);
                value = toValue(value.getClass(), value, descriptor.getPropertyType());
                descriptor.getWriteMethod().invoke(dist, value);
            }
        } catch (Exception e) {
            throw new BeanConverterException(e);
        }
    }

    private static Map<String, PropertyDescriptor> getCachePropertyDescriptors(Class<?> clazz) throws IntrospectionException {
        String canonicalName = clazz.getCanonicalName();
        Map<String, PropertyDescriptor> map = cache.get(canonicalName);

        if (map == null) {
            map = doCacheClass(clazz, canonicalName);
        }

        return map;
    }

    /**
     *
     */
    private synchronized static Map<String, PropertyDescriptor> doCacheClass(Class<?> clazz, String canonicalName)
            throws IntrospectionException {
        if (cache.containsKey(canonicalName)) return cache.get(canonicalName);

        Map<String, PropertyDescriptor> map = new ConcurrentHashMap<>();

        List<String> fieldNames = new ArrayList<>();

        BeanInfo srcBeanInfo = Introspector.getBeanInfo(clazz);

        PropertyDescriptor[] descriptors = srcBeanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {

            fieldNames.add(descriptor.getName());

            Method readMethod = descriptor.getReadMethod();
            Method writeMethod = descriptor.getWriteMethod();

            String name = descriptor.getName();

            if (readMethod == null)
                try {
                    readMethod = clazz.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));

                    descriptor.setReadMethod(readMethod);
                } catch (NoSuchMethodException | SecurityException e) {
                }

            if (writeMethod == null)
                try {
                    writeMethod = clazz.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), descriptor.getPropertyType());

                    descriptor.setWriteMethod(writeMethod);
                } catch (NoSuchMethodException | SecurityException e) {
                }

            if (readMethod != null && writeMethod != null) {
                map.put(descriptor.getName(), descriptor);
            }
        }

        cache.put(canonicalName, map);
        fieldCache.put(canonicalName, fieldNames);
        return map;
    }


    /**
     * 判断是否基本类型
     */
    public static boolean isSimpleDataType(Object o) {
        return isSimpleDataType(o.getClass());
    }

    public static boolean isSimpleDataType(Class<?> clazz) {
        return
                (
                        clazz.isPrimitive() ||
                                clazz.equals(String.class) ||
                                clazz.equals(Integer.class) ||
                                clazz.equals(Byte.class) ||
                                clazz.equals(Long.class) ||
                                clazz.equals(Double.class) ||
                                clazz.equals(Float.class) ||
                                clazz.equals(Character.class) ||
                                clazz.equals(Short.class) ||
                                clazz.equals(BigDecimal.class) ||
                                clazz.equals(Boolean.class) ||
                                clazz.equals(Date.class)
                );
    }


    public static class BeanConverterException extends RuntimeException {

        private static final long serialVersionUID = 152873897614690397L;

        public BeanConverterException(Throwable cause) {
            super(cause);
        }
    }


}
