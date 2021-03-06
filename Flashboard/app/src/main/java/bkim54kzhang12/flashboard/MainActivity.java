package bkim54kzhang12.flashboard;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public final static String EXTRA_RANDOM = "bkim54kzhang12.flashboard.RANDOM";
    public final static String EXTRA_SUBJECT = "bkim54kzhang12.flashboard.SUBJECT";

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Button reviewButton;
    private Button insertButton;

    protected static FlashdbAdapter dbAdapter;

    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        insertButton = (Button) findViewById(R.id.insertButton);

        Context context = getApplicationContext();
        myPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        //setup DB
        dbAdapter = new FlashdbAdapter(this);
        dbAdapter.open();

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
    public void onDestroy() {
        super.onDestroy();
        dbAdapter.close();
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
        reviewButton.setEnabled(false);
        insertButton.setEnabled(true);
        reviewButton.setText(R.string.review_u);
        insertButton.setText(R.string.insert_cards);
        reviewButton.setBackgroundColor(getResources().getColor(R.color.button_enabled));
        insertButton.setBackgroundColor(getResources().getColor(R.color.button_disabled));
        fragmentManager.popBackStack();
    }

    public void launchInsertFragment(View view) {
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
        private TextView selectSubjectTextView;
        private TextView noSubjectTextView;
        private View myFragmentView;
        private ImageButton startButton;
        private ArrayList<String> subjects;
        private CheckBox randomCheckBox;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout resource that'll be returned
            myFragmentView = inflater.inflate(R.layout.fragment_review, container, false);
            subjectSpinner = (Spinner) myFragmentView.findViewById(R.id.review_subject_spinner);
            selectSubjectTextView = (TextView) myFragmentView.findViewById(R.id.select_subject_textview);
            noSubjectTextView =  (TextView) myFragmentView.findViewById(R.id.no_subject_review_textview);
            randomCheckBox = (CheckBox) myFragmentView.findViewById(R.id.random_checkbox);
//            // Create an ArrayAdapter using the string array and a default spinner layout
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
//                    R.array.subjects_array, android.R.layout.simple_spinner_item);

            //Create ArrayAdapter
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this.getActivity(),
                    android.R.layout.simple_spinner_item);

            subjects = (ArrayList<String>)dbAdapter.getSubjects();
            if (subjects.size()==0) {
                subjectSpinner.setVisibility(View.INVISIBLE);
                selectSubjectTextView.setVisibility(View.INVISIBLE);
                noSubjectTextView.setText("No Subjects, Please add a card!");
                noSubjectTextView.setVisibility(View.VISIBLE);
            } else {
                selectSubjectTextView.setVisibility(View.VISIBLE);
                selectSubjectTextView.setText("Subject:");
                subjectSpinner.setVisibility(View.VISIBLE);
                noSubjectTextView.setVisibility(View.INVISIBLE);
                for(int i = 0; i < subjects.size(); i++) {
                    adapter.add(subjects.get(i));
                }
            }
            //subjectSpinner.setSelection(0);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            subjectSpinner.setAdapter(adapter);

            startButton = (ImageButton) myFragmentView.findViewById(R.id.start_button);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dbAdapter.isEmpty()) {
                        Toast toast = Toast.makeText(getActivity().getApplication(), "Please insert at least 1 card", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {

                        Intent intent = new Intent(getActivity(), ReviewActivity.class);
                        intent.putExtra(EXTRA_SUBJECT, subjectSpinner.getSelectedItem().toString());
                        intent.putExtra(EXTRA_RANDOM, randomCheckBox.isChecked());

                        startActivity(intent);
                    }
                }
            });
            return myFragmentView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

    }

    public static class InsertFragment extends Fragment {
        private Spinner subjectSpinner;
        private TextView noSubjectTextView;
        private View myFragmentView;
        private EditText questionEditText;
        private EditText answerEditText;
        private ImageButton insertButton;
        private Button subjectButton;
        //private View subjectView;
        private EditText subjectEditText;
        private ArrayList<String> subjects;
        ArrayAdapter<CharSequence> adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout resource that'll be returned
            myFragmentView =  inflater.inflate(R.layout.fragment_insert, container, false);
            //View subjectView =  inflater.inflate(R.layout.subject_alert, container, false);
            subjectSpinner = (Spinner) myFragmentView.findViewById(R.id.insert_subject_spinner);
            noSubjectTextView =  (TextView) myFragmentView.findViewById(R.id.no_subject_insert_textview);
//            // Create an ArrayAdapter using the string array and a default spinner layout
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
//                    R.array.subjects_array, android.R.layout.simple_spinner_item);

            //Create ArrayAdapter
            adapter = new ArrayAdapter<CharSequence>(this.getActivity(),
                    android.R.layout.simple_spinner_item);

            subjects = (ArrayList<String>)dbAdapter.getSubjects();
            if (subjects.size()==0) {
                subjectSpinner.setVisibility(View.INVISIBLE);
                noSubjectTextView.setText("No Subjects, Please add a card!");
                noSubjectTextView.setVisibility(View.VISIBLE);
            } else {
                subjectSpinner.setVisibility(View.VISIBLE);
                noSubjectTextView.setVisibility(View.INVISIBLE);
                for(int i = 0; i < subjects.size(); i++) {
                    adapter.add(subjects.get(i));
                }
            }

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            subjectSpinner.setAdapter(adapter);

            questionEditText = (EditText) myFragmentView.findViewById(R.id.question_editText);
            answerEditText = (EditText) myFragmentView.findViewById(R.id.answer_editText);
            insertButton = (ImageButton) myFragmentView.findViewById(R.id.insert_button);
            subjectButton = (Button) myFragmentView.findViewById(R.id.add_subject_button);

            insertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String question = questionEditText.getText().toString();
                    String answer = answerEditText.getText().toString();
                    Toast toast;
                    if(subjectSpinner.getVisibility() == View.INVISIBLE || question.equals("") || answer.equals("")) {
                        toast = Toast.makeText(getActivity(), "Please Complete Fields", Toast.LENGTH_SHORT);
                    } else {
                        String subject = subjectSpinner.getSelectedItem().toString();
                        dbAdapter.insertCard(new CardItem(subject, question, answer));
                        toast = Toast.makeText(getActivity(), "Added New Card!", Toast.LENGTH_SHORT);
                        questionEditText.setText("");
                        answerEditText.setText("");
                    }
                    toast.show();
                }
            });

            subjectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View subjectView =  inflater.inflate(R.layout.subject_alert, null);
                    subjectEditText= (EditText) subjectView.findViewById(R.id.subject_editText);

                    //Make a builder for the alert dialog
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                    mBuilder.setTitle("Add a new subject");
                    mBuilder.setView(subjectView);
                    //Set the positive button listener for the alert dialog
                    mBuilder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String newSubject = subjectEditText.getText().toString();

                            //TODO: Need to add new subject to the database

                            //TODO: Need to notify spinner adapter that the data set has changed / repopulate the spinner
                            adapter.add(newSubject);
                            if (subjects.size()>=0) {
                                subjectSpinner.setVisibility(View.VISIBLE);
                                noSubjectTextView.setText("");
                                noSubjectTextView.setVisibility(View.INVISIBLE);
                            }
                            //Set subjetspinner to most recently added item!
                            subjectSpinner.setSelection(adapter.getCount());

                        }
                    });
                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //cancel alert
                        }
                    });
                    mBuilder.show();
                }
            });

            return myFragmentView;
        }

        @Override
        public void onResume() {
            super.onResume();
        }
    }

}
