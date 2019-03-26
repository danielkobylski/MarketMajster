package com.danielkobylski.android.marketmajster;

import android.accounts.Account;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ButtonFlat;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountConfigActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PHOTO = 5;

    private User mUser;
    private CircleImageView mAvatarPreview;
    private ButtonFlat mAvatarButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_config);

        mUser = UserTransfer.mLoggedUser;

        mAvatarPreview = (CircleImageView) findViewById(R.id.user_avatar_edit);
        byte[] avatarContent = mUser.getAvatarImage().getContent();
        Bitmap bmp = BitmapFactory.decodeByteArray(avatarContent, 0, avatarContent.length);
        mAvatarPreview.setImageBitmap(bmp);

        mAvatarButton  = (ButtonFlat) findViewById(R.id.avatar_choice_button);
        mAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_CODE_PHOTO);//one can be replaced with any action code
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    mAvatarPreview.setImageURI(selectedImage);
                }

                break;
            case REQUEST_CODE_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String imagePath = selectedImage.getPath();
                    String imageExt = imagePath.substring(imagePath.lastIndexOf(".")+1);
                    Log.d("obrazek", selectedImage.toString());
                    mAvatarPreview.setImageURI(selectedImage);
                }
                break;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    public void updateAvatarImage(Long userId) {

    }

}
