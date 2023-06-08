package com.hcmut.admin.utraffictest.model;

import java.util.List;

public class DiagnosisInfo {
    private String name;
    private List<Integer> specialisationIds;
    private Double accuracy;

    public String getName() {
        return name;
    }

    public List<Integer> getSpecialisationIds() {
        return specialisationIds;
    }

    public Double getAccuracy() {
        return accuracy;
    }
}
