package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 顶部用户信息
 * Created by yi on 2017/9/20.
 */

public class UserTopEntity implements Parcelable {
        private String userId;
        private String userName;
        private String headPath;
        private String sex;
        private String levelColor;
        private int level;
        private BadgeEntity badge;
        private boolean vip;
        private boolean isCheck;
        private String signature;
        private String id;
        private String userIcon;
        private String rcTargetId;
        private String createTime;
        private int focus;//0 互相关注 1 关注 -1 未关注

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<UserTopEntity> CREATOR = new Parcelable.Creator<UserTopEntity>() {
        @Override
        public UserTopEntity createFromParcel(Parcel in) {
            UserTopEntity entity = new UserTopEntity();
            Bundle bundle;
            bundle = in.readBundle(getClass().getClassLoader());
            entity.userId = bundle.getString("userId");
            entity.userName = bundle.getString("userName");
            entity.headPath = bundle.getString("headPath");
            entity.sex = bundle.getString("sex");
            entity.levelColor = bundle.getString("levelColor");
            entity.level = bundle.getInt("level");
            entity.vip = bundle.getBoolean("vip");
            entity.isCheck = bundle.getBoolean("isCheck");
            entity.badge = bundle.getParcelable("badge");
            entity.signature = bundle.getString("signature");
            entity.userIcon = bundle.getString("userIcon");
            return entity;
        }

        @Override
        public UserTopEntity[] newArray(int size) {
            return new UserTopEntity[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("userName", userName);
        bundle.putString("headPath",headPath);
        bundle.putString("sex",sex);
        bundle.putString("levelColor",levelColor);
        bundle.putInt("level",level);
        bundle.putBoolean("vip",vip);
        bundle.putBoolean("isCheck",isCheck);
        bundle.putParcelable("badge",badge);
        bundle.putString("signature",signature);
        bundle.putString("userIcon",userIcon);
        parcel.writeBundle(bundle);
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLevelColor() {
        return levelColor;
    }

    public void setLevelColor(String levelColor) {
        this.levelColor = levelColor;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public BadgeEntity getBadge() {
        return badge;
    }

    public void setBadge(BadgeEntity badge) {
        this.badge = badge;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public void setRcTargetId(String rcTargetId) {
        this.rcTargetId = rcTargetId;
    }

    public String getRcTargetId() {
        return rcTargetId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getFocus() {
        return focus;
    }

    public void setFocus(int focus) {
        this.focus = focus;
    }
}
