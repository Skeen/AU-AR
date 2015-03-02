package com.mygdx.game;


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
	private Model pawn;
	private Model rook;
	private Model bishop;
	private Model king;
	private Model queen;
	private Model horse;
	
	private MatOfPoint3f points3d;
	private Size board = new Size(5,7);
	
	private Vector3[][] pos_board = new Vector3[8][8];
	AI ai = new AI();
	
	@Override
	public void create() 
    {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		cap = new VideoCapture();
        //System.out.println(cap.open(0));
       	image = new Mat();

		cap.open(2);
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
		cam.position.set(new Vector3(5f,5f,5f));
		cam.lookAt(0f, 0f, 0f);
		cam.near = 1f;
		cam.far = 500f;
		cam.update();
		
		ModelBuilder modelBuilder = new ModelBuilder();
		pawn = modelBuilder.createBox(0.5f, 0.5f, 0.5f,
				new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
					Usage.Position | Usage.Normal);
		
		rook = modelBuilder.createBox(1f, 1f, 1f,
				new Material(ColorAttribute.createDiffuse(Color.RED)),
				Usage.Position | Usage.Normal);
		
		bishop = modelBuilder.createBox(1f, 1f, 1f,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)),
				Usage.Position | Usage.Normal);
		
		queen = modelBuilder.createBox(1f, 1f, 1f,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				Usage.Position | Usage.Normal);
		
		king = modelBuilder.createBox(1f, 1f, 1f,
				new Material(ColorAttribute.createDiffuse(Color.BLACK)),
				Usage.Position | Usage.Normal);

		horse = modelBuilder.createBox(1f, 1f, 1f,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);

        Point3[] points = new Point3[(int)board.height*(int)board.width];
        for(int i = 0; i < board.height; ++i)
        {
            for(int j = 0; j < board.width; ++j)
            {
            	points[j+(i*(int)board.width)] = new Point3(j, 0.0f, i);
            }
        }
        
        points3d = new MatOfPoint3f(points);
        
        for(int i = 0; i < 8; ++i)
        {
            for(int j = 0; j < 8; ++j)
            {
            	pos_board[i][j] = new Vector3(i, 0.0f, j);
            }
        }
	}

	@Override
	public void render() 
    {
		Array<ModelInstance> instances = new Array<ModelInstance>();
		
		cap.read(image);
		
		MatOfPoint2f corners = new MatOfPoint2f();
		
		boolean patternfound = Calib3d.findChessboardCorners(image,
                board,
                corners);
		
		if(patternfound)
		{
			Calib3d.drawChessboardCorners(image, board, corners, patternfound);

			Mat rvec = new Mat();
			Mat tvec = new Mat();
			Calib3d.solvePnP(points3d, corners, UtilAR.getDefaultIntrinsicMatrix(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()),
				UtilAR.getDefaultDistortionCoefficients(), rvec, tvec);
			
			UtilAR.setCameraByRT(rvec, tvec, cam);
			
			update_ai();
			
			for(int x=0; x<8; ++x)
			{
				for(int y=0; y<8; ++y)
				{
					if(ai.myboard[x][y] == (AI.WHITE | AI.PAWN))
					{
						instances.add(new ModelInstance(pawn, pos_board[x][y]));
					}
					else if(ai.myboard[x][y] == (AI.WHITE | AI.BISHOP))
					{
						instances.add(new ModelInstance(bishop, pos_board[x][y]));
					}
					else if(ai.myboard[x][y] == (AI.WHITE | AI.KNIGHT))
					{
						instances.add(new ModelInstance(horse, pos_board[x][y]));
					}
					else if(ai.myboard[x][y] == (AI.WHITE | AI.ROOK))
					{
						instances.add(new ModelInstance(rook, pos_board[x][y]));
					}
					else if(ai.myboard[x][y] == (AI.WHITE | AI.QUEEN))
					{
						instances.add(new ModelInstance(queen, pos_board[x][y]));
					}
					else if(ai.myboard[x][y] == (AI.WHITE | AI.KING))
					{
						instances.add(new ModelInstance(king, pos_board[x][y]));
					}
				}
			}
		}
                
		//Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		UtilAR.imDrawBackground(image);
		
		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}
	
	public float time = 0;
	public float accumtime = 0;
	
	public void update_ai()
	{
		time = Gdx.graphics.getDeltaTime();
		accumtime += time;
		if(accumtime > 1)
		{
			System.out.println("AI TICK");
			ai.run();
			accumtime = 0;
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
