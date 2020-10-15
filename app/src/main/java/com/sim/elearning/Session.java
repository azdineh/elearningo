package com.sim.elearning;

import java.io.Serializable;
import java.util.ArrayList;

public class Session implements Serializable {

    private String id;
    private String title;
    private String date;
    private String modulename;
    private ArrayList<FileContent> filesContent;
    private String teacherid;
    private String description;
    private String discussionid;
    private ArrayList<String> absentStudents;

    public Session() {
        id = new String();
        title = new String("No title");
        date = new String();
        modulename = new String();
        filesContent = new ArrayList<FileContent>();
        teacherid = "";
        description = "";
        absentStudents=new ArrayList<String>();

    }

    public Session(String id, String title, String date, String modulename, ArrayList<FileContent> filesContent,
                   String teacherid, String description, String discussionid) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.modulename = modulename;
        this.filesContent = filesContent;
        this.teacherid = teacherid;
        this.description = description;
        this.discussionid = discussionid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModulename() {
        return modulename;
    }

    public void setModulename(String modulename) {
        this.modulename = modulename;
    }

    public ArrayList<FileContent> getFilesContent() {
        return filesContent;
    }

    public void setFilesContent(ArrayList<FileContent> filesContent) {
        this.filesContent = filesContent;
    }

    public ArrayList<String> getAbsentStudents() {
        return absentStudents;
    }

    public void setAbsentStudents(ArrayList<String> absentStudents) {
        this.absentStudents = absentStudents;
    }

    public String getTeacherid() {
        return teacherid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

    public String getDiscussionid() {
        return discussionid;
    }

    public void setDiscussionid(String discussionid) {
        this.discussionid = discussionid;
    }
}

