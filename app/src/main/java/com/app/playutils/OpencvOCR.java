package com.app.playutils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:create by ys
 * 时间:2021/12/1 09
 * 邮箱 894417048@qq.com
 */
public class OpencvOCR {

    public static final String TAG = "opencv++++";
    /**
     * @param index 步骤
     * @param srcmat 原图
     * @param dstmat 模板图片
     */
    public static DotPoint ocr(int index, Mat srcmat, Mat dstmat){
       return matchTemplate(index,srcmat, dstmat, Imgproc.TM_CCOEFF_NORMED);
    }

    /**
     * 模板匹配法
     * @param index
     * @param inMat
     * @param tempMat
     * @param method
     */
    private static DotPoint matchTemplate(int index, Mat inMat, Mat tempMat, int method) {

        Mat inMat1 = new Mat();
        Mat tempMat1 = new Mat();

        Imgproc.cvtColor(inMat, inMat1, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.cvtColor(tempMat, tempMat1, Imgproc.COLOR_BGRA2GRAY);

        int templatW = tempMat1.width();
        int templatH = tempMat1.height();

        int result_cols = inMat1.cols() - tempMat1.cols() + 1;
        int result_rows = inMat1.rows() - tempMat1.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        Imgproc.matchTemplate(inMat1, tempMat1, result, method);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        //在原图上的对应模板可能位置画一个绿色矩形
        //Imgproc.rectangle(inMat1, mmr.maxLoc, new Point(mmr.maxLoc.x + templatW, mmr.maxLoc.y + templatH), new Scalar(0, 255, 0), 5);
        Log.e(TAG,templatW + "  " + templatH);
        Log.e(TAG, "匹配的值：" + mmr.maxVal + "   ------左上角坐标：" + mmr.maxLoc.x + "," + mmr.maxLoc.y + "   ------右下角坐标：" + (mmr.maxLoc.x + templatW) + "," + (mmr.maxLoc.y+ templatH));

        DotPoint point = new DotPoint();
        point.setIndex(index);
        point.setMinX(NumberUtils.dtol(mmr.maxLoc.x));
        point.setMiny(NumberUtils.dtol(mmr.maxLoc.y));
        point.setMaxx(NumberUtils.dtol(mmr.maxLoc.x + templatW));
        point.setMaxy(NumberUtils.dtol(mmr.maxLoc.y+ templatH));
        point.setMaxVal(mmr.maxVal);
//        Bitmap bitmap = Bitmap.createBitmap(inMat1.width(), inMat1.height(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(inMat1, bitmap);
//        iv.setImageBitmap(bitmap);
        return point;
    }

    //橘黄色  0 22
    //黄色 22 38
    //绿色 38 75
    //蓝色 75 130
    //紫色 130 160
    //红色 160 179
    //mat默认是BGR
    public static DotPoint findColorPoint(int index,Mat srcmat){
        Bitmap bitmap = Bitmap.createBitmap(srcmat.width(),srcmat.height(),Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(srcmat,srcmat,Imgproc.COLOR_BGR2RGB);
        Utils.matToBitmap(srcmat,bitmap);
        //定义一个颜色 mat
        Mat hsvmat = new Mat();
        //hsv 转换
        Imgproc.cvtColor(srcmat, hsvmat, Imgproc.COLOR_RGB2HSV);
        //查找橘黄色按钮轮廓
        Core.inRange(hsvmat,new Scalar(15,220,40),new Scalar(25,255,255),hsvmat);
        //获取矩阵
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3.2,3.2));
        //开运算
        Imgproc.morphologyEx(hsvmat,hsvmat,Imgproc.MORPH_OPEN,kernel);
        //闭运算
        Imgproc.morphologyEx(hsvmat,hsvmat,Imgproc.MORPH_CLOSE,kernel);
        //找轮廓数量
        List<MatOfPoint> contours = new ArrayList<>();
        Mat outMat = new Mat();
        Imgproc.findContours(hsvmat,contours,outMat,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);
        Log.e(TAG,  "轮廓数量：" + contours.size());
        //画轮廓
        //Imgproc.drawContours(srcmat,contours,-1,new Scalar(0, 0, 255),2);
        List<Point> list = new ArrayList();
        //取最y 中心点坐标
        double miny = 0;
        double minx = 0;
        for (int i = 0; i < contours.size(); i++) {
            //去掉小面积轮廓
            //if (Imgproc.contourArea(contours.get(i)) < srcmat.size().area() / (1000)) {
            //    continue;
            //}
            MatOfPoint ptmat = contours.get(i);
            // 取中心坐标点坐标 (Red)
            MatOfPoint2f ptmat2 = new MatOfPoint2f(ptmat.toArray());
            RotatedRect bbox = Imgproc.minAreaRect(ptmat2);
            System.out.println("第"+ (i+1) +"个轮廓的中心点 x坐标：" + bbox.center.x + "  Y坐标：" + bbox.center.y);
            if (miny == 0){
                miny = bbox.center.y;
                minx = bbox.center.x;
            } else if (miny > bbox.center.y){
                miny = bbox.center.y;
                minx = bbox.center.x;
            }

            //画中心点
            //color = new Scalar(255, 0, 0);
            //Imgproc.circle(srcmat, bbox.center, 5, color, -1);
            list.add(bbox.center);
            //画矩形轮廓
            //Rect box = bbox.boundingRect();
            //color = new Scalar(0, 255, 0);
            //Imgproc.rectangle(srcmat, box.tl(), box.br(), color, 2);
        }

        DotPoint point = new DotPoint();
        point.setIndex(index);
        point.setMinX(NumberUtils.dtol(minx));
        point.setMiny(NumberUtils.dtol(miny));
        point.setMaxx(NumberUtils.dtol(minx));
        point.setMaxy(NumberUtils.dtol(miny));
        point.setMaxVal(1);

        return point;
    }

} 