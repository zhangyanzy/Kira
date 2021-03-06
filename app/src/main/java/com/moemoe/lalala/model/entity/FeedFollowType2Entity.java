package com.moemoe.lalala.model.entity;

import java.util.ArrayList;

/**
 * Created by yi on 2018/1/16.
 */

public class FeedFollowType2Entity {

    private String bg;
    private String title;
    private String content;
    private int fileNums;
    private int docNums;
    private int focusNums;

    public int getFocusNums() {
        return focusNums;
    }

    public boolean getJoin() {
        return join;
    }

    public boolean getManager() {
        return manager;
    }

    public void setFocusNums(int focusNums) {
        this.focusNums = focusNums;
    }

    private boolean join;

    public void setJoin(boolean join) {
        this.join = join;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    private boolean manager;
    private ArrayList<UserTopEntity> admins;


    public FeedFollowType2Entity() {
        admins = new ArrayList<>();
    }

    public ArrayList<UserTopEntity> getAdmins() {
        return admins;
    }

    public void setAdmins(ArrayList<UserTopEntity> admins) {
        this.admins = admins;
    }

    public int getFileNums() {
        return fileNums;
    }

    public void setFileNums(int fileNums) {
        this.fileNums = fileNums;
    }

    public int getDocNums() {
        return docNums;
    }

    public void setDocNums(int docNums) {
        this.docNums = docNums;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }
}
