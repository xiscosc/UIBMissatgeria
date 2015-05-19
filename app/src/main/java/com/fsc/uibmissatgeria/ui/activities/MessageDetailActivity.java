package com.fsc.uibmissatgeria.ui.activities;

import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.api.ServerSettings;
import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.managers.ImageManager;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.FileMessage;
import com.fsc.uibmissatgeria.models.Message;
import com.fsc.uibmissatgeria.models.Subject;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.ui.adapters.FileAdapter;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageDetailActivity extends AppCompatActivity {


    private Toolbar toolbar;

    private ProgressDialog pDialog;
    private List<FileMessage> files;
    private RecyclerView recView;
    private FileAdapter fileAdapter;

    private LinearLayout include;
    private TextView messageUser;
    private TextView messageDate;
    private TextView messageBody;
    private CircleImageView avatarImg;

    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_message);

        Intent i = getIntent();
        message = Message.findById(Message.class, i.getLongExtra(Constants.MESSAGE_OBJ, 0));

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        recView = (RecyclerView) findViewById(R.id.message_detail_list);


        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);

        /*recView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)); */


        include = (LinearLayout) findViewById(R.id.message_detail);

        View myLayout = this.getLayoutInflater.inflate(R.layout.listitem_message, null);

        messageUser = (TextView)include.findViewById(R.id.message_user);
        messageDate = (TextView)include.findViewById(R.id.message_date);
        messageBody = (TextView)include.findViewById(R.id.message_body);
        avatarImg = (CircleImageView)include.findViewById(R.id.user_avatar);


        messageUser.setText(message.getUser().getName());
        messageDate.setText(message.getStringDate());
        messageBody.setText(message.getBody());
        Avatar avatar = message.getUser().getAvatar();
        if (avatar != null && avatar.hasFile()) avatarImg.setImageBitmap(avatar.getBitmap(this));

    }




   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra(Constants.SUBJECT_OBJ, sbj.getId());
                upIntent.putExtra(Constants.GROUP_OBJ, gr.getId());
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.menu_conversation_attach:
                getFile();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

   */

    private void createAdapter() {
        fileAdapter = new FileAdapter(files, this);
        recView.setAdapter(fileAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Uri route = data.getData();
                FileMessage f = null;
                if (FileManager.isImageFromUri(route, this)) {
                    ImageManager imageManager = new ImageManager(this);
                    f = imageManager.saveImageToStorageGroup(route);
                } else {
                    FileManager fileManager = new FileManager(this);
                    f = fileManager.saveFileToStorageGroup(route);
                }
                if (f != null) {
                    files.add(f);
                    createAdapter();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
