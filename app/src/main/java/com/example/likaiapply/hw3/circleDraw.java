package com.example.likaiapply.hw3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by likaiapply on 2017/2/22.
 */

public class circleDraw extends View implements View.OnTouchListener {

    ArrayList<Circle> circleList=new ArrayList<Circle>();//all circles on screen
    HashMap<Integer,Speed> movement=new HashMap<Integer, Speed>();//all circles are moving
    ArrayList<Integer> newMoveCircle=new ArrayList<Integer>();//index of new added circle in move mode

    private float startX;//Action down X location
    private float startY;//Action down Y location

    private boolean swipeInProgress = false;
    int circleCounter=0;//circle index in circleList
    String modeType="";//include three mode: "DRAW","MOVE" and "DELETE"
    String penColor="BLACK";//set Pen color, default color is "BLACK"
    boolean moving=false;//flag to see if in move mode movement
    float screenWidth;//value acquire from MainActivity
    float screenHeight;

    VelocityTracker velocity;//compute velocity in move mode
    Timer everySecond;//loop task in move mode to keep circle moving

    static Paint blackPen;//pen color
    static Paint redPen;
    static Paint greenPen;
    static Paint bluePen;
    static {
        blackPen = new Paint();
        blackPen.setColor(Color.BLACK);
        blackPen.setStyle(Paint.Style.STROKE);
        blackPen.setStrokeWidth(5.0f);

        redPen=new Paint();
        redPen.setColor(Color.RED);
        redPen.setStyle(Paint.Style.STROKE);
        redPen.setStrokeWidth(5.0f);

        greenPen=new Paint();
        greenPen.setColor(Color.GREEN);
        greenPen.setStyle(Paint.Style.STROKE);
        greenPen.setStrokeWidth(5.0f);

        bluePen=new Paint();
        bluePen.setColor(Color.BLUE);
        bluePen.setStyle(Paint.Style.STROKE);
        bluePen.setStrokeWidth(5.0f);

    }
    //Circle class store circle informations
    public class Circle{
        float centerX;
        float centerY;
        float radius;
        Paint colorPen;
        public Circle(float x,float y, float r, String color){
            centerX=x;
            centerY=y;
            radius=r;
            switch (color){
                case "BLACK":
                    colorPen = blackPen;
                    break;
                case "RED":
                    colorPen=redPen;
                    break;
                case "GREEN":
                    colorPen=greenPen;
                    break;
                case "BLUE":
                    colorPen=bluePen;
                    break;
                default:
                    colorPen=blackPen;
                    break;
            }
        }

        //draw circle
        public void drawOn(Canvas canvas){
            canvas.drawCircle(centerX,centerY,radius,colorPen);

        }

        public boolean isInCircle(float x,float y){
            float r=(float) Math.sqrt((centerX-x)*(centerX-x)+(centerY-y)*(centerY-y));
            if (r<radius)return true;
            return false;
        }
    }

    //store speed information during move mode
    public class Speed{
        //distance between circle center and current location
        float deltX;
        float deltY;

        //speed
        float speedX;
        float speedY;
        public Speed(float x,float y,float sx,float sy){
            deltX=x;
            deltY=y;
            speedX=sx;
            speedY=sy;
        }
    }
    public void addCircle(Circle circle){//add circle to circleList
        circleList.add(circle);

    }
    public void setCircle(int index,Circle circle){//change circle information
        circleList.set(index,circle);

    }
    public void setPenColor(String color){//set pen color
        penColor=color;
        System.out.println(penColor);

    }
    public void setModeType(String mode){//set mode type
        modeType=mode;

    }
    public void getScreenSize(Point screenSize){//acquire Screen size
        screenWidth=screenSize.x;
        screenHeight=screenSize.y;

    }
    //set movement state
    public void setMovementState(boolean move){
        moving=move;
    }
    //set timer
    public void setTimer(Timer timer){
        everySecond=timer;
    }
    //each collision will readuce the speed
    public float speedReduce(float speed){
        return speed*0.7f;
    }

