package com.fsc.uibmissatgeria.models;

import com.orm.SugarRecord;

/**
 * Created by xiscosastre on 16/5/15.
 */
public class FileMessageConversation extends SugarRecord<FileMessageConversation> {

    private long idApi;
    private String remote_path;
    private String local_path;
    private String mimeType;
    private MessageConversation message;

    public FileMessageConversation(long idApi, String remote_path, String local_path, String mimeType, MessageConversation message) {
        this.idApi = idApi;
        this.remote_path = remote_path;
        this.local_path = local_path;
        this.mimeType = mimeType;
        this.message = message;
    }

    public FileMessageConversation(String local_path, String mimeType, MessageConversation message) {
        this.local_path = local_path;
        this.mimeType = mimeType;
        this.message = message;
        this.idApi = -1;
        this.remote_path = "";
    }

    public FileMessageConversation() {
    }
}
