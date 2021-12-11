package com.app.playutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

/**
 * @author:create by ys
 * 时间:2021/12/11 11
 * 邮箱 894417048@qq.com
 */
public class Workflow {


    /**
     * 登录
     */
    public static DotPoint clickView(Context context, Bitmap bitmap, int drawable){
        try {
            Mat dstmat = Utils.loadResource(context, drawable);
            Mat srcmat = new Mat();
            Utils.bitmapToMat(bitmap,srcmat);
            DotPoint p = OpencvOCR.ocr(2,srcmat,dstmat);
            if (p.getMaxVal() > 0.55) {
                return p;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 点击关卡
     */
    public static DotPoint clickLevel(Bitmap bitmap){
        Mat srcmat = new Mat();
        Utils.bitmapToMat(bitmap,srcmat);
        DotPoint p = OpencvOCR.findColorPoint(3,srcmat);
        if (p.getMaxVal() > 0.8) {
            return p;
        } else {
            return null;
        }
    }
} 