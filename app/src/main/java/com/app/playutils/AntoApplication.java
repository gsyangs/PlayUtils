package com.app.playutils;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author:create by ys
 * 时间:2021/11/30 16
 * 邮箱 894417048@qq.com
 */
public class AntoApplication extends Application {

    private static AntoApplication instance;

    private boolean isOpenOKactivity;

    private List<Point> points;


    @Override
    public void onCreate() {
        super.onCreate();

        AntoApplication.instance = this;
        points = new ArrayList<>();
    }


    public static AntoApplication getInstance() {
        return AntoApplication.instance;
    }

    public boolean isOpenOKactivity() {
        return isOpenOKactivity;
    }

    public void setOpenOKactivity(boolean openOKactivity) {
        isOpenOKactivity = openOKactivity;
    }

    public void setPoint(Point point){
        if (points != null){
        } else {
            points = new ArrayList<>();
        }
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }
}