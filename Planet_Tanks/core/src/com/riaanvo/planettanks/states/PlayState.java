package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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

/**
 * Created by riaanvo on 9/5/17.
 */

public class PlayState extends State {

    private PerspectiveCamera cam;
    private Model model;
    private ModelInstance modelInstances[];

    private Environment environment;
    private float rotateTimer = 0f;
    private float waitForRotate = 0.01f;
    private float rotationAmountDegrees = 1f;
    private Vector3 up = new Vector3(0,1,0);

    private BitmapFont font;

    private int cornerOffset = 50;

    private float[] rotationRates;

    double currentCamRotation = 0;
    float distanceFromCenter = 10;

    public PlayState(){
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        model = modelBuilder.createBox(1f, 1f, 1f, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        int cubeSize = 10;
        int gridWidth = cubeSize;
        int gridDepth = cubeSize;
        int gridHeight = cubeSize;

        modelInstances = new ModelInstance[gridWidth * gridDepth * gridHeight];

        for(int y = 0; y < gridHeight; y ++){
            for(int z = 0; z < gridDepth; z ++) {
                for (int x = 0; x < gridWidth; x++) {
                    ModelInstance mi = new ModelInstance(model);
                    mi.transform.setTranslation(1.2f * (x - gridWidth / 2), 1.2f * (y - gridHeight / 2), 1.2f * (z - gridDepth / 2));

                    modelInstances[(y * gridWidth * gridDepth) + (z * gridWidth) + x] = mi;
                }
            }
        }


        rotationRates = new float[gridWidth * gridDepth * gridHeight];
        for(int i = 0; i < rotationRates.length; i ++){
            rotationRates[i] = (float)Math.random();
        }

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
    }

    @Override
    public void update(float deltaTime) {
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
            }

            currentCamRotation += 0.02;
            float xPos = (float)Math.cos(currentCamRotation) * distanceFromCenter;
            float zPos = (float)Math.sin(currentCamRotation) * distanceFromCenter;
            cam.position.set(xPos, cam.position.y, zPos);
            cam.lookAt(Vector3.Zero);
            cam.update();
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {

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
    public void dispose() {
        font.dispose();
        model.dispose();
    }
}
