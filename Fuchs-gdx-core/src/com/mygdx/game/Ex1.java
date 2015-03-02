package com.mygdx.game;
import java.io.File;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;

public class Ex1 implements ApplicationListener {
	public Environment environment;
	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public float time;
	public float accumtime;
	public float scaling = 1;
	
	@Override
	public void create() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(1f, 0.5f, 0.75f);
		//cam.up.set(0.418489f,-0.791933f,0.444645f);
		cam.near = 1f;
		cam.far = 1000f;
		cam.update();
				
		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createArrow(0f, 0f, 0f, 1f, 0f, 0f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(model));
		model = modelBuilder.createArrow(0f, 0f, 0f, 0f, 1f, 0f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(model));
		model = modelBuilder.createArrow(0f, 0f, 0f, 0f, 0f, 1f,
				0.1f, 0.2f, 100, 1,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(model));
		
		model = modelBuilder.createBox(1f, 1f, 1f,
		new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
			Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(model, 1f, 0.5f, 0.75f));
		/*
		model = modelBuilder.createBox(0.9f, 0.1f, 0.1f,
			new Material(ColorAttribute.createDiffuse(Color.WHITE)),
			Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(model, 0f, 0f, 0f));
		model = modelBuilder.createCylinder(1f, 1f, 1f,
			200, 1, new Material(ColorAttribute.createDiffuse(Color.PURPLE)),
			Usage.Position | Usage.Normal);
		instances.add(new ModelInstance(model, 0f, 0f, 0f));
		*/
		
		UBJsonReader jsonReader = new UBJsonReader();
		ModelLoader loader = new G3dModelLoader(jsonReader);
		System.out.println(new File("huhu").getAbsolutePath());
		
		
		model = loader.loadModel(new FileHandle(new File("C:\\Users\\Martin\\Desktop\\chess stl\\Pawn.g3db")));
        model.materials.get(0).set(ColorAttribute.createDiffuse(Color.BLACK));
        instances.add(new ModelInstance(model));
        model = loader.loadModel(new FileHandle(new File("assets/Bishop.g3db")));
        instances.add(new ModelInstance(model, 4f, 0f, 0f));
        model = loader.loadModel(new FileHandle(new File("C:\\Users\\Martin\\Desktop\\chess stl\\Horse.g3db")));
        instances.add(new ModelInstance(model, 8f, 0f, 0f));
        model = loader.loadModel(new FileHandle(new File("C:\\Users\\Martin\\Desktop\\chess stl\\Rook.g3db")));
        instances.add(new ModelInstance(model, -4f, 0f, 0f));
        model = loader.loadModel(new FileHandle(new File("C:\\Users\\Martin\\Desktop\\chess stl\\Queen.g3db")));
        instances.add(new ModelInstance(model, -8f, 0f, 0f));
        model = loader.loadModel(new FileHandle(new File("C:\\Users\\Martin\\Desktop\\chess stl\\King.g3db")));
        instances.add(new ModelInstance(model, 0f, 0f, 4f));
	}
	
	@Override
	public void render() {
		time = Gdx.graphics.getDeltaTime();
		accumtime += time;
		cam.rotateAround(new Vector3(1f, 0.5f, 0.75f), new Vector3(0f, 1f, 0f), time*50);
		cam.update();
		/*instances.get(5).transform.translate(0,-(accumtime-4)/200,0);
		instances.get(3).transform.rotate(new Vector3(1f, 1f, 1f), time*300);
		if(accumtime <= 2) {
			instances.get(4).transform.translate(time/scaling,0,0);
		}
		if(accumtime > 2 && accumtime <= 4) {
			instances.get(4).transform.translate(0,0,time/scaling);
		}
		if(accumtime > 4 && accumtime <= 6) {
			instances.get(4).transform.translate(-time/scaling,0,0);
		}
		if(accumtime > 6 && accumtime <= 8) {
			instances.get(4).transform.translate(0,0,-time/scaling);
		}
		if(accumtime > 8) {
			accumtime = 0;
			instances.get(4).transform.setTranslation(0f,0f,0f);
			instances.get(4).transform.scale(1.5f,1.5f,1.5f);
			scaling *= 1.5;
		}*/
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(cam);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}
	
	@Override
	public void dispose() {
		modelBatch.dispose();
		model.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = Gdx.graphics.getWidth();
		cam.viewportHeight = Gdx.graphics.getHeight();
		cam.update();
	}
	
	@Override
	public void pause() {
	}
	
	@Override
	public void resume() {
	}
}