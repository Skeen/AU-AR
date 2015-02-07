package com.mygdx.game;


import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;

import com.badlogic.gdx.ApplicationListener;

import org.opencv.core.Point3;

public class Ex2Cheat implements ApplicationListener 
{
	VideoCapture cap;
    Mat image;
	
	@Override
	public void create() 
    {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		cap = new VideoCapture();
        //System.out.println(cap.open(0));
       	image = new Mat();

		cap.open(1);
		cap.read(image);
		while(image.type() == 0)
			cap.read(image);
	}

	@Override
	public void render() 
    {
		cap.read(image);
		Size board = new Size(5,7);
		MatOfPoint2f corners = new MatOfPoint2f();
		
		boolean patternfound = Calib3d.findChessboardCorners(image,
                board,
                corners);
		
		if(patternfound)
		{
			Calib3d.drawChessboardCorners(image, board, corners, patternfound);
			
	        float cell_size = 1;
	        
	        Point3[] points = new Point3[(int)board.height*(int)board.width];
	        for(int i = 0; i < board.height; ++i)
	        {
	            for(int j = 0; j < board.width; ++j)
	            {
	            	points[j+(i*(int)board.width)] = new Point3(j*cell_size, i*cell_size, 0.0f);
	            }
	        }
	        
	        MatOfPoint3f points3d = new MatOfPoint3f(points);

			Mat rvec = new Mat();
			Mat tvec = new Mat();
			Calib3d.solvePnP(points3d, corners, UtilAR.getDefaultIntrinsicMatrix(1920,1080),
				UtilAR.getDefaultDistortionCoefficients(), rvec, tvec);
			
			System.out.print("{");
			for(int x = 0; x < rvec.rows(); x++)
			{
				System.out.print(rvec.get(x, 0)[0]);
				System.out.print(",");
			}
			System.out.println("}");
			System.out.print("[");
			for(int x = 0; x < tvec.rows(); x++)
			{
				System.out.print(tvec.get(x, 0)[0]);
				System.out.print(",");
			}
			System.out.println("]");
		}        
                
		UtilAR.imDrawBackground(image);
	}
	
	@Override
	public void dispose() 
	{
		cap.release();

        image.release();
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
