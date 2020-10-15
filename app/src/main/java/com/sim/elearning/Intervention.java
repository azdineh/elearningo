package com.sim.elearning;

public class Intervention {

    private String id;
    private String fullname;
    private String intervention;
    private String unixtimestamp;

    public Intervention() {
         String id="";
         String fullname="";
         String intervention="";
         String unixtimestamp="";
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Intervention(String id, String fullname, String intervention, String unixtimestamp) {
        this.id = id;
        this.fullname = fullname;
        this.intervention = intervention;
        this.unixtimestamp = unixtimestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntervention() {
        return intervention;
    }

    public void setIntervention(String intervention) {
        this.intervention = intervention;
    }

    public String getUnixtimestamp() {
        return unixtimestamp;
    }

    public void setUnixtimestamp(String unixtimestamp) {
        this.unixtimestamp = unixtimestamp;
    }

    @Override
    public String toString() {
        return "Intervention{" +
                "id='" + id + '\'' +
                '}';
    }
}
