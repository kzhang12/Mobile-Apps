package bkim54kzhang12.flashboard;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Button reviewButton;
    private Button insertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        insertButton = (Button) findViewById(R.id.insertButton);
        //When first created load the review fragment and disable the review button so cant click
        reviewButton.setEnabled(false);
        insertButton.setEnabled(true);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        ReviewFragment reviewFrag = new ReviewFragment();
        fragmentTransaction.add(R.id.fragment_container, reviewFrag);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchReviewFragment(View view) {
        Toast toast = Toast.makeText(getApplicationContext(), "Review", Toast.LENGTH_SHORT);
        toast.show();
        reviewButton.setEnabled(false);
        insertButton.setEnabled(true);
        reviewButton.setText(R.string.review_u);
        insertButton.setText(R.string.insert_cards);
        reviewButton.setBackgroundColor(getResources().getColor(R.color.button_enabled));
        insertButton.setBackgroundColor(getResources().getColor(R.color.button_disabled));
        fragmentManager.popBackStack();
    }

    public void launchInsertFragment(View view) {
        Toast toast = Toast.makeText(getApplicationContext(), "Insert", Toast.LENGTH_SHORT);
        toast.show();
        reviewButton.setEnabled(true);
        insertButton.setEnabled(false);
        reviewButton.setText(R.string.review);
        insertButton.setText(R.string.insert_cards_u);
        reviewButton.setBackgroundColor(getResources().getColor(R.color.button_disabled));
        insertButton.setBackgroundColor(getResources().getColor(R.color.button_enabled));
        // Create new fragment and transaction
        InsertFragment insertFragment = new InsertFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, insertFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    public static class ReviewFragment extends Fragment {
        private Spinner subjectSpinner;
        private View myFragmentView;
        private Button startButton;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout resource that'll be returned
            myFragmentView = inflater.inflate(R.layout.fragment_review, container, false);
            subjectSpinner = (Spinner) myFragmentView.findViewById(R.id.review_subject_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                    R.array.subjects_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            subjectSpinner.setAdapter(adapter);

            startButton = (Button) myFragmentView.findViewById(R.id.start_button);
            startButton.setOnClickListener(startListener);
            return myFragmentView;
        }

        private View.OnClickListener startListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };


        @Override
        public void onResume() {
            super.onResume();
        }

    }

    public static class InsertFragment extends Fragment {
        private Spinner subjectSpinner;
        private View myFragmentView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout resource that'll be returned
            myFragmentView =  inflater.inflate(R.layout.fragment_insert, container, false);
            subjectSpinner = (Spinner) myFragmentView.findViewById(R.id.insert_subject_spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                    R.array.subjects_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            subjectSpinner.setAdapter(adapter);
            return myFragmentView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }
    }

}
