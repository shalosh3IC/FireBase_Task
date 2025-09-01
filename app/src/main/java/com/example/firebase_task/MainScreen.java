package com.example.firebase_task;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainScreen extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    public int code = 0;
    TextView name;
    Button button33;
    ListView LV;
    LinearLayout Note;
    EditText Title, Content;
    Switch importance;
    AlertDialog.Builder adb;
    Notes n;
    ArrayAdapter<String> adp;
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<Notes> value = new ArrayList<Notes>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_screen);
        GetImageFS();
        name.findViewById(R.id.name);
        name.setText(FBref.refAuth.getCurrentUser().getEmail());
        button33 = findViewById(R.id.button33);
        LV = findViewById(R.id.LV);
        adp = new ArrayAdapter<String>(MainScreen.this, android.R.layout.simple_list_item_1, list);
        LV.setAdapter(adp);
        LV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        LV.setOnItemClickListener(this);
        reader();
        button33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                alert(view);
            }
        });
    }
    public void GetImageFS()
    {
        DocumentReference docRef = FBref.RefImages.document("background");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                if(documentSnapshot.exists())
                {
                    com.google.firebase.firestore.Blob blob = (com.google.firebase.firestore.Blob) documentSnapshot.get("imageContent");
                    byte[] bytes = blob.toBytes();
                    if (blob != null)
                    {
                        bytes = blob.toBytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ByteArrayOutputStream B = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, B);
                        String path = MediaStore.Images.Media.insertImage(MainScreen.this.getContentResolver(), bitmap, "Title", null);
                        Uri image = Uri.parse(path);
                        code = 1;
                        BackgroundChange(image);
                    }
                    else
                    {
                        Toast.makeText(MainScreen.this, "Image download failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(MainScreen.this, "Image download failed", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void BackgroundChange(Uri image)
    {
        LinearLayout mainLayout = findViewById(R.id.main);
        if (image != null && mainLayout != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(image);
                Drawable drawable = Drawable.createFromStream(inputStream, image.toString());
                mainLayout.setBackground(drawable);
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (FileNotFoundException e)
            {
                Log.e("MainActivity", "File not found for URI: " + image, e);
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Log.e("MainActivity", "Error setting background from URI: " + image, e);
                Toast.makeText(this, "Error setting background", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if (image == null)
            {
                Log.w("MainActivity", "Image URI is null.");
            }
            if (mainLayout == null)
            {
                Log.w("MainActivity", "mainLayout is null. Ensure it's initialized.");
            }
        }
    }
    public void reader()
    {
        ValueEventListener stuListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                list.clear();
                value.clear();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    String str1 = data.getKey();
                    Notes note = data.getValue(Notes.class);
                    list.add(str1);
                    value.add(note);
                }
                adp.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        FBref.RefNotes.addValueEventListener(stuListener);
    }
    public void reader2(Boolean importance)
    {
        ValueEventListener stuListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                list.clear();
                value.clear();
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    if(data.getValue(Notes.class).isNoteImportance() == importance)
                    {
                        String str1 = data.getKey();
                        Notes note = data.getValue(Notes.class);
                        list.add(str1);
                        value.add(note);
                    }
                }
                adp.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        FBref.RefNotes.addValueEventListener(stuListener);
    }
    public void alert(View view)
    {
        Note = (LinearLayout) getLayoutInflater().inflate(R.layout.note_writer, null);
        Title = Note.findViewById(R.id.editTextText2);
        Content = Note.findViewById(R.id.editTextText3);
        importance = Note.findViewById(R.id.switch1);


        adb = new AlertDialog.Builder(MainScreen.this);

        adb.setView(Note);
        adb.setTitle("Write a note:");
        adb.setPositiveButton("enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                n = new Notes(Title.getText().toString(), Content.getText().toString(), importance.isChecked());
                String key = n.getNoteTitle();
                FBref.RefNotes.child(key).setValue(n);
            }
        });
        adb.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        adb.show();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Notes note = value.get(i);
        AlertDialog.Builder ap = new AlertDialog.Builder(MainScreen.this);
        String str = (String) adapterView.getItemAtPosition(i);
        ap.setTitle(str);
        ap.setMessage(note.getNoteContent());
        ap.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        String str = item.getTitle().toString();
        if(str.equals("All Notes"))
        {
            reader();
        }
        else if(str.equals("Important Notes"))
        {
            reader2(true);
        }
        else if(str.equals("Not Important Notes"))
        {
            reader2(false);
        }
        else if(str.equals("Set BackGround"))
        {
            Intent tttt = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(tttt, 3);
        }
        else if(str.equals("Log Out"))
        {
            Intent tt = new Intent(MainScreen.this, MainActivity.class);
            FBref.refAuth.signOut();
            startActivity(tt);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null)
        {
            if(code == 1)
            {
                DocumentReference docRef = FBref.RefImages.document("background");
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        if(documentSnapshot.exists())
                        {
                            docRef.delete();
                        }
                    }
                });
            }
            Uri image = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] imageBytes = baos.toByteArray();
                if (imageBytes.length > 1040000)
                {
                    int qual = 100;
                    while (imageBytes.length > 1040000)
                    {
                        qual -= 5;
                        baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,qual,baos);
                        imageBytes = baos.toByteArray();
                    }
                }
                Blob blob = Blob.fromBytes(imageBytes);
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("imageName", "background");
                imageMap.put("imageContent", blob);
                FBref.RefImages.document("background").set(imageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainScreen.this, "Operation Success", Toast.LENGTH_SHORT).show();
                        BackgroundChange(image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainScreen.this, "Operation Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Operation Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}