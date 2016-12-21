/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.api.wrappers.impl;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts objects to specific types.
 */
public final class ObjectConverter {
    
    private ObjectConverter() {
        // static methods only
    }

    /**
     * Converts the object to the given type.
     * @param obj object
     * @param type type
     * @return the converted object
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object obj, Class<T> type) {
        if (obj == null) {
            return null;
        }
        else if (type.isArray()) {
            return (T) convertToArray(obj, type.getComponentType());
        }
        if (type.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        }
        else {
            String result = getSingleValue(obj);
            if (result == null) {
                return null;
            }
            if (type == String.class) {
                return (T) result.toString();
            }
            if (type == Boolean.class) {
                return (T) (Boolean)Boolean.parseBoolean(result);
            }
            try {
                if (type == Byte.class) {
                    return (T) (Byte)Byte.parseByte(result);
                }
                if (type == Short.class) {
                    return (T) (Short)Short.parseShort(result);
                }
                if (type == Integer.class) {
                    return (T) (Integer)Integer.parseInt(result);
                }
                if (type == Long.class) {
                    return (T) (Long)Long.parseLong(result);
                }
                if (type == Float.class) {
                    return (T) (Float)Float.parseFloat(result);
                }
                if (type == Double.class) {
                    return (T) (Double)Double.parseDouble(result);
                }
                if (type == BigDecimal.class) {
                    return (T) new BigDecimal(result);
                }
            }
            catch (NumberFormatException e) {
                return null;
            }
            return null;
        }
    }

    /**
     * Gets a single value of String from the object. If the object is an array it returns it's first element.
     * @param obj object or object array.
     * @return result of <code>toString()</code> on object or first element of an object array. If @param obj is null
     * or it's an array with first element that is null, then null is returned.
     */
    private static String getSingleValue(Object obj) {
        final String result;
        if (obj == null) {
            result = null;
        }
        else if (obj.getClass().isArray()) {
            if (Array.getLength(obj) == 0) {
                result = null;
            }
            else {
                result = getSingleValue(Array.get(obj, 0));
            }
        }
        else {
            result = obj.toString();
        }
        return result;
    }

    /**
     * Converts the object to an array of the given type
     * @param obj the object or object array
     * @param type the component type of the array
     * @return and array of type T
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] convertToArray(Object obj, Class<T> type) {
        if (obj.getClass().isArray()) {
            List<Object> resultList = new ArrayList<Object>();
            for (int i = 0; i < Array.getLength(obj); i++) {
                T singleValueResult = convert(Array.get(obj, i), type);
                if (singleValueResult != null) {
                    resultList.add(singleValueResult);
                }
            }
            if (resultList.isEmpty()) {
                return null;
            }
            return resultList.toArray((T[]) Array.newInstance(type, resultList.size()));
        }
        else {
            final T singleValueResult = convert(obj, type);
            // return null for type conversion errors instead of single element array with value null
            if (singleValueResult == null) {
                return null;
            }
            final T[] arrayResult = (T[]) Array.newInstance(type, 1);
            arrayResult[0] = singleValueResult;
            return arrayResult;
        }
    }

}
