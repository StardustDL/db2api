package db2api.checker;

import db2api.model.ApiModel;
import db2api.model.ConfigModel;

import java.util.ArrayList;

public abstract class AccessChecker {
    public abstract ArrayList<String> check(ApiModel request, ConfigModel config);
}
