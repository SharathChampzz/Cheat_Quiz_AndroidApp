package com.example.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultAcitvity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private String[] values;
    private String question;
    ListView listView;
    Button result;
    int flag;   // 1 means all 0 means only questions
    private ArrayAdapter<String> adapter;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(StoreValues.getRoomid());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_acitvity);

        listView = findViewById(R.id.list);

       // linearLayout = findViewById(R.id.linlay);


        Intent inten = getIntent();
        question = inten.getStringExtra("question");
        assert question != null;
        if (question.equals("showall")){
            flag = 1;
        }
        else{
            flag = 0;
        }
        //Toast.makeText(this, question, Toast.LENGTH_SHORT).show();




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                values = new String[size];
                String qn , ans , result;
                int i = 0;
                double similarity;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Upload upload = snapshot.getValue(Upload.class);
                    assert upload != null;
                    qn = upload.getQuestion();
                    ans = upload.getAnswer();
                    result = "Question : " + qn + "\n" + "Answer : " + ans;
                    if (flag == 1){
                        values[i] = result;
                        //Toast.makeText(ResultAcitvity.this, result + ":" + similarity, Toast.LENGTH_SHORT).show();
                        i++;
                    }
                    else{
                        try{
                            similarity = CheckSimilarity(qn , question);
                            if(similarity > 0.45){
                                values[i] = result;
                                //Toast.makeText(ResultAcitvity.this, result + ":" + similarity, Toast.LENGTH_SHORT).show();
                                i++;
                            }
                            else{
                                String doNothing = "do nothing";
                            }
                        }
                        catch(Exception e){
                            Toast.makeText(ResultAcitvity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    /*

                    if (values[0].equals("")){
                        values[0] = "No Match Found Sorry!!";
                    }        */

                    for (int j=i;j<size;j++){
                        values[j] = "";
                    }

                }


                try{

                    update();
                    listView.setAdapter(adapter);
                    Toast.makeText(ResultAcitvity.this, "Available Answer Updated!!", Toast.LENGTH_SHORT).show();

                }
                catch (Exception e){
                    Toast.makeText(ResultAcitvity.this, "Error during Updating : " + e.toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ResultAcitvity.this, "Cancelled..!!!", Toast.LENGTH_SHORT).show();
            }
        });



        //listView.setOnClickListener((View.OnClickListener) this);
    }

    private double CheckSimilarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    private int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    private void update() {

        adapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1  , values);

        /*
        for (int i=0;i<2;i++){
            Toast.makeText(this, values[i], Toast.LENGTH_SHORT).show();
        }

        ListView listView = new ListView(this);
        linearLayout.addView(listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        */
    }
}