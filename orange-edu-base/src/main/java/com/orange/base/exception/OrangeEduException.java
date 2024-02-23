package com.orange.base.exception;

public class OrangeEduException extends RuntimeException{
    private String errMessage;

    public OrangeEduException() {
        super();
    }

    public OrangeEduException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public static void cast(CommonError commonError){
        throw new OrangeEduException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new OrangeEduException(errMessage);
    }

}
