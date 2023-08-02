package db2api.translator;

import db2api.model.ApiModel;
import db2api.model.ConfigModel;
import db2api.model.SqlStatements;

public abstract class SqlTranslator {
    public abstract SqlStatements translate(ApiModel request, ConfigModel config);
}
