package com.faas.auth;

import com.faas.model.ApiModel;
import com.faas.model.ConfigModel;

import java.util.ArrayList;

public abstract class AccessChecker {
    public abstract ArrayList<String> check(ApiModel request, ConfigModel config);
}
