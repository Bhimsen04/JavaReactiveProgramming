package com.bhim.BRS.Entites;

import java.util.List;

public class Response {

    private String result;
    private List<ErrorMsg> errorMsgs = null;

    public Response() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ErrorMsg> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(List<ErrorMsg> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }
}
