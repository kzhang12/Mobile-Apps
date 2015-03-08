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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class ReviewActivity extends ActionBarActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    protected static FlashdbAdapter dbAdapter;
    TextView textView;
    private CardItem card;
    private int counter;
    private int max;
    private CheckBox randomizeCheckbox;
    private List<CardItem> cards;
    private String subject;
    private boolean emptyDeck;

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        randomizeCheckbox = (CheckBox) findViewById(R.id.random_checkbox);

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

        Intent intent = getIntent();
        subject = intent.getExtras().getString("subject");
        textView = (TextView) findViewById(R.id.review_textView);
        try {
            cards = dbAdapter.getSpecificCards(subject);
            //Toast.makeText(getApplicationContext(), "There are no cards for this subject", Toast.LENGTH_SHORT).show();
        }
        catch(Exception SQLException) {
            throw SQLException;
        }
        max = cards.size() - 1;
        card = cards.get(counter);
        String question = card.getQuestion();
        textView.setText(question);
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
                Toast.makeText(this, "You deleted this card", Toast.LENGTH_SHORT).show();
                String q = card.getQuestion();
                String a = card.getAnswer();
                dbAdapter.removeCard(q);

                //Reset cards after deleting a card
                if (cards.size() == 1) {
                    //nothing left in deck after deleted this card
                    textView.setText("THere are no more Questions in this deck");
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
                    card = cards.get(counter);
                    String question = card.getQuestion();
                    textView.setText(question);
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
        Toast toast = Toast.makeText(getApplicationContext(), "Tapped", Toast.LENGTH_SHORT);
        toast.show();
        String answer = card.getAnswer();
        textView.setText(answer);
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
                        Toast toast = Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT);
                        toast.show();
                        if (counter > 0) {
                            counter--;
                        }
                        else {
                            counter = max;
                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Left", Toast.LENGTH_SHORT);
                        toast.show();
                        if(counter < max) {
                            counter++;
                        }
                        else {
                            counter = 0;
                        }
                    }
                    card = cards.get(counter);
                    String question = card.getQuestion();
                    textView.setText(question);

                }
            }
        return true;
    }
}
