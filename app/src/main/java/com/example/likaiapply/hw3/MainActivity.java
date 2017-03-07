package com.example.likaiapply.hw3;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Timer;

public class MainActivity extends AppCompatActivity{

    View shapes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Point screenSize=new Point();

        shapes=new circleDraw(this);
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        ((circleDraw)shapes).getScreenSize(screenSize);
        setContentView(shapes);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.mode_select, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

            switch (item.getItemId()) {
                case R.id.draw:
                    Log.i("rew", "Draw Mode");
                    ((circleDraw)shapes).setModeType("DRAW");
                    ((circleDraw)shapes).setMovementState(false);
                    break;
                case R.id.move:
                    Log.i("rew", "Move Mode");
                    ((circleDraw)shapes).setModeType("MOVE");
                    ((circleDraw)shapes).setMovementState(true);
                    ((circleDraw)shapes).setTimer(new Timer());
                    break;
                case R.id.delete:
                    Log.i("rew", "Delete Mode");
                    ((circleDraw)shapes).setModeType("DELETE");
                    ((circleDraw)shapes).setMovementState(false);
                    break;
                case R.id.blackpen:
                    ((circleDraw)shapes).setPenColor("BLACK");
                    break;
                case R.id.redpen:
                    ((circleDraw)shapes).setPenColor("RED");
                    break;
                case R.id.greenpen:
                    ((circleDraw)shapes).setPenColor("GREEN");
                    break;
                case R.id.bluepen:
                    ((circleDraw)shapes).setPenColor("BLUE");
                    break;
                default:
                    break;
            }
        return super.onOptionsItemSelected(item);
    }

}
