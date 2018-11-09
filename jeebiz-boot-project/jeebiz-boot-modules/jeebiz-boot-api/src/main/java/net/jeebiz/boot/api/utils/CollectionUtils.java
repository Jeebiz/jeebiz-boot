/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Predicate;

public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {
	
	public static <T> List<T> findAll(Collection<T> inputCollection, Predicate predicate) {
        if (inputCollection != null && predicate != null) {
        	List<T> outputCollection = new ArrayList<T>();
        	CollectionUtils.select(inputCollection, predicate, outputCollection);
            return outputCollection;
        }
        return null;
    }
	
}
