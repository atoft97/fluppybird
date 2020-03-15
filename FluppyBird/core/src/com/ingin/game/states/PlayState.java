package com.ingin.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ingin.game.FluppyBird;
import com.ingin.game.sprites.Bird;
import com.ingin.game.sprites.Tube;

import org.omg.CORBA.PUBLIC_MEMBER;

public class PlayState extends State {

    private static final int TUBE_SPACING = 125;
    private static final int TUBE_COUNT = 4;
    private static final int GROUND_Y_OFFSET = -50;

    private Bird bird;
    private Texture bg;
    private Texture ground;
    private Vector2 groundPos1, groundPos2;

    private Array<Tube> tubes;

    private int tubeCounter = 0;

    /////////////////////////////////
    private double learningRate = 0.2;
    private double discount = 0.99;

    private double[][][][] qTable;
    private double[][][][] numerOfTimes;

    public static int deaths = 0;
    public static int hopp = 0;

    public static int maxTube = 0;

    private float oldY = 300;




    ////////////////////////////////


    //change void
    public int[] getDescreeteState(){
        float lengdeX = (tubes.get(tubeCounter % TUBE_COUNT).getPosBotTube().x - bird.getPosition().x);
        float lengdeY = bird.getPosition().y - (tubes.get(tubeCounter % TUBE_COUNT).getPosBotTube().y + tubes.get(tubeCounter % TUBE_COUNT).getBottomTube().getHeight());

        int[] state = new int[2];
        int lengdeXdisc = (int) Math.round(lengdeX/10);
        int lengdeYdisc = (int) Math.round((lengdeY + 400) / 10);
        state[0] = lengdeXdisc;
        state[1] = lengdeYdisc;

        //System.out.println(lengdeXdisc + ","+ lengdeYdisc);

        return (state);
    }

