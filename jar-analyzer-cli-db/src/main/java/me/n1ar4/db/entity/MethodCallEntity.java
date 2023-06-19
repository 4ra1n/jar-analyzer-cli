package me.n1ar4.db.entity;

public class MethodCallEntity {
    private int mcId;
    private String callerClassName;
    private String callerMethodName;
    private String callerMethodDesc;
    private String calleeClassName;
    private String calleeMethodName;
    private String calleeMethodDesc;

    public int getMcId() {
        return mcId;
    }

    public void setMcId(int mcId) {
        this.mcId = mcId;
    }

    public String getCallerClassName() {
        return callerClassName;
    }

    public void setCallerClassName(String callerClassName) {
        this.callerClassName = callerClassName;
    }

    public String getCallerMethodName() {
        return callerMethodName;
    }

    public void setCallerMethodName(String callerMethodName) {
        this.callerMethodName = callerMethodName;
    }

    public String getCallerMethodDesc() {
        return callerMethodDesc;
    }

    public void setCallerMethodDesc(String callerMethodDesc) {
        this.callerMethodDesc = callerMethodDesc;
    }

    public String getCalleeClassName() {
        return calleeClassName;
    }

    public void setCalleeClassName(String calleeClassName) {
        this.calleeClassName = calleeClassName;
    }

    public String getCalleeMethodName() {
        return calleeMethodName;
    }

    public void setCalleeMethodName(String calleeMethodName) {
        this.calleeMethodName = calleeMethodName;
    }

    public String getCalleeMethodDesc() {
        return calleeMethodDesc;
    }

    public void setCalleeMethodDesc(String calleeMethodDesc) {
        this.calleeMethodDesc = calleeMethodDesc;
    }

    @Override
    public String toString() {
        return "MethodCallEntity{" +
                "mcId=" + mcId +
                ", callerClassName='" + callerClassName + '\'' +
                ", callerMethodName='" + callerMethodName + '\'' +
                ", callerMethodDesc='" + callerMethodDesc + '\'' +
                ", calleeClassName='" + calleeClassName + '\'' +
                ", calleeMethodName='" + calleeMethodName + '\'' +
                ", calleeMethodDesc='" + calleeMethodDesc + '\'' +
                '}';
    }
}
