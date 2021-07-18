package com.heloo.android.osmapp.model;

/**
 * Created by Witness on 5/8/21
 * Describe:
 */
public class TokenBean {
    /**
     * tokenHead : Bearer
     * token : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNzc4ODU1MjA4NiIsImNyZWF0ZWQiOjE2MjA0NjgzMDk3NTUsImV4cCI6MTYyMTA3MzEwOX0.tJSliul4TU5cK7AVWuPOTXzlXO1ENXt7BCfOFTou5C23uaVlfsTOBIBxaBKbJmDV4pzzl8dxzrCJb_nXFA5JjA
     */

    private String tokenHead;
    private String token;

    public String getTokenHead() {
        return tokenHead;
    }

    public void setTokenHead(String tokenHead) {
        this.tokenHead = tokenHead;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
