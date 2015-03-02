package com.mygdx.game;


import java.io.File;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Size;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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

public class Project implements ApplicationListener 
{
	private VideoCapture cap;
    private Mat image;
    private Environment environment;
	private PerspectiveCamera cam;
	private ModelBatch modelBatch;
	private Model model;
	
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
		{
			cap.read(image);
			System.err.println("Unable to acquire camera!");
		}
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(39, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(new Vector3(50f,50f,50f));
		cam.lookAt(0f, 0f, 0f);
		cam.near = 1f;
		cam.far = 500f;
		cam.update();
		/*
		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(new FileHandle(new File("/home/skeen/Desktop/chess/rook.obj")));
		*/
		
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createBox(1f, 1f, 1f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
					Usage.Position | Usage.Normal);
	}

	@Override
	public void render() 
    {
		Array<ModelInstance> instances = new Array<ModelInstance>();
		ModelBuilder modelBuilder = new ModelBuilder();
		Model arrow = modelBuilder.createArrow(0f, 0f, 0f, 10f, 0f, 0f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(arrow));
		arrow = modelBuilder.createArrow(0f, 0f, 0f, 0f, 10f, 0f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(arrow));
		arrow = modelBuilder.createArrow(0f, 0f, 0f, 0f, 0f, 10f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(arrow));
		
		cap.read(image);
		//image = Highgui.imread("/home/skeen/Desktop/Chess_Board_full.png");
		
		Size board = new Size(5,7);
		MatOfPoint2f corners = new MatOfPoint2f();
		
		boolean patternfound = Calib3d.findChessboardCorners(image,
                board,
                corners);
		
		if(patternfound)
		{
			Calib3d.drawChessboardCorners(image, board, corners, patternfound);
			
	        float cell_size = 1f;
	        Point3[] points = new Point3[(int)board.height*(int)board.width];
	        for(int i = 0; i < board.height; ++i)
	        {
	            for(int j = 0; j < board.width; ++j)
	            {
	            	points[j+(i*(int)board.width)] = new Point3(j*cell_size, 0.0f, i*cell_size);
	            }
	        }
	        
	        MatOfPoint3f points3d = new MatOfPoint3f(points);

			Mat rvec = new Mat();
			Mat tvec = new Mat();
			Calib3d.solvePnP(points3d, corners, UtilAR.getDefaultIntrinsicMatrix(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()),
				UtilAR.getDefaultDistortionCoefficients(), rvec, tvec);
			
			UtilAR.setCameraByRT(rvec, tvec, cam);
		}        
                
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		instances.add(new ModelInstance(model, 0*1+0.5f, 0.5f, 0*1+0.5f));
		
		UtilAR.imDrawBackground(image);
		
		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();
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
