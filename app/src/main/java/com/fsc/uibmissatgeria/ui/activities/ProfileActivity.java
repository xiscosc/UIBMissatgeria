package com.fsc.uibmissatgeria.ui.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.managers.ImageManager;
import com.fsc.uibmissatgeria.managers.ModelManager;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.User;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {
    private TextView name;
    private TextView uibdigital;
    private CircleImageView circleImageView;
    private User user;
    private Avatar avatar;
    private Avatar newAvatar;

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
        if (avatar!= null && avatar.hasFile()) circleImageView.setImageBitmap(avatar.getBitmap(this));
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
                ImageManager imageManager = new ImageManager(this);
                newAvatar = imageManager.saveAvatarToStorageGroup(image, user);
                SendMessageTask t = new SendMessageTask(this);
                t.execute();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private class SendMessageTask extends AsyncTask<Void, Void, Avatar> {

        Context ctx;
        ProgressDialog pDialog;

        public SendMessageTask(Context ctx) {
            super();
            this.ctx = ctx;
        }

        @Override
        protected Avatar doInBackground(Void... params) {
            ModelManager modelManager = new ModelManager(ctx);
            return modelManager.updateAvatar(user, newAvatar);
        }

        @Override
        protected void onPostExecute(Avatar response) {
            if (response != null) {
              if (avatar!= null){
                  FileManager.deleteFile(avatar.getLocalPath());
                  avatar.delete();
              }
              avatar = response;
              avatar.save();
              newAvatar = null;
              if (avatar!= null && avatar.hasFile()) circleImageView.setImageBitmap(avatar.getBitmap(ctx));
            } else {
                FileManager.deleteFile(newAvatar.getLocalPath());
                Constants.showToast(ctx, getString(R.string.error_avatar));
            }
            pDialog.hide();

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctx);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(getResources().getString(R.string.avatar_message));
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }
    }

}