    public PlayState(GameStateManager gsm, double[][][][] qTable,double[][][][] numerOfTimes) {

        super(gsm);
        bird = new Bird(50,300);
        cam.setToOrtho(false, FluppyBird.WIDTH/2, FluppyBird.HEIGHT/2);
        bg = new Texture("bg.png");
        ground = new Texture("ground.png");
        groundPos1 = new Vector2(cam.position.x - cam.viewportWidth/2, GROUND_Y_OFFSET);
        groundPos2 = new Vector2((cam.position.x - cam.viewportWidth/2)  + ground.getWidth(), GROUND_Y_OFFSET);

        tubes = new Array<Tube>();
        for (int i=1; i <= TUBE_COUNT; i++){
            tubes.add(new Tube(i * (TUBE_SPACING + Tube.TUBE_WIDTH)));
        }

        this.qTable = qTable;
        this.numerOfTimes = numerOfTimes;

        System.out.println("============================================");
        System.out.println("Deaths: " + deaths);
        System.out.println("Beste: " + maxTube);
        //System.out.println(qTable[30][30][0]);

        System.out.println("Zeros: " + printNumberOfZeros(qTable));
    }



    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            bird.jump();
        }
    }

    public int decideAction(int discX, int discY, int speed){
        //velg høgaste value
        //System.out.println(qTable[discX][discY][1] + "," + qTable[discX][discY][0]);
        /*
        System.out.println("========================================");
        System.out.println("Hopp: " + qTable[discX][discY][speed][1]);
        System.out.println("Chill: " + qTable[discX][discY][speed][1]);

        System.out.println("X: " + discX);
        System.out.println("Y: " + discY);
        System.out.println("Speed: " + speed);
        */

        if (qTable[discX][discY][speed][1] > qTable[discX][discY][speed][0]){

            bird.jump();
            //System.out.println("hopp");
            hopp += 1;
            return (1);
        }
        else {
            return (0);
        }

    }

    public int getDescreeteSpeed(float oldY,float newY){


        float speed = newY -oldY ;
        int discSpeed = Math.round(speed+ 20);
        //System.out.println(discSpeed);
        if (discSpeed > 40){
            return 40;
        }
        if (discSpeed < 0) {
            return 0;
        }
        return discSpeed;
                //(int) Math.round((lengdeY + 400) / 10);
    }

    public void updateQ(int reward, int discXnew, int discYnew, int discX, int discY, int action, int speed, int newSpeed){

        numerOfTimes[discX][discY][speed][action] += 1;

        double maxFutureQ = 0;
        if (qTable[discXnew][discYnew][newSpeed][1] > qTable[discXnew][discYnew][newSpeed][0]){
            maxFutureQ = qTable[discXnew][discYnew][newSpeed][1];
        }
        else{
            maxFutureQ = qTable[discXnew][discYnew][newSpeed][0];
        }
        double currentQ = qTable[discX][discY][speed][action];

        double a = 1/numerOfTimes[discX][discY][speed][action];
        //double newQ = (1-a) * currentQ + a * (reward + discount * maxFutureQ);
        double newQ = (1-learningRate) * currentQ + learningRate * (reward + discount * maxFutureQ);
        qTable[discX][discY][speed][action] = newQ;


    }

    public double printNumberOfZeros (double [][][][] qTable){
        int zeros = 0;
        for (int i = 0; i<=50; i+=1){
            for (int j = 0; j<=80; j+=1){
                for (int k = 0; k<=40; k++){
                    for (int l = 0; l<=1; l++){
                        if (qTable[i][j][k][l] == 0){
                            zeros += 1;
                        }
                    }
                }
            }
        }
        return zeros;
    }

    @Override
    public void update(float dt) {

        //handleInput();
        float oldPosY = bird.getPosition().y;

        int[] discState =  getDescreeteState();
        int discX = discState[0];
        int discY = discState[1];

        int speed = getDescreeteSpeed(oldY, bird.getPosition().y);
        int action = decideAction(discX, discY, speed);
        oldY = bird.getPosition().y;
        updateGround();
        bird.update(dt);
        cam.position.x = bird.getPosition().x + 80;

        int[] newState = getDescreeteState();
        int discXnew = newState[0];
        int discYnew = newState[1];

        int newSpeed = getDescreeteSpeed(oldPosY, bird.getPosition().y);






        for (int i = 0; i < tubes.size; i++){
            Tube tube = tubes.get(i);

            if (cam.position.x - (cam.viewportWidth/2) > tube.getPosTopTube().x + tube.getTopTube().getWidth()){
                tube.reposition(tube.getPosTopTube().x + ((Tube.TUBE_WIDTH + TUBE_SPACING) * TUBE_COUNT));
            }

            if(tube.collides(bird.getBounds())){
                deaths += 1;
                int reward = -1000;
                updateQ(reward, discXnew, discYnew, discX, discY, action, speed, newSpeed);

                //qTable[discX][discY][action] = -1000;
                gsm.set(new PlayState(gsm, qTable, numerOfTimes));
            }
        }
        if(bird.getPosition().y <= ground.getHeight() + GROUND_Y_OFFSET){
            deaths += 1;

            int reward = -1000;
            updateQ(reward, discXnew, discYnew, discX, discY, action, speed, newSpeed);

            //qTable[discX][discY][action] = -1000;
            gsm.set(new PlayState(gsm, qTable, numerOfTimes));


        }
        else if (bird.getPosition().y>400) {

            int reward = -1000;
            updateQ(reward, discXnew, discYnew, discX, discY, action, speed, newSpeed);

            //qTable[discX][discY][action] = -1000;
            deaths += 1;
            gsm.set(new PlayState(gsm, qTable, numerOfTimes));
        }else {
            //if ((tubes.get(tubeCounter % TUBE_COUNT).getPosBotTube().x - bird.getPosition().x) < 0){
            //    reward=10000;
            //}

            int reward = 1;
            updateQ(reward, discXnew, discYnew, discX, discY, action, speed, newSpeed);


            //System.out.println("Hopp" + hopp + "|" + "Deaths" + deaths);

            //System.out.println("=======================================");
            //System.out.println("Høyde over bakken: " + (bird.getPosition().y - ground.getHeight() - GROUND_Y_OFFSET));
            //System.out.println("Lengde til første tube: " + (tubes.get(tubeCounter % TUBE_COUNT).getPosBotTube().x - bird.getPosition().x));
            //System.out.println("Top tube y: " + tubes.get(tubeCounter % TUBE_COUNT).getPosTopTube().y);
            //System.out.println("Bot tube y: " + (tubes.get(tubeCounter % TUBE_COUNT).getPosBotTube().y + tubes.get(tubeCounter % TUBE_COUNT).getBottomTube().getHeight()));
            //System.out.println("currentQ:" + currentQ);
            //System.out.println("newQ:" + newQ);

            if ((tubes.get(tubeCounter % TUBE_COUNT).getPosBotTube().x - bird.getPosition().x) < 0) {
                tubeCounter++;
                System.out.println(tubeCounter);
                if (tubeCounter > maxTube){
                    maxTube = tubeCounter;
                }
            }
        }


        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth/2), 0);
        sb.draw(bird.getTexture(), bird.getPosition().x, bird.getPosition().y);
        for (Tube tube : tubes) {
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBottomTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }

        sb.draw(ground, groundPos1.x, groundPos1.y);
        sb.draw(ground, groundPos2.x, groundPos2.y);
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        ground.dispose();
        for (Tube tube : tubes) {
            tube.dispose();
        }
    }

    public void updateGround() {
        if(cam.position.x - (cam.viewportWidth/2) > groundPos1.x + ground.getWidth()){
            groundPos1.add(ground.getWidth() * 2,0);
        }
        if(cam.position.x - (cam.viewportWidth/2) > groundPos2.x + ground.getWidth()){
            groundPos2.add(ground.getWidth() * 2,0);
        }
    }
}
