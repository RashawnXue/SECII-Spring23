package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserType {
    @JsonProperty("普通用户") NORMAL_USER("普通用户"), @JsonProperty("管理员") ADMIN_USER("管理员");

    private String text;

    UserType(String text){this.text = text;}

    public String getText() {
        return this.text;
    }
}
