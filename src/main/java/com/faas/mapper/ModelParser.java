package com.faas.mapper;

public abstract class ModelParser<T> {
    public abstract T parseText(String text);
}
