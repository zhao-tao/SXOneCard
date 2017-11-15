package com.sxonecard.http.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-4-27.
 * type：1 图片类型 2 视频类型
 */

public class AdBean {

    private String Starttime;
    private String Endtime;
    private String Type;
    private List<String> Filepath = new ArrayList<String>();
    private String Nexttime;

    public String getStarttime() {
        return Starttime;
    }

    public void setStarttime(String starttime) {
        Starttime = starttime;
    }

    public String getEndtime() {
        return Endtime;
    }

    public void setEndtime(String endtime) {
        Endtime = endtime;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public List<String> getFilepath() {
        return Filepath;
    }

    public void setFilepath(List<String> filepath) {
        Filepath = filepath;
    }

    public String getNexttime() {
        return Nexttime;
    }

    public void setNexttime(String nexttime) {
        Nexttime = nexttime;
    }

}