    //add listener to this view
    public circleDraw(Context context) {
        super(context);
        setOnTouchListener(this);
    }
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN: return handleActionDown(event);
            case MotionEvent.ACTION_UP: return handleActionUp(event);
            case MotionEvent.ACTION_MOVE: return handleActionMove(event);
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                swipeInProgress = false;
                return false;
        }
        return false;
    }

    private boolean handleActionDown(MotionEvent event) {
        swipeInProgress = true;
        Log.i("rew","Down");
        startX = event.getX();
        startY = event.getY();

        switch (modeType) {
            case "DRAW":
                this.addCircle(new Circle(startX, startY, 1.0f, penColor));
                movement.clear();

                break;
            case "MOVE":
                everySecond.cancel();
                newMoveCircle.clear();
                for(int i=0;i<circleList.size();i++){
                    if(circleList.get(i).isInCircle(startX,startY)) {
                        if(velocity!=null){
                            velocity.recycle();
                            velocity=null;
                        }
                        if(movement.containsKey(i)){
                            movement.remove(i);
                            movement.put(i,new Speed(startX-circleList.get(i).centerX,startY-circleList.get(i).centerY,0f,0f));
                        }
                        else {
                            movement.put(i, new Speed(startX - circleList.get(i).centerX, startY - circleList.get(i).centerY, 0f, 0f));
                        }
                        newMoveCircle.add(i);
                    }
                }

                break;
            case "DELETE":
                movement.clear();

                break;
            default:
                break;
        }

        return true;
    }

    private boolean handleActionMove(MotionEvent event) {
        swipeInProgress = true;

        float currentX = event.getX();
        float currentY = event.getY();
        invalidate();
        switch (modeType) {
            case "DRAW":
                float radius=(float) Math.sqrt((startX-currentX)*(startX-currentX)+(startY-currentY)*(startY-currentY));
                float minX=Math.min(startX,screenWidth-startX);
                float minY=Math.min(startY,screenHeight-startY-160f);
                float maxRadius=Math.min(minX,minY);

                setCircle(circleCounter, new Circle(startX, startY, Math.min(radius,maxRadius), penColor));
                invalidate();
                break;
            case "MOVE":

                velocity=VelocityTracker.obtain();
                velocity.addMovement(event);
                velocity.computeCurrentVelocity(1000);
                for(int i=0;i<circleList.size();i++) {

                    if (newMoveCircle.contains(i)) {
                        float circleCenterX = currentX - movement.get(i).deltX;
                        float circleCenterY = currentY - movement.get(i).deltY;
                        if (circleCenterX < circleList.get(i).radius || circleCenterX + circleList.get(i).radius > screenWidth)
                            circleCenterX= circleList.get(i).centerX;
                        else {
                            circleList.get(i).centerX = circleCenterX;
                        }

                        if (circleCenterY < circleList.get(i).radius || circleCenterY + circleList.get(i).radius > screenHeight - 160f)
                            circleCenterY = circleList.get(i).centerY;
                        else {
                            circleList.get(i).centerY = circleCenterY;
                        }
                        movement.get(i).speedX = velocity.getXVelocity() /100.0f;
                        movement.get(i).speedY = velocity.getYVelocity() /100.0f;
                    }
                }

                break;
            case "DELETE":
                break;
            default:
                break;
        }

        return true;
    }

    private boolean handleActionUp(MotionEvent event) {
        if (!swipeInProgress) return false;
        Log.i("rew","Up");
        float endX = event.getX();
        float endY = event.getY();
        switch (modeType) {
            case "DRAW":
                startX = endX;
                startY = endY;
                circleCounter++;
                break;
            case "MOVE":

                everySecond=new Timer();
                if(moving) {
                    everySecond.schedule(new TimerTask() {
                        int con=0;
                        @Override
                        public void run() {
                            if(!moving) everySecond.cancel();

                            for (int i = 0; i < circleList.size(); i++) {
                                if (movement.containsKey(i)) {

                                    if (circleList.get(i).centerX+movement.get(i).speedX < circleList.get(i).radius || circleList.get(i).centerX + movement.get(i).speedX+circleList.get(i).radius > screenWidth) {
                                        movement.get(i).speedX = -movement.get(i).speedX;
                                        movement.get(i).speedX=speedReduce(movement.get(i).speedX);
                                        movement.get(i).speedY=speedReduce(movement.get(i).speedY);
                                    }
                                    if (circleList.get(i).centerY +movement.get(i).speedY< circleList.get(i).radius || circleList.get(i).centerY+movement.get(i).speedY + circleList.get(i).radius > screenHeight-160) {
                                        movement.get(i).speedY = -movement.get(i).speedY;
                                        movement.get(i).speedX=speedReduce(movement.get(i).speedX);
                                        movement.get(i).speedY=speedReduce(movement.get(i).speedY);

                                    }
                                    circleList.get(i).centerX = circleList.get(i).centerX + movement.get(i).speedX;
                                    circleList.get(i).centerY = circleList.get(i).centerY + movement.get(i).speedY;
                                }

                            }

                        }
                    },100,100);
                }else{
                    everySecond=new Timer();
                }

                break;
            case "DELETE":
                int circleIndex=0;
                while(circleIndex<circleList.size()) {
                    if(circleList.get(circleIndex).isInCircle(startX,startY)&&circleList.get(circleIndex).isInCircle(endX,endY)) {
                        circleList.remove(circleIndex);
                        circleIndex--;
                        circleCounter--;
                    }
                    circleIndex++;
                }
                invalidate();
                break;
            default:
                break;
        }
        swipeInProgress = false;
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (Circle each : circleList)
            each.drawOn(canvas);

        if(moving) invalidate();
    }

}
