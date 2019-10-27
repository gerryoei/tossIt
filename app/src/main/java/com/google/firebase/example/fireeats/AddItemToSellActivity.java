package com.google.firebase.example.fireeats;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.example.fireeats.model.TossItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddItemToSellActivity extends AppCompatActivity
{
    private FirebaseFirestore mFirestore;

    DatePickerDialog picker;
    Spinner spinner1;
    EditText mItemName;
    ImageView itemImage;
    Button uploadImage;
    EditText mItemDescription;
    AutoCompleteTextView mItemAddress;
    EditText mItemPrice;
    EditText mItemPriceEnd;
    EditText mExpiredDate;

    Bitmap bitmap_photo;
    String currentPhotoPath = "";
    String photoFileName = "";
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_to_sell);

        String[] arr = {
                "1000 Northside Dr NW, Atlanta, GA, 30318",
                "455 14th St NW, Atlanta, GA 30318",
                "801 Atlantic Dr, Atlanta, GA 30332",
                "266 Ferst Dr NW, Atlanta, GA 30332",
                "3311 Flowers Rd S, Atlanta, GA 30341"};

        mItemName = (EditText)findViewById(R.id.sellItem_form_name);
        mItemPrice = (EditText)findViewById(R.id.sellItem_form_price);
        mItemDescription = (EditText)findViewById(R.id.sellItem_form_description);
        mItemAddress = (AutoCompleteTextView) findViewById(R.id.sellItem_form_address);
        mExpiredDate = (EditText)findViewById(R.id.expiredDate);
        mExpiredDate.setInputType(InputType.TYPE_NULL);
        itemImage = (ImageView)findViewById(R.id.sellItem_imageUpload);
        uploadImage = (Button)findViewById(R.id.button_imageUpload);
        mItemPriceEnd = (EditText)findViewById(R.id.sellItem_form_priceEnd);
        spinner1 = (Spinner)findViewById(R.id.spinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, arr);

        mItemAddress.setThreshold(2);
        mItemAddress.setAdapter(adapter);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mExpiredDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddItemToSellActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mExpiredDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        initFirestore();


        findViewById(R.id.sellItem_form_submit).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onSubmitClicked();
            }
        });
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                galleryAddPic();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            Uri uri = Uri.fromFile(file);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            itemImage.setImageBitmap(bitmap);
            bitmap_photo = bitmap;
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        photoFileName = imageFileName;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,35, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void onSubmitClicked(){
        String itemName = mItemName.getText().toString();
        String itemAddress = mItemAddress.getText().toString();
        String itemCategory = String.valueOf(spinner1.getSelectedItem());
        String itemPhoto = BitMapToString(bitmap_photo);
        Log.w("Image", itemPhoto);
        //String itemPhoto = "hahaha";
        //Get start date which is current date
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String startDate = dateFormat.format(date);
        String endDate = mExpiredDate.getText().toString();
        long startPrice = Long.parseLong(mItemPrice.getText().toString());
        long endPrice = Long.parseLong(mItemPriceEnd.getText().toString());

        String itemDescription = mItemName.getText().toString();

        TossItem newItem = new TossItem(itemName,
                                        itemAddress,
                                        itemDescription,
                                        itemCategory,
                                        itemPhoto,
                                        startDate,
                                        startDate,
                                        endDate,
                                        startPrice,
                                        startPrice,
                                        endPrice);
        // Get a reference to the tossitem collection
        CollectionReference tossitem = mFirestore.collection("testitem");
        tossitem.add(newItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.w("ADDED", "ADDED");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("FAILED ADD", "Error adding event document", e);
            }
        });
    }
}
