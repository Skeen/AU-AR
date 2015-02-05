package com.mygdx.game;
import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
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
	@Override
	public void create() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		cap = new VideoCapture();
        //System.out.println(cap.open(0));
		cap.open(0);
	}
	
	@Override
	public void render() {
		Mat image = new Mat();
		Mat greyimage = new Mat();
		cap.read(image);
		image.convertTo(image, CvType.CV_8UC1);
		Mat output = Mat.zeros(image.size(), CvType.CV_8UC1);
		//Imgproc.cvtColor(image, greyimage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cornerHarris(image, output, 2, 3, 0.04);
		//Mat eye = Mat.eye(128, 128, CvType.CV_8UC1);
		//Core.multiply(eye, new Scalar(255), eye);
		UtilAR.imDrawBackground(image);
		//UtilAR.imShow(eye);
		image.release();
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