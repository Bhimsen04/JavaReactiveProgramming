package com.bhim.BRS.Entites;

import java.util.List;

public class Activity {
    private List<String> lcFaResponseCodes;

    public List<String> getLcFaResponseCodes() {
        return lcFaResponseCodes;
    }

    public Activity setLcFaResponseCodes(List<String> lcFaResponseCodes) {
        this.lcFaResponseCodes = lcFaResponseCodes;
        return this;
    }
}
