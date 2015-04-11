package com.fsc.uibmissatgeria.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.adapters.ConversationAdapter;


public class ConversationsFragment extends Fragment {

    ConversationAdapter adapterConversations;

    public ConversationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listchats, container, false);

        Conversation[] conversations = new Conversation[] {
               new Conversation(19),
               new Conversation(43),
                new Conversation(22),
                new Conversation(119),
                new Conversation(4),
                new Conversation(55),


        };


        adapterConversations = new ConversationAdapter(getActivity(), conversations);

        ListView listView = (ListView) rootView.findViewById(R.id.list_item_c);
        listView.setAdapter(adapterConversations);

        return rootView;
    }

}