package com.riaanvo.planettanks;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;

import java.awt.Font;
import java.sql.Time;

public class Main extends ApplicationAdapter {
	private PerspectiveCamera cam;
    private Model model;
    private ModelInstance modelInstances[];
    private ModelBatch modelBatch;

    private Environment environment;
    private float rotateTimer = 0f;
    private float waitForRotate = 0.01f;
    private float rotationAmountDegrees = 1f;
    private Vector3 up = new Vector3(0,1,0);

    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private int cornerOffset = 50;

    private float[] rotationRates;

	@Override
	public void create () {
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        model = modelBuilder.createBox(1f, 1f, 1f, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        int gridWidth = 40;
        int gridDepth = 40;
        modelInstances = new ModelInstance[gridWidth * gridDepth];

        for(int z = 0; z < gridDepth; z ++) {
            for (int x = 0; x < gridDepth; x++) {
                ModelInstance mi = new ModelInstance(model);
                mi.transform.setTranslation(1.2f * (x - gridWidth / 2), 0, 1.2f * (z - gridDepth / 2));
                modelInstances[z * gridWidth + x] = mi;
            }
        }

        rotationRates = new float[gridWidth * gridDepth];
        for(int i = 0; i < rotationRates.length; i ++){
            rotationRates[i] = (float)Math.random();
        }

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        spriteBatch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
	}

	public void update(float deltaTime){
        rotateTimer += deltaTime;
        //System.out.println(rotateTimer);

        if(rotateTimer > waitForRotate){
            rotateTimer = 0;
            for(int i = 0; i < modelInstances.length; i ++){
                if(i % 2 == 0) {
                    modelInstances[i].transform.rotate(up, rotationRates[i] * rotationAmountDegrees);
                } else {
                    modelInstances[i].transform.rotate(up, -rotationRates[i] * rotationAmountDegrees);
                }
                //rotationAmountDegrees *= -1;
            }
        }
    }

	@Override
	public void render () {
		//Gdx.gl.glClearColor(0, 0, 0, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(Gdx.graphics.getDeltaTime());


        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        for(int i = 0; i < modelInstances.length; i ++){
            modelBatch.render(modelInstances[i], environment);
        }
        modelBatch.end();

        spriteBatch.begin();
        font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), cornerOffset * 2, cornerOffset * 2);
        spriteBatch.end();
    }
	
	@Override
	public void dispose () {
        model.dispose();
        modelBatch.dispose();
        spriteBatch.dispose();
        font.dispose();
	}
}
