package com.app.playutils;

/**
 * @author:create by ys
 * 时间:2021/12/1 13
 * 邮箱 894417048@qq.com
 */
public class Point {
    //第几步
    private int index;
    //左上角x坐标
    private float minX;
    //左上角y坐标
    private float miny;
    //右上角x坐标
    private float maxx;
    //右上角y坐标
    private float maxy;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMiny() {
        return miny;
    }

    public void setMiny(float miny) {
        this.miny = miny;
    }

    public float getMaxx() {
        return maxx;
    }

    public void setMaxx(float maxx) {
        this.maxx = maxx;
    }

    public float getMaxy() {
        return maxy;
    }

    public void setMaxy(float maxy) {
        this.maxy = maxy;
    }
}