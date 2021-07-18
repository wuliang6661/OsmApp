package com.heloo.android.osmapp.model;

/**
 * Created by Witness on 6/25/21
 * Describe:
 */
public class SignBean {

    /**
     * TeamType : 0
     * SigninType : 1
     * UserType : 会员
     */

    private String TeamType;
    private String SigninType;
    private String UserType;

    public String getTeamType() {
        return TeamType;
    }

    public void setTeamType(String TeamType) {
        this.TeamType = TeamType;
    }

    public String getSigninType() {
        return SigninType;
    }

    public void setSigninType(String SigninType) {
        this.SigninType = SigninType;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String UserType) {
        this.UserType = UserType;
    }
}
