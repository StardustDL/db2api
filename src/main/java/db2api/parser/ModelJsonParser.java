package db2api.parser;
import cn.hutool.json.JSONObject;

public abstract class ModelJsonParser<T> extends ModelParser<T> {

    @Override
    public T parseText(String text) {
        return parseJSON(new JSONObject(text));
    }

    public abstract T parseJSON(JSONObject json);
    
}
