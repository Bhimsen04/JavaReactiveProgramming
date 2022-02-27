package com.bhim.BRS.Logic;

import com.bhim.BRS.Entites.Activity;
import com.bhim.BRS.Entites.ErrorMsg;
import com.bhim.BRS.Entites.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main_Modify {

    public static void main(String[] args) throws InterruptedException {
        InsuranceLicenseDataProcessor_Modify ildp = new InsuranceLicenseDataProcessor_Modify();
        Activity activity = new Activity();

        Response response = new Response();
        response.setResult("N");
        List<ErrorMsg> errorMsgs = new ArrayList<>();
        response.setErrorMsgs(new ArrayList<>());

//        ErrorMsg errorMsg = new ErrorMsg();
//        errorMsg.setErrMsg("hello");
//        errorMsg.setErrCode("1");
//        errorMsgs.add(errorMsg);
//        errorMsg = new ErrorMsg();
//        errorMsg.setErrMsg("hello2");
//        errorMsg.setErrCode("2");
//        errorMsgs.add(errorMsg);
//        response.setErrorMsgs(errorMsgs);

        ildp.callApi(activity, response);
    }
}
