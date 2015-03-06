package com.mygdx.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
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
	private ArrayList<PerspectiveCamera> cam = new ArrayList<PerspectiveCamera>();
	private ModelBatch modelBatch;
	private Model model;
	private Model model2;
	//import templates in all 4 possible turns
	private Mat triangle1 = Highgui.imread("assets/drei1.jpg");
	private Mat triangle2 = Highgui.imread("assets/drei2.jpg");
	private Mat triangle3 = Highgui.imread("assets/drei3.jpg");
	private Mat triangle4 = Highgui.imread("assets/drei4.jpg");
	private Mat corner1 = Highgui.imread("assets/eck1.jpg");
	private Mat corner2 = Highgui.imread("assets/eck2.jpg");
	private Mat corner3 = Highgui.imread("assets/eck3.jpg");
	private Mat corner4 = Highgui.imread("assets/eck4.jpg");
	
	@Override
	public void create() 
    {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		cap = new VideoCapture();
       	image = new Mat();
       	work_image = new Mat();
       	clean_image = new Mat();

		cap.open(0);
		
		boolean wset = cap.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 1280);
		boolean hset = cap.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 720);

		System.out.println(wset);
		System.out.println(hset);
		
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
		
		cam.add(new PerspectiveCamera(39, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		cam.add(new PerspectiveCamera(39, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		//cam changed to an array to render something on multiple markers
		for(PerspectiveCamera c : cam)
		{
			c.position.set(new Vector3(5f,5f,5f));
			c.lookAt(0f, 0f, 0f);
			c.near = 1f;
			c.far = 500f;
			c.update();
		}
		
	}

	@Override
	public void render() 
    {
		Array<Array<ModelInstance>> instances = new Array<Array<ModelInstance>>();
		ModelBuilder modelBuilder = new ModelBuilder();
		/*Model arrow = modelBuilder.createArrow(0f, 0f, 0f, 1f, 0f, 0f,
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
		instances.add(new ModelInstance(arrow));*/
		
		//ModelLoader loader = new ObjLoader();
		//model = loader.loadModel(new FileHandle(new File("C:\\Users\\Martin\\workspace\\Fuchs-gdx-core\\Bishop.g3db")));
        //model = loader.loadModel(Gdx.files.internal("Bishop.g3db"));
        //instances.add(new ModelInstance(model));
		
		// Read an image
		cap.read(image);
		clean_image = image.clone();
		
		// a) Create a binary image.
		// Grayscale it
		Imgproc.cvtColor(image, work_image, Imgproc.COLOR_BGR2GRAY);
		//UtilAR.imShow("B/W", work_image);
		// Threshold it
		Imgproc.threshold(work_image, work_image, 50.0f, 255.0f, Imgproc.THRESH_BINARY);
		//UtilAR.imShow("THRESHOLD", work_image);
		
		// b) Find the contours.
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(work_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		//Imgproc.drawContours(work_image, contours, -1, new Scalar(255));
		//Imgproc.drawContours(image, contours, -1, new Scalar(0,0,255));
		//UtilAR.imShow("CONTOUR", work_image);
		//UtilAR.imShow("CONTOUR-COLOR", image);
		
		// c) Create approximative polygons.
		List<MatOfPoint> polygons = new ArrayList<MatOfPoint>();
		for(MatOfPoint contour : contours)
		{
			MatOfPoint2f thisContour2f = new MatOfPoint2f();
		    MatOfPoint approxContour = new MatOfPoint();
		    MatOfPoint2f approxContour2f = new MatOfPoint2f();
			
		    contour.convertTo(thisContour2f, CvType.CV_32FC2);
		    Imgproc.approxPolyDP(thisContour2f, approxContour2f, 5, true);
		    approxContour2f.convertTo(approxContour, CvType.CV_32S);
			
			polygons.add(approxContour);
		}
		//Imgproc.drawContours(image, polygons, -1, new Scalar(0,255,0));
		//UtilAR.imShow("POLY-COLOR", image);
		
		// d) Discard polygons which are not marker candidates.
		List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
		for(MatOfPoint polygon : polygons)
		{
			if(polygon.size().height == 4 && Imgproc.isContourConvex(polygon)) {
				squares.add(polygon);
			}
		}
		Collections.sort(squares, new Comparator<MatOfPoint>() 
        {

            public int compare(MatOfPoint p1, MatOfPoint p2) 
            {
                
                double p1area = Imgproc.contourArea(p1);
                double p2area = Imgproc.contourArea(p2);
                if(p1area > p2area)
                	return -1;
                else if(p1area < p2area)
                	return 1;
                else
                	return 0;
                

                // it can also return 0, and 1
            }
           }
		);
		
		//squares is sorted by size
		// maybe just use collections max
		List<MatOfPoint> areasquares = new ArrayList<MatOfPoint>();
		if(squares.isEmpty() == false)
		{
			double biggest = Imgproc.contourArea(squares.get(0));
			for(MatOfPoint square : squares)
			{
				double current = Imgproc.contourArea(square);
				if(current > 0.5*biggest)
				{
					areasquares.add(square);
				}
			}
		}
		
		List<MatOfPoint> crosspsquares = new ArrayList<MatOfPoint>();
		if(squares.isEmpty() == false)
		{
			for(MatOfPoint square : areasquares)
			{
				double[] corner1 = square.get(0, 0);
				double[] corner2 = square.get(1, 0);
				double[] corner3 = square.get(2, 0);
				double a = corner1[0]-corner2[0];
				double x = corner3[0]-corner2[0];
				double b = corner1[1]-corner2[1];
				double y = corner3[1]-corner2[1];
				double crossp = a*y-b*x;
				if(crossp < 0)
				{
					crosspsquares.add(square);
				}

			}
		}
		
		//Imgproc.drawContours(image, crosspsquares, -1, new Scalar(255,0,0));
		//UtilAR.imShow("SQUARE-COLOR", image);
		
		// e) For each marker candidate draw the four edges and/or corners. Display the result image.
		Imgproc.drawContours(clean_image, crosspsquares, -1, new Scalar(255,255,0));
		//UtilAR.imShow("SQUARE-CLEAN-COLOR", clean_image);
		
		int i = 0;
		for(MatOfPoint mat_square : crosspsquares)
		{
			i++;
			MatOfPoint2f square = new MatOfPoint2f();
			mat_square.convertTo(square, CvType.CV_32FC2);
			
			// f) Use the PnP solver to render the 3D coordinate system onto the marker candidates.
			Point3[] points = {new Point3(0,0,0),new Point3(0,0,1),new Point3(1,0,1),new Point3(1,0,0)};
			MatOfPoint3f points3d = new MatOfPoint3f(points);

		Mat rvec = new Mat();
		Mat tvec = new Mat();
		Calib3d.solvePnP(points3d, square, UtilAR.getDefaultIntrinsicMatrix(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()),
			UtilAR.getDefaultDistortionCoefficients(), rvec, tvec);
		
		UtilAR.setCameraByRT(rvec, tvec, cam.get(i-1));
		System.out.println("HIT!");
		
		// g) Unwarp the content of the found marker candidate and display the unwarped image.
		List<Point> homepoints = new ArrayList<Point>();
		homepoints.add(new Point(0,0));
		homepoints.add(new Point(image.width(),0));
		homepoints.add(new Point(image.width(),image.height()));
		homepoints.add(new Point(0,image.height()));

		MatOfPoint2f homopoints = new MatOfPoint2f();
		homopoints.fromList(homepoints);
		
		Mat M = Calib3d.findHomography(square, homopoints);
		Mat dest = new Mat();
		Imgproc.warpPerspective(image, dest, M, image.size());
		
		//compare warped image with all the templates to determine the right one
		Mat result1 = new Mat();
		Mat result2 = new Mat();
		Mat result3 = new Mat();
		Mat result4 = new Mat();
		Mat result5 = new Mat();
		Mat result6 = new Mat();
		Mat result7 = new Mat();
		Mat result8 = new Mat();
		
		Imgproc.matchTemplate(dest, triangle1, result1, 1);
		Imgproc.matchTemplate(dest, triangle2, result2, 1);
		Imgproc.matchTemplate(dest, triangle3, result3, 1);
		Imgproc.matchTemplate(dest, triangle4, result4, 1);
		Imgproc.matchTemplate(dest, corner1, result5, 1);
		Imgproc.matchTemplate(dest, corner2, result6, 1);
		Imgproc.matchTemplate(dest, corner3, result7, 1);
		Imgproc.matchTemplate(dest, corner4, result8, 1);
		
		//UtilAR.imShow("OUTPUT-COLOR" + i, dest);
		
		
		// the results from the match template function differ a lot, doesnt work with a fixed threshold
		// determine outliers by comparing to the average
		double average = (result1.get(0, 0)[0]+result2.get(0, 0)[0]+result3.get(0, 0)[0]+result4.get(0, 0)[0]+result5.get(0, 0)[0]+result6.get(0, 0)[0]+result7.get(0, 0)[0]+result8.get(0, 0)[0])/8f;
		double average1 = (result1.get(0, 0)[0]+result2.get(0, 0)[0]+result3.get(0, 0)[0]+result4.get(0, 0)[0])/4f;
		double average2 = (result5.get(0, 0)[0]+result6.get(0, 0)[0]+result7.get(0, 0)[0]+result8.get(0, 0)[0])/4f;
		
		// instances is an array of arrays, because we have multiple worlds, one on each marker
		instances.add(new Array<ModelInstance>());
		
		// giant IF to determine the right marker
		if(result1.get(0, 0)[0] < 0.85f*average && result1.get(0, 0)[0] < 0.9f*average1)
		{
		model = modelBuilder.createArrow(0.5f, 0f, 0.5f, 0f, 0f, 0.5f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model));
		}
		if(result2.get(0, 0)[0] < 0.85f*average && result2.get(0, 0)[0] < 0.9f*average1)
		{
		model = modelBuilder.createArrow(0.5f, 0f, 0.5f, 0.5f, 0f, 1f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model));
		}
		if(result3.get(0, 0)[0] < 0.85f*average && result3.get(0, 0)[0] < 0.9f*average1)
		{
		model = modelBuilder.createArrow(0.5f, 0f, 0.5f, 1f, 0f, 0.5f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model));
		}
		if(result4.get(0, 0)[0] < 0.85f*average && result4.get(0, 0)[0] < 0.9f*average1)
		{
		model = modelBuilder.createArrow(0.5f, 0f, 0.5f, 0.5f, 0f, 0f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model));
		}
				
		if(result5.get(0, 0)[0] < 0.85f*average && result5.get(0, 0)[0] < 0.9f*average2)
		{
		model2 = modelBuilder.createBox(0.5f, 0.5f, 0.5f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model2, 1, 0, 0));
		}
		if(result6.get(0, 0)[0] < 0.85f*average && result6.get(0, 0)[0] < 0.9f*average2)
		{
		model2 = modelBuilder.createBox(0.5f, 0.5f, 0.5f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model2));
		}
		if(result7.get(0, 0)[0] < 0.85f*average && result7.get(0, 0)[0] < 0.9f*average2)
		{
		model2 = modelBuilder.createBox(0.5f, 0.5f, 0.5f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model2, 0, 0, 1));
		}
		if(result8.get(0, 0)[0] < 0.85f*average && result8.get(0, 0)[0] < 0.9f*average2)
		{
		model2 = modelBuilder.createBox(0.5f, 0.5f, 0.5f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
		instances.get(i-1).add(new ModelInstance(model2, 1, 0, 1));
		}
		
		/*Imgproc.cvtColor(dest, destC, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(triangle, triangleC, Imgproc.COLOR_BGR2GRAY);
		Imgproc.calcHist(triangle, channels, mask, hist, histSize, ranges);
		
		
		double corr = Imgproc.compareHist(destC, triangleC, Imgproc.CV_COMP_CORREL);
		System.out.println(corr);*/
		
		
		}
		
		UtilAR.imDrawBackground(clean_image);
		
		// render all the different worlds
		for( int j=0; j<i; j++)
		{
		modelBatch.begin(cam.get(j));
		modelBatch.render(instances.get(j), environment);
		modelBatch.end();
		}
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