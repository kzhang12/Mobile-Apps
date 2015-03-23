package bkim54kzhang12.flashboard;

import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;


public class ReviewActivity extends ActionBarActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    protected static FlashdbAdapter dbAdapter;
    private TextView q_a_textView;
    private ImageView arrows_ImageView;
    private CardItem card;
    private int counter;
    private int max;
    private int min = 0;
    private List<CardItem> cards;
    private String subject;
    private boolean emptyDeck;
    private boolean random;
    private Random randGen;
    private int randomNum;

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);

        //setup DB
        dbAdapter = new FlashdbAdapter(this);
        dbAdapter.open();
        counter = 0;

        //setup random number bullshit
        randGen = new Random();

        Intent intent = getIntent();
        subject = intent.getExtras().getString(MainActivity.EXTRA_SUBJECT);
        random = intent.getExtras().getBoolean(MainActivity.EXTRA_RANDOM);
        q_a_textView = (TextView) findViewById(R.id.review_textView);
        arrows_ImageView = (ImageView) findViewById(R.id.arrow_imageView);
        try {
            cards = dbAdapter.getSpecificCards(subject);
            //Toast.makeText(getApplicationContext(), "There are no cards for this subject", Toast.LENGTH_SHORT).show();
        }
        catch(Exception SQLException) {
            throw SQLException;
        }
        max = cards.size() - 1;
        if(random) {
            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            randomNum = randGen.nextInt((max - min) + 1) + min;
            counter = randomNum;
        }
        //counter is 0 otherwise.
        card = cards.get(counter);
        String question = card.getQuestion();
        q_a_textView.setText(question);
        emptyDeck = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbAdapter.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_trashcan:
                if(emptyDeck) {
                    Toast.makeText(this, "No more cards left!", Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }
                Toast.makeText(this, "You deleted this card", Toast.LENGTH_SHORT).show();
                String q = card.getQuestion();
                String a = card.getAnswer();
                dbAdapter.removeCard(q);

                //Reset cards after deleting a card
                if (cards.size() == 1) {
                    //nothing left in deck after deleted this card
                    q_a_textView.setText("There are no more questions in this deck");
                    arrows_ImageView.setVisibility(View.INVISIBLE);
                    emptyDeck = true;

                } else {
                    try {
                        cards = dbAdapter.getSpecificCards(subject);
                        //Toast.makeText(getApplicationContext(), "There are no cards for this subject", Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception SQLException) {
                        throw SQLException;
                    }
                    max = cards.size() - 1;
                    card = cards.get(0);
                    String question = card.getQuestion();
                    q_a_textView.setText(question);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        //Toast toast = Toast.makeText(getApplicationContext(), "Tapped", Toast.LENGTH_SHORT);
        //toast.show();
        String question = card.getQuestion();
        String answer = card.getAnswer();
        if (!emptyDeck) {
            if (q_a_textView.getText().toString().equals(question)) {
                q_a_textView.setText(answer);
            } else {
                q_a_textView.setText(question);
            }
        }
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    //see if swiping
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final int SWIPE_THRESHOLD = 100;
        final int SWIPE_VELOCITY_THRESHOLD = 100;

            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (emptyDeck)
                return true;
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        //Toast toast = Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT);
                        //toast.show();
                        if (random) {
                        // nextInt is normally exclusive of the top value,
                        // so add 1 to make it inclusive
                            do {
                                randomNum = randGen.nextInt((max - min) + 1) + min;
                            } while (counter == randomNum); //keep generating until there different
                            counter = randomNum;
                        }
                        else { //else not random
                            if (counter > 0) {
                                counter--;
                            } else {
                                counter = max;
                            }
                        }
                    } else {
                        //Toast toast = Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT);
                        //toast.show();
                        if(counter < max) {
                            counter++;
                        }
                        else {
                            counter = 0;
                        }
                    }
                    //Toast toast = Toast.makeText(getApplicationContext(), counter+":"+randomNum, Toast.LENGTH_SHORT);
                    //toast.show();
                    card = cards.get(counter);
                    String question = card.getQuestion();
                    q_a_textView.setText(question);

                }
            }
        return true;
    }
}
