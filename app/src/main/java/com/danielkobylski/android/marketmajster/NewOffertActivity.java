package com.danielkobylski.android.marketmajster;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ButtonFlat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class NewOffertActivity extends AppCompatActivity {

    private static ButtonFlat mPhotoChoiceButton;
    private static TextInputEditText mOffertName;
    private static TextInputEditText mCategory;
    private static TextInputEditText mCity;
    private static TextInputEditText mDescription;
    private static List<Image> mImageList;

    private static final int REQUEST_CODE_PHOTOS = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offert);

        mOffertName = (TextInputEditText)findViewById(R.id.offert_name_edit_text);
        mCategory = (TextInputEditText)findViewById(R.id.category_edit_text);
        mCity = (TextInputEditText)findViewById(R.id.city_edit_text);
        mDescription = (TextInputEditText)findViewById(R.id.description_edit_text);

        mPhotoChoiceButton = (ButtonFlat) findViewById(R.id.photo_choice_button);
        mPhotoChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(pickPhoto , REQUEST_CODE_PHOTOS);//one can be replaced with any action code
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    //mAvatarPreview.setImageURI(selectedImage);
                }

                break;
            case REQUEST_CODE_PHOTOS:
                if(resultCode == RESULT_OK){

                }
                break;
        }
    }

}
