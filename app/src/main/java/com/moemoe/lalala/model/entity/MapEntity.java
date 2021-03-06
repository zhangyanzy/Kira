package com.moemoe.lalala.model.entity;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;

/**
 *
 * Created by yi on 2017/10/10.
 */

public class MapEntity {
    private String id; // id
    private Image image;// 图片信息
    private String schema;// 跳转地址
    private int pointX;// x坐标
    private int pointY;// y坐标
    private String text;// 显示文字
    private String shows;// 时间段显示
    private String name;
    private String md5;
    private int layer;//图片图层
    private int type;//类型：1，家具 2.角色 3，垃圾
    private HouseLikeEntity roleTimer;//角色好感度计时器


    public HouseLikeEntity getRoleTimer() {
        return roleTimer;
    }

    public void setRoleTimer(HouseLikeEntity roleTimer) {
        this.roleTimer = roleTimer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getPointX() {
        return pointX;
    }

    public void setPointX(int pointX) {
        this.pointX = pointX;
    }

    public int getPointY() {
        return pointY;
    }

    public void setPointY(int pointY) {
        this.pointY = pointY;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getShows() {
        return shows;
    }

    public void setShows(String shows) {
        this.shows = shows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
