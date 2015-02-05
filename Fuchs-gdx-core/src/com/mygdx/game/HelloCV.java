package com.mygdx.game;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class HelloCV {
    public static void main(String[] args) {
        System.loadLibrary("opencv_java2410");
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());
    }
}