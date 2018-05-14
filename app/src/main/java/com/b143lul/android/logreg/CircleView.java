package com.b143lul.android.logreg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import static com.b143lul.android.logreg.Login.SHARED_PREF_NAME;

public class CircleView extends View {
    SharedPreferences sharedPreferences;
    JSONObject groupScores;
    Paint paint1;
    String localUsername = "";
    public CircleView(Context context) {
        super(context);
        init(context);
    }
    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public void init(Context context) {
        paint1 = new Paint();
        paint1.setColor(Color.BLUE);
        setWillNotDraw(false);
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }
    protected void onDraw(Canvas canvas) {
        if (groupScores != null) {
            for (int i = 0; i < groupScores.names().length(); i++) {
                try {
                    if (!groupScores.names().getString(i).isEmpty()) {
                        int userscore = 0;
                        // This plan died so this if statement don't work RN LOL who gives af tho
                        if (groupScores.names().getString(i).equals("yourscore")) {
                            paint1.setColor(Color.RED);
                            userscore = Integer.parseInt(groupScores.getString(groupScores.names().getString(i)).split(",")[1]);
                        } else {
                            paint1.setColor(Color.BLUE);
                            // Get all the values that aren't yours to paint the circles.
                            String name = groupScores.names().getString(i);
                            userscore = Integer.parseInt(groupScores.getString(name));
                            if (!name.equals(localUsername)) {
                                // Now let's actually draw the stuff.
                                paint1.setTextAlign(Paint.Align.CENTER);
                                paint1.setTextSize(40);
                                canvas.drawCircle(getWidth() / 5 + posX(userscore), posY(userscore), 50, paint1);
                                canvas.drawText(name, getWidth()/5 + posX(userscore), posY(userscore)+100, paint1);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                int yourScore = groupScores.getInt(localUsername);
                paint1.setColor(Color.RED);
                paint1.setTextAlign(Paint.Align.CENTER);
                canvas.drawCircle(getWidth() / 5 + posX(yourScore), posY(yourScore), 50, paint1);
                canvas.drawText(localUsername, getWidth()/5 + posX(yourScore), posY(yourScore)+100, paint1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(JSONObject allGroupScores, String username) {
        groupScores = allGroupScores;
        localUsername = username;
        invalidate();
        // Invalidate refreshes the draw function
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }

    private float posX(float points) {
        // GOOD SHIT DANIEL :ok_hand:
        float maxPoints = 10000;
        float width = getWidth();

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

    private float posY(float points) {                                                    //The same applies to the if-statements here, as above.
        // GOOD SHIT DANIEL :ok_hand:
        float maxPoints = 10000;
        float height = (float) (getHeight()*0.75);
        float startHeight = height/16;
        if (points < maxPoints/5) {
            return height/5+startHeight;                                    //This ensures that the first horizontal piece is height/5 from the top.
        } else if (points >= maxPoints/5 && points < (maxPoints/5)*2) {
            return ((points/(maxPoints/5))*((height/5)*3)/2)-height/10+startHeight; //And this makes the first vertical piece go for height/5*3/2, because we have height/5 on the top and bottom, so theres height/5*3 left in the middle, but we have 2 vertical pieces, so its height/5*3/2.
        } else if (points >= (maxPoints/5)*2 && points < (maxPoints/5)*3) {
            return height/2+startHeight;                                       //Again, this is to make sure the second horizontal piece is in the right plac, which is in the middle.
        } else if (points >= (maxPoints/5)*3 && points < (maxPoints/5)*4) {
            return ((points/(maxPoints/5))*((height/5)*3)/2)-height/5*2+startHeight;         //This second vertical piece is basically the same as the first, except for the alignment, so it's further down.
        } else {
            return height/5*4+startHeight;      //And finally this makes the last horizontal piece appear height/5 from the bottom.
        }
    }
}