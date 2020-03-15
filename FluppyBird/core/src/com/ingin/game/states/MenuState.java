package com.ingin.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ingin.game.FluppyBird;

public class MenuState extends State{

    private Texture background;
    private Texture playBtn;

    public MenuState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, FluppyBird.WIDTH/2, FluppyBird.HEIGHT/2);
        background = new Texture("bg.png");
        playBtn = new Texture("playBtn.png");
        initializeQTable();
        initializeNumerOfTimes();
    }

    //passer ned ein qTabell herifrå
    private double[][][][] qTable = new double[51][81][41][2];
    private double[][][][] numerOfTimes = new double[51][81][41][2];


    public void initializeQTable (){
        //qTable[50][30][1] = 20;
        for (int i = 0; i<=50; i+=1){
            for (int j = 0; j<=80; j+=1){
                for (int k = 0; k<=1; k++){
                    for (int l = 0; l<=40; l++){
                        qTable[i][j][l][k] = 0;
                    }

                }
            }
        }
    }

    public void initializeNumerOfTimes (){
        //qTable[50][30][1] = 20;
        //qTable[50][30][1] = 20;
        for (int i = 0; i<=50; i+=1){
            for (int j = 0; j<=80; j+=1){
                for (int k = 0; k<=1; k++){
                    for (int l = 0; l<=40; l++){
                        qTable[i][j][l][k] = 0;
                    }

                }
            }
        }
    }

    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            gsm.set(new PlayState(gsm, qTable, numerOfTimes));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0,0);
        sb.draw(playBtn, cam.position.x - playBtn.getWidth()/2, cam.position.y);
        sb.end();
    }

    public void dispose() {
        background.dispose();
        playBtn.dispose();
        System.out.println("Menu State Disposed");
    }
}
