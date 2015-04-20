package com.fsc.uibmissatgeria.models;

import android.content.Context;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.api.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiscosastre on 11/04/15.
 */
public class ModelsManager {

    private Context ctx;
    private Server server;

    public ModelsManager(Context c) {
        ctx = c;
        server = new Server(ctx);
}

    public ArrayList<Message> getMessages(Subject s, SubjectGroup g) {
        return new ArrayList<>();
    }

    public ArrayList<Subject> getSubjects() {
        List<Subject> subjects = Subject.listAll(Subject.class);
        if (subjects.isEmpty()) {
            initSubjects();
        }
        return (ArrayList<Subject>) Subject.listAll(Subject.class);
    }

    private void initSubjects() {
        Map<String, Object> subjectsData = server.getSubjects();
        boolean existsError = subjectsData.containsKey(Constants.RESULT_ERROR);
        if (existsError) {
            String error_message = (String) subjectsData.get(Constants.RESULT_ERROR);
            Constants.showToast(ctx, error_message);
        }
    }

    public void resetDB() {
        User.deleteAll(User.class);
        //Message.deleteAll(Message.class);
        Subject.deleteAll(Subject.class);
        SubjectGroup.deleteAll(SubjectGroup.class);
    }



}
