package com.sim.elearning;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class FileContent implements Serializable {

    private String name;
    private String remoteName;
    private  String uri;

    public FileContent() {
        this.name = "";
        this.uri = "";
    }

    public FileContent(String name, String uri) {
        this.name = name;
        this.uri = uri;
        this.remoteName="";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }



    public String getRemoteName() {
        return remoteName;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
