/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.provider;

public interface KeyValueProvider<T> {

    public T get(String key);

}
