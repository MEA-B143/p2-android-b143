package com.b143lul.android.logreg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class CircleView extends View {
    JSONObject groupScores;
    Paint paint1;
    public CircleView(Context context) {
        super(context);
        init();
    }
    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void init() {
        paint1 = new Paint();
        paint1.setColor(Color.RED);
        this.setWillNotDraw(false);
    }
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (groupScores != null) {
            for (int i = 0; i < groupScores.names().length(); i++) {
                try {
                    if (!groupScores.names().getString(i).isEmpty()) {
                        // Get all the values to paint the circles.
                        String name = groupScores.names().getString(i);
                        int userscore = 0;
                            userscore = Integer.parseInt(groupScores.getString(name));
                        // Now let's actually draw the stuff.
                        canvas.drawCircle(posX(userscore), posY(userscore), 100, paint1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update(JSONObject allGroupScores) {
        groupScores = allGroupScores;
        invalidate();
    }

    private float posX(int points) {
        int maxPoints = 1000;
        int width = getWidth();

        if (points < maxPoints/5) {                                    //These If-statements divide the maxPoints into 5, as there are 5 sections of the track. This one is the first piece (horizontal).
            return (points/(maxPoints/5))*((width/5)*3);                  //Dividing the points with the amount of points needed for the piece an then multiplying it by where it needs to "fit", makes it fit there. Its multiplied by width/5*3 because there is width/5 on each side of the track.
        } else if (points >= maxPoints/5 && points < (maxPoints/5)*2) {   //This is the first vertical piece.
            return (width/5)*3;                                              //This ensures that the first vertical piece will be width/5 from the right side of the screen.
        } else if (points >= (maxPoints/5)*2 && points < (maxPoints/5)*3) {       //Second horizontal piece.
            return (width/5*3)-((points-(maxPoints/5)*2)/(maxPoints/5))*((width/5)*3);   //This is basically the same as the first piece, but it's modified to go backwards, so at points increase the value gets smaller and smaller.
        } else if (points >= (maxPoints/5)*3 && points < (maxPoints/5)*4) {    //Second vertical piece.
            return 0;                                                        //This is a 0, not really much to say; it's basically because the second vertical piece for some reason aligned itself with the rest. Magic..?
        } else {                                                         //And finally "the rest" which is the last piece (horizontal).
            return ((points-(maxPoints/5)*4)/(maxPoints/5))*((width/5)*3); //This is just the same as the first horizontal piece, except some of it is negated so it doesn't go off screen.
        }
    }

    private float posY(int points) {                                                    //The same applies to the if-statements here, as above.
        int maxPoints = 1000;
        int height = getHeight();

        if (points < maxPoints/5) {
            return height/5;                                    //This ensures that the first horizontal piece is height/5 from the top.
        } else if (points >= maxPoints/5 && points < (maxPoints/5)*2) {
            return ((points/(maxPoints/5))*((height/5)*3)/2)-height/10; //And this makes the first vertical piece go for height/5*3/2, because we have height/5 on the top and bottom, so theres height/5*3 left in the middle, but we have 2 vertical pieces, so its height/5*3/2.
        } else if (points >= (maxPoints/5)*2 && points < (maxPoints/5)*3) {
            return height/2;                                       //Again, this is to make sure the second horizontal piece is in the right plac, which is in the middle.
        } else if (points >= (maxPoints/5)*3 && points < (maxPoints/5)*4) {
            return ((points/(maxPoints/5))*((height/5)*3)/2)-height/5*2;         //This second vertical piece is basically the same as the first, except for the alignment, so it's further down.
        } else {
            return height/5*4;      //And finally this makes the last horizontal piece appear height/5 from the bottom.
        }
    }
}