package com.app.playutils;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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
    public static com.app.playutils.Point ocr(int index, Mat srcmat, Mat dstmat){
       return matchTemplate(index,srcmat, dstmat, Imgproc.TM_CCOEFF_NORMED);
    }

    /**
     * 模板匹配法
     * @param index
     * @param inMat
     * @param tempMat
     * @param method
     */
    private static com.app.playutils.Point matchTemplate(int index,Mat inMat, Mat tempMat, int method) {

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

        com.app.playutils.Point point = new com.app.playutils.Point();
        point.setIndex(index);
        point.setMinX(NumberUtils.dtol(mmr.maxLoc.x));
        point.setMiny(NumberUtils.dtol(mmr.maxLoc.y));
        point.setMaxx(NumberUtils.dtol(mmr.maxLoc.x + templatW));
        point.setMaxy(NumberUtils.dtol(mmr.maxLoc.y+ templatH));

//        Bitmap bitmap = Bitmap.createBitmap(inMat1.width(), inMat1.height(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(inMat1, bitmap);
//        iv.setImageBitmap(bitmap);
        return point;
    }

} 