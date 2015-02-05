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
	
	private Mat floodfill(Mat matrix)
	{
		int[][] filled = new int[matrix.rows()][matrix.cols()];
		Mat output2 = Mat.zeros(matrix.size(), matrix.type());
		int[] temp = {0,0};
		int[] temp2 = {0,0};
		ArrayList<int[]> stack = new ArrayList<int[]>();
		double accx = 0;
		double accy = 0;
		double count = 0;
		double[] ar = new double[] {0};
        final double threshold = 0.005*5;
        int area = 0;
        
		for( int j = 0; j < matrix.rows(); j++ )
        {
            for( int i = 0; i < matrix.cols(); i++ )
            {
            	ar = matrix.get(j,i);
                area = filled[j][i];
                filled[j][i] = 1;
                if(ar[0] > threshold && area == 0)
                {
                	temp[0] = j;
                	temp[1] = i;
                	stack.add(temp.clone());
                	while(stack.isEmpty() == false)
                	{
                		temp = stack.remove(0);
                		ar = matrix.get(temp[0],temp[1]);
                		accx += temp[0]*ar[0];
                		accy += temp[1]*ar[0];
                		count += ar[0];
                		
                		if(temp[0] < matrix.rows()-1 && temp[1] < matrix.cols()-1 && temp[0] > 0 && temp[1] > 0)
                		{
	                		temp2[0] = temp[0]+1;
	                		temp2[1] = temp[1];
	                		
	                		ar = matrix.get(temp2[0],temp2[1]);
	                		area = filled[temp2[0]][temp2[1]];
	                		if(ar[0] > threshold && area == 0)
	                		{
	                			filled[temp2[0]][temp2[1]] = 1;
	                			stack.add(temp2.clone());
	                		}
	                		
	                		temp2[0] = temp[0]-1;
	                		ar = matrix.get(temp2[0],temp2[1]);
	                		area = filled[temp2[0]][temp2[1]];
	                		if(ar[0] > threshold && area == 0)
	                		{
	                			filled[temp2[0]][temp2[1]] = 1;
	                			stack.add(temp2.clone());
	                		}
	                		
	                		temp2[0] = temp[0];
	                		temp2[1] = temp[1]+1;
	                		ar = matrix.get(temp2[0],temp2[1]);
	                		area = filled[temp2[0]][temp2[1]];
	                		if(ar[0] > threshold && area == 0)
	                		{
	                			filled[temp2[0]][temp2[1]] = 1;
	                			stack.add(temp2.clone());
	                		}
	                		
	                		temp2[1] = temp[1]-1;
	                		ar = matrix.get(temp2[0],temp2[1]);
	                		area = filled[temp2[0]][temp2[1]];
	                		if(ar[0] > threshold && area == 0)
	                		{
	                			filled[temp2[0]][temp2[1]] = 1;
	                			stack.add(temp2.clone());
	                		}
                		}
                	}
                	//output2.put((int) (accy/count+0.5),(int) (accx/count+0.5),1);
                	if(count > 0.5)
                		Core.circle(image, new Point((int) (accy/count+0.5), (int) (accx/count+0.5)), 5, new Scalar(255, 255, 255));
                	accx = 0;
                	accy = 0;
                	count = 0;
                }
            }
        }
		return output2;
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
        
        floodfill(output);
        
        /// Drawing a circle around corners
    /*
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
    */
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
