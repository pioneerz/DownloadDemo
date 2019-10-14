package com.timcoder.downloaddemo;

/**
 * @author Administrator
 * 2019/10/10 0010.
 */
public class OkDownload {

    private static OkDownload mInstance;

    public static OkDownload getInstance() {
        if (mInstance == null) {
            synchronized (OkDownload.class) {
                if (mInstance == null) {
                    mInstance = new OkDownload();
                }
            }
        }
        return mInstance;
    }


}
