package com.example.ocr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnteryActivity extends AppCompatActivity {

    Button report , enter , showall, singbtn;
    EditText roomid , singqn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entery);

        report = findViewById(R.id.report);
        enter = findViewById(R.id.enter);
        roomid = findViewById(R.id.roomid);
        showall = findViewById(R.id.showall);
        singqn = findViewById(R.id.single);
        singbtn = findViewById(R.id.singlebtn);



        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roomid.getText().toString().equals("")){
                    Toast.makeText(EnteryActivity.this, "Please Enter ROOM ID!", Toast.LENGTH_SHORT).show();
                }
                else{
                    StoreValues.setRoomid(roomid.getText().toString());
                    startActivity(new Intent(EnteryActivity.this , MainActivity.class));
                }

            }
        });

        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roomid.getText().toString().equals("")){
                    Toast.makeText(EnteryActivity.this, "Please Enter ROOM ID!", Toast.LENGTH_SHORT).show();
                }
                else{
                    StoreValues.setRoomid(roomid.getText().toString());
                    Intent intent = new Intent(EnteryActivity.this , ResultAcitvity.class);
                    intent.putExtra("question", "showall");
                    startActivity(intent);
                }
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={"sharathkumar.mskj.1999@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Regarding Cheat Quiz Application");
                intent.putExtra(Intent.EXTRA_TEXT,"");
                //intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
                //Toast.makeText(EnteryActivity.this, "Work in progress!!", Toast.LENGTH_SHORT).show();
            }
        });

        singbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roomid.getText().toString().equals("")){
                    Toast.makeText(EnteryActivity.this, "Please Enter ROOM ID!", Toast.LENGTH_SHORT).show();
                }
                else{
                    StoreValues.setRoomid(roomid.getText().toString());
                    Intent intent = new Intent(EnteryActivity.this , ResultAcitvity.class);
                    intent.putExtra("question", singqn.getText().toString());
                    startActivity(intent);
                }
            }
        });

    }
}
