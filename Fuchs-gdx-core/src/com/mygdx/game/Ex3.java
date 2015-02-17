package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import org.opencv.core.Point3;

public class Ex3 implements ApplicationListener 
{
	private VideoCapture cap;
    private Mat image;
    private Mat work_image;
    private Mat clean_image;
    private Environment environment;
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;
	private Model model;
	
	@Override
	public void create() 
    {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		cap = new VideoCapture();
       	image = new Mat();
       	work_image = new Mat();
       	clean_image = new Mat();

		cap.open(1);
		cap.read(image);
		while(image.type() == 0)
		{
			cap.read(image);
			System.err.println("Unable to acquire camera!");
		}
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(new Vector3(0f,0f,0f));
		cam.lookAt(0f, 0f, 0f);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
	}

	@Override
	public void render() 
    {
		Array<ModelInstance> instances = new Array<ModelInstance>();
		ModelBuilder modelBuilder = new ModelBuilder();
		Model arrow = modelBuilder.createArrow(0f, 0f, 0f, 1f, 0f, 0f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(arrow));
		arrow = modelBuilder.createArrow(0f, 0f, 0f, 0f, 1f, 0f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(arrow));
		arrow = modelBuilder.createArrow(0f, 0f, 0f, 0f, 0f, 1f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(arrow));
		// Read an image
		cap.read(image);
		clean_image = image.clone();
		
		// a) Create a binary image.
		// Grayscale it
		Imgproc.cvtColor(image, work_image, Imgproc.COLOR_BGR2GRAY);
		UtilAR.imShow("B/W", work_image);
		// Threshold it
		Imgproc.threshold(work_image, work_image, 50.0f, 255.0f, Imgproc.THRESH_BINARY);
		UtilAR.imShow("THRESHOLD", work_image);
		
		// b) Find the contours.
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(work_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(work_image, contours, -1, new Scalar(255));
		Imgproc.drawContours(image, contours, -1, new Scalar(0,0,255));
		UtilAR.imShow("CONTOUR", work_image);
		UtilAR.imShow("CONTOUR-COLOR", image);
		
		// c) Create approximative polygons.
		List<MatOfPoint> polygons = new ArrayList<MatOfPoint>();
		for(MatOfPoint contour : contours)
		{
			MatOfPoint2f thisContour2f = new MatOfPoint2f();
		    MatOfPoint approxContour = new MatOfPoint();
		    MatOfPoint2f approxContour2f = new MatOfPoint2f();
			
		    contour.convertTo(thisContour2f, CvType.CV_32FC2);
		    Imgproc.approxPolyDP(thisContour2f, approxContour2f, 2, true);
		    approxContour2f.convertTo(approxContour, CvType.CV_32S);
			
			polygons.add(approxContour);
		}
		Imgproc.drawContours(image, polygons, -1, new Scalar(0,255,0));
		UtilAR.imShow("POLY-COLOR", image);
		
		// d) Discard polygons which are not marker candidates.
		List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
		for(MatOfPoint polygon : polygons)
		{
			if(polygon.size().height == 4) {
				squares.add(polygon);
			}
		}
		Imgproc.drawContours(image, squares, -1, new Scalar(255,0,0));
		UtilAR.imShow("SQUARE-COLOR", image);
		
		// e) For each marker candidate draw the four edges and/or corners. Display the result image.
		Imgproc.drawContours(clean_image, squares, -1, new Scalar(255,255,0));
		UtilAR.imShow("SQUARE-CLEAN-COLOR", clean_image);
		
		// f) Use the PnP solver to render the 3D coordinate system onto the marker candidates.
		
		// g) Unwarp the content of the found marker candidate and display the unwarped image.
		
		/*
		for(MatOfPoint p : contours)
		{
			System.out.println(p);
		}
		*/
		
		
		
		//System.out.println(image.size());
		
		//image = Highgui.imread("/home/skeen/Desktop/Chess_Board.png");
		/*
		Size board = new Size(5,7);
		MatOfPoint2f corners = new MatOfPoint2f();
		
		boolean patternfound = Calib3d.findChessboardCorners(image,
                board,
                corners);
		
		if(patternfound)
		{
			Calib3d.drawChessboardCorners(image, board, corners, patternfound);
			
	        float cell_size = 1f;
	        
	        List<Point3> objPoints = new ArrayList<Point3>();
	        for(int i = 0; i < board.height; ++i)
	        {
	        	for(int j = 0; j < board.width; ++j)
	            {
	        		objPoints.add(new Point3(j*cell_size, 0.0f, i*cell_size));
	            }
	        }

	        MatOfPoint3f points3d = new MatOfPoint3f();
	        points3d.fromList(objPoints);
	        
			Mat rvec = new Mat();
			Mat tvec = new Mat();
			Calib3d.solvePnP(points3d, corners, UtilAR.getDefaultIntrinsicMatrix(image.cols(),image.rows()),
				UtilAR.getDefaultDistortionCoefficients(), rvec, tvec);
			
			UtilAR.setCameraByRT(rvec, tvec, cam);
		}        
                
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		*/
		/*
		Texture t = UtilAR.createCameraTexture(cap);
		UtilAR.imToTexture(image, t);
		UtilAR.texDrawBackground(t);;
		*/
		UtilAR.imDrawBackground(image);
		/*
		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();
		*/
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