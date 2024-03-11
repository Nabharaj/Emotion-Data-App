package com.example.emotiondetection2022_23;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private EditText editTextName, editTextAge, editTextGender;
    private RadioGroup[] radioGroups;
    private String[] questionKeys = {"question1", "question2", "question3", "question4",
            "question5", "question6", "question7", "question8", "question9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("user_answers");

        // Find views for name, age, and gender
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextGender = findViewById(R.id.editTextGender);

        // Find the views for RadioGroups
        radioGroups = new RadioGroup[9];
        radioGroups[0] = findViewById(R.id.radioGroupQuestion1);
        radioGroups[1] = findViewById(R.id.radioGroupQuestion2);
        radioGroups[2] = findViewById(R.id.radioGroupQuestion3);
        radioGroups[3] = findViewById(R.id.radioGroupQuestion4);
        radioGroups[4] = findViewById(R.id.radioGroupQuestion5);
        radioGroups[5] = findViewById(R.id.radioGroupQuestion6);
        radioGroups[6] = findViewById(R.id.radioGroupQuestion7);
        radioGroups[7] = findViewById(R.id.radioGroupQuestion8);
        radioGroups[8] = findViewById(R.id.radioGroupQuestion9);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswersToFirebase();
            }
        });
    }

    private void saveAnswersToFirebase() {
        String userName = editTextName.getText().toString().trim();
        String userAge = editTextAge.getText().toString().trim();
        String userGender = editTextGender.getText().toString().trim();

        if (userName.isEmpty() || userAge.isEmpty() || userGender.isEmpty()) {
            Toast.makeText(this, "Please enter your name, age, and gender.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use name, age, and gender as the user ID
        String userId = userName + "_" + userAge + "_" + userGender;

        // Save the user information to Firebase Realtime Database
        DatabaseReference userRef = databaseRef.child(userId);
        userRef.child("name").setValue(userName);
        userRef.child("age").setValue(userAge);
        userRef.child("gender").setValue(userGender);

        // Initialize an array to store the numeric values for each question's answers
        int[] questionValues = new int[questionKeys.length];

        // Save the answers to questions to respective child nodes and calculate numeric values
        for (int i = 0; i < radioGroups.length; i++) {
            String answer = getSelectedAnswer(radioGroups[i]);
            int numericValue = calculateNumericValue(answer);
            questionValues[i] = numericValue;
            userRef.child(questionKeys[i]).setValue(answer);
            userRef.child(questionKeys[i] + "_numeric").setValue(numericValue);
        }

        // Calculate the total score
        int totalScore = 0;
        for (int value : questionValues) {
            totalScore += value;
        }

        // Save the total score to Firebase
        userRef.child("DepressionSeverity").child("total").setValue(totalScore);

        Toast.makeText(this, "Answers saved to Firebase", Toast.LENGTH_SHORT).show();
    }

    private int calculateNumericValue(String answer) {
        // Define the mapping of answers to numeric values
        switch (answer) {
            case "No":
                return 0;
            case "Several days":
                return 1;
            case "More than half the days":
                return 2;
            case "Everyday":
                return 3;
            default:
                return 0; // If the answer is not recognized, default to 0
        }
    }

    private String getSelectedAnswer(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        if (radioButton != null) {
            return radioButton.getText().toString();
        } else {
            return "";
        }
    }
}
