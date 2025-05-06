package com.group20.dailyreadingtracker.user;

import java.io.Serializable;

/**
 * Value Object representing a user's data for API responses.
 * 
 * <p>Contains all user-related information including:
 * <ul>
 *   <li>Basic profile data</li>
 *   <li>Account status flags</li>
 *   <li>Avatar information</li>
 * </ul>
 * 
 * @author Yongrun Huang
 * @author Sofiia Mamonova
 */

public class UserVo implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String avatarFileName;
    private Boolean isEnabled;
    private Boolean isFreezed;
    private Integer timesRecentlyBeingFlagged;
    private String contactInfo;
    private String signature;

    public UserVo() {
    }

    public UserVo(Long id, String username, String email, String avatarFileName,
            Boolean isEnabled, Boolean isFreezed, Integer timesRecentlyBeingFlagged,
            String contactInfo, String signature) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatarFileName = avatarFileName;
        this.isEnabled = isEnabled;
        this.isFreezed = isFreezed;
        this.timesRecentlyBeingFlagged = timesRecentlyBeingFlagged;
        this.contactInfo = contactInfo;
        this.signature = signature;
    }

    public UserVo(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.avatarFileName = user.getAvatarFileName();
        this.isEnabled = user.isEnabled();
        this.isFreezed = user.isFreezed();
        this.timesRecentlyBeingFlagged = user.getRecentlyFlagged();
        this.contactInfo = user.getContactInfo();
        this.signature = user.getSignature();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarFileName() {
        return avatarFileName;
    }

    public void setAvatarFileName(String avatarFileName) {
        this.avatarFileName = avatarFileName;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getIsFreezed() {
        return isFreezed;
    }

    public void setIsFreezed(Boolean isFreezed) {
        this.isFreezed = isFreezed;
    }

    public Integer getTimesRecentlyBeingFlagged() {
        return timesRecentlyBeingFlagged;
    }

    public void setTimesRecentlyBeingFlagged(Integer timesRecentlyBeingFlagged) {
        this.timesRecentlyBeingFlagged = timesRecentlyBeingFlagged;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", avatarFileName='" + avatarFileName + '\'' +
                ", isEnabled=" + isEnabled +
                ", isFreezed=" + isFreezed +
                ", timesRecentlyBeingFlagged=" + timesRecentlyBeingFlagged +
                ", contactInfo='" + contactInfo + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}