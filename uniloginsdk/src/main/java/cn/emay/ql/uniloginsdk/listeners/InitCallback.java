package cn.emay.ql.uniloginsdk.listeners;

public abstract class InitCallback {

    public abstract void onSuccess(String msg);

    public abstract void onFailed(String msg);
}