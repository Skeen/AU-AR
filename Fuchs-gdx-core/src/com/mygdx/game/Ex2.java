package com.mygdx.game;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.badlogic.gdx.ApplicationListener;

public class Ex2 implements ApplicationListener 
{
	VideoCapture cap;
    Mat image;
    Mat greyimage;
    Mat output;
    ArrayList<Mat> corners = new ArrayList<Mat>();
	
	@Override
	public void create() 
    {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		cap = new VideoCapture();
        //System.out.println(cap.open(0));
       	image = new Mat();
		greyimage = new Mat();
        output = new Mat();

		cap.open(0);
		cap.read(image);
		while(image.type() == 0)
			cap.read(image);
	}
	
	private Mat detectcorners()
    {
		cap.read(image);
		Imgproc.Canny(image, greyimage, 200, 200);
		Imgproc.cornerHarris(greyimage, output, 2, 3, 0.04);
		return output;
	}
	
	@Override
	public void render() 
    {
        Mat temp = detectcorners();
        Mat alfa = Mat.zeros(temp.size(), temp.type());
        for(int x=0; x<corners.size(); x++)
        {
            Imgproc.accumulate(corners.get(x), alfa);
        }
        corners.add(temp);
        if(corners.size() > 5)
        {
            Mat del = corners.remove(0);
            del.release();
        }
        output = alfa;
    
        /// Drawing a circle around corners
        for( int j = 0; j < output.rows(); j++ )
        {
            for( int i = 0; i < output.cols(); i++ )
            {
                double[] arr = output.get(j,i);
                final double threshold = 0.005*5;
                if(arr[0] > threshold)
                {
                    Core.circle(image, new Point(i, j), 5, new Scalar(255, 255, 255));
                }
            }
        }
		UtilAR.imDrawBackground(image);
	}
	
	@Override
	public void dispose() 
	{
		cap.release();

        image.release();
        greyimage.release();
        output.release();
	}
	
	@Override
	public void resize(int width, int height) {
	}
	
	@Override
	public void pause() {
	}
	
	@Override
	public void resume() {
	}
}
