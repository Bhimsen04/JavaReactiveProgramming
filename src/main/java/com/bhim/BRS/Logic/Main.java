package com.bhim.BRS.Logic;

import com.bhim.BRS.Entites.Activity;
import com.bhim.BRS.Entites.ErrorMsg;
import com.bhim.BRS.Entites.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {
        InsuranceLicenseDataProcessor ildp = new InsuranceLicenseDataProcessor();
        Activity activity = new Activity();

        Response response = new Response();
        List<ErrorMsg> errorMsgs = new ArrayList<>();
        response.setErrorMsgs(null);

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
