package com.app.playutils;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:create by ys
 * 时间:2021/11/30 16
 * 邮箱 894417048@qq.com
 */
public class AntoApplication extends Application {

    private static AntoApplication instance;

    private boolean isOpenOKactivity;

    private List<DotPoint> points;

    //当前界面可点击图标
    private DotPoint point;
    //当前停留功能界面
    private int Activity;
    //当前体力  最大20
    private int Phys;
    //当前胜数
    private int victorys;


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

    public void setPoints(DotPoint point){
        if (points != null){
        } else {
            points = new ArrayList<>();
        }
        points.add(point);
    }

    public List<DotPoint> getPoints() {
        return points;
    }

    public void setPoint(DotPoint point) {
        this.point = point;
    }

    public DotPoint getPoint() {
        return point;
    }

    public int getActivity() {
        return Activity;
    }

    public void setActivity(int activity) {
        Activity = activity;
    }

    public int getPhys() {
        return Phys;
    }

    public void setPhys(int phys) {
        Phys = phys;
    }

    public int getVictorys() {
        return victorys;
    }

    public void setVictorys(int victorys) {
        this.victorys = victorys;
    }
}