package com.faas.service;

import com.faas.model.ApiModel;
import com.faas.model.ConfigModel;
import com.faas.model.SqlStatements;

public abstract class SqlTranslator {
    public abstract SqlStatements translate(ApiModel request, ConfigModel config);
}
