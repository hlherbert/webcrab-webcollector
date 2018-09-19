package webcrab.fangxingou.module.po;

//返回结果
public abstract class Result {
    protected int errNo;//错误码 0
    protected String message;//消息 success

    public int getErrNo() {
        return errNo;
    }

    public void setErrNo(int errNo) {
        this.errNo = errNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
