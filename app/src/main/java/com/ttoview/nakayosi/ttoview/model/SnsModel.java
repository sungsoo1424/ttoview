package com.ttoview.nakayosi.ttoview.model;

import java.io.Serializable;

public class SnsModel implements Serializable {

    private String snsId;
    private String userName;
    private String userEmail;
    private String gender;
    private String accessToken;
    private String avatarImageUrl;

    public String getSNSID() {
        return snsId;
    }

    public void setSNSID(String snsId) {
        this.snsId = snsId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }


    @Override
    public String toString() {
        return "SnsModel{" +
                "snsId='" + snsId + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", gender='" + gender + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", avatarImageUrl='" + avatarImageUrl + '\'' +
                '}';
    }
}
