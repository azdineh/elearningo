package com.sim.elearning;

import java.util.ArrayList;
import java.util.List;

public class Discussion {

    private String id;
    private List<Intervention> interventions;

    public Discussion() {
        interventions=new ArrayList<Intervention>();
    }

    public Discussion(String id, List<Intervention> interventions) {
        this.id = id;
        this.interventions = interventions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Intervention> getInterventions() {
        return interventions;
    }

    public void setInterventions(List<Intervention> interventions) {
        this.interventions = interventions;
    }
}
