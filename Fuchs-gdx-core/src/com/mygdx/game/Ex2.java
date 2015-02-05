package com.mygdx.game;

import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Ex2 implements ApplicationListener {
	VideoCapture cap;
	Mat image = new Mat();
	Mat greyimage = new Mat();
	Mat output = new Mat();
	
	@Override
	public void create() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		cap = new VideoCapture();
        //System.out.println(cap.open(0));
		cap.open(0);
		cap.read(image);
		while(image.type() == 0)
			cap.read(image);
	}
	
	private Mat detectcorners() {
		cap.read(image);
		Imgproc.Canny(image, greyimage, 200, 200);
		Imgproc.cornerHarris(greyimage, output, 2, 3, 0.04);
		return output;
	}
	
	@Override
	public void render() {
		Mat avg;
		for(int i=0; i<=5; i++) {
			
		}
		cap.read(image);
		//image = Mat.eye(new Size(800,600), 16);
		//Core.multiply(image, new Scalar(255,0,0), image);
	
		//Mat output = Mat.zeros(image.size(), CvType.CV_8UC1);
		//Imgproc.cvtColor(image, greyimage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(image, greyimage, 200, 200);
		Imgproc.cornerHarris(greyimage, output, 2, 3, 0.04);
		//Mat eye = Mat.eye(128, 128, CvType.CV_8UC1);
		//Core.multiply(eye, new Scalar(255), eye);
        /// Drawing a circle around corners
        for( int j = 0; j < output.rows(); j++ )
        {
            for( int i = 0; i < output.cols(); i++ )
            {
                double[] arr = output.get(j,i);
                final double threshold = 0.005;
                if(arr[0] > threshold)
                {
                    Core.circle(image, new Point(i, j), 15, new Scalar(255, 255, 255));
                }
            }
        }
		UtilAR.imDrawBackground(image);
		//UtilAR.imShow(eye);
		//image.release();
	}
	
	@Override
	public void dispose() {
		cap.release();
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