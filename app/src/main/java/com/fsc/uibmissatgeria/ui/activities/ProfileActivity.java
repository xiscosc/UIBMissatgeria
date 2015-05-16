package com.fsc.uibmissatgeria.ui.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.managers.ImageManager;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.User;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {
    private TextView name;
    private TextView uibdigital;
    private CircleImageView circleImageView;
    private User user;
    private Avatar avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = (TextView) findViewById(R.id.profile_user_name);
        uibdigital = (TextView) findViewById(R.id.profile_uibdigital);
        circleImageView = (CircleImageView) findViewById(R.id.user_avatar);
        AccountUIB accountUIB = new AccountUIB(this);
        user = accountUIB.getUser();
        avatar = user.getAvatar();
        if(avatar!= null) circleImageView.setImageBitmap(avatar.getBitmap(this));
        setProfile();
    }

    private void setProfile() {
        if (user!=null) {
            name.setText(user.getName());
            uibdigital.setText(user.getUibDigitalUser());
        }

    }

    public void changeImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Uri image = data.getData();
                if (avatar == null) {
                    avatar = new Avatar();
                    avatar.createAvatarFromIntent(image, user, this);
                    avatar.save();
                } else {
                    avatar.updateAvatarFromIntent(image, this);
                    avatar.save();
                }
                circleImageView.setImageBitmap(avatar.getBitmap(this));
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
