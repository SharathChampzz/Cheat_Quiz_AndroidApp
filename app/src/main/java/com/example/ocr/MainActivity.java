package com.example.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    Button btn , select , iknow , search , ggl;
    EditText txt , ans;
    Bitmap bitmap;
    public static final int PICK_IMAGE = 1;
    private  String qn;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(StoreValues.getRoomid());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.viewImage);
        btn = findViewById(R.id.result);
        txt = findViewById(R.id.textt);
        ans = findViewById(R.id.answer);
        select = findViewById(R.id.select);
        iknow = findViewById(R.id.ikr);
        search = findViewById(R.id.search);
        ggl = findViewById(R.id.google);

        ggl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String escapedQuery = null;
                try {
                    escapedQuery = URLEncoder.encode(txt.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
                //Intent intent = new Intent(MainActivity.this , LoadUrl.class);
                //intent.putExtra("url",uri.toString());
                //startActivity(intent);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectImage();
                btn.setVisibility(View.INVISIBLE);
                txt.setVisibility(View.INVISIBLE);
                ans.setVisibility(View.INVISIBLE);
                iknow.setVisibility(View.INVISIBLE);
                search.setVisibility(View.INVISIBLE);
                ggl.setVisibility(View.INVISIBLE);
                loadImage();

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setVisibility(View.INVISIBLE);
                txt.setVisibility(View.VISIBLE);
                ans.setVisibility(View.VISIBLE);
                iknow.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
                ggl.setVisibility(View.VISIBLE);
                getTextFromImage(view);
            }
        });

        iknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ans.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "If You want to upload your answer Please fill answer Section!", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(MainActivity.this, "Great! Thank you for helping your friends!! Live Long!", Toast.LENGTH_SHORT).show();
                    /// Uploading code
                    String id = databaseReference.push().getKey();
                    Upload upload = new Upload(txt.getText().toString() , ans.getText().toString());
                    assert id != null;
                    databaseReference.child(id).setValue(upload)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   // Toast.makeText(MainActivity.this, "Gracias!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            })
                    ;
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Let me see... Anybody Answered this Question!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this , ResultAcitvity.class);
                qn = txt.getText().toString();
                intent.putExtra("question", qn);
                startActivity(intent);
            }
        });

    }

    private void loadImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                //.setAspectRatio(1,1)
                .start(this);
    }


    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    protected final void onActivityResult(final int requestCode, final int
            resultCode, final Intent i) {
        super.onActivityResult(requestCode, resultCode, i);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(i);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img.setImageBitmap(bitmap);
                btn.setVisibility(View.VISIBLE);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();
                Toast.makeText(this, "Some Exceptions Found!", Toast.LENGTH_SHORT).show();
            }
        }



    }
    public void getTextFromImage(View v){
               TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()){
            Toast.makeText(this, "Text Recogniser is Not Operating", Toast.LENGTH_SHORT).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            StringBuilder sb = new StringBuilder();
            for(int i=0;i<items.size();i++){
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue().replaceAll("\n"," "));
                //sb.append("\n");
            }
            txt.setText(sb.toString());
        }

    }
}
