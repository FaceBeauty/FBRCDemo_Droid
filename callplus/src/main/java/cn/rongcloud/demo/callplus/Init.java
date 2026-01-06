package cn.rongcloud.demo.callplus;

public class Init {
    private boolean isInitBuffer =false;


    private static volatile Init instance = null;

    private Init() {}


    public static Init getInstance() {
        if (instance == null) {
            synchronized (Init.class) {
                if (instance == null) {
                    instance = new Init();
                }
            }
        }
        return instance;
    }

    public boolean isInitBuffer() {
        return isInitBuffer;
    }

    public void setInitBuffer(boolean initBuffer) {
        isInitBuffer = initBuffer;
    }
}
