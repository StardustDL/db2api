package db2api.parser;

public abstract class ModelParser<T> {
    public abstract T parseText(String text);
}
