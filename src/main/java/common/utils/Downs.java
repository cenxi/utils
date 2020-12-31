package common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public final class Downs {
    /**
     * 获取远程文件尺寸
     */
    public static long getRemoteFileSize(String remoteFileUrl) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(remoteFileUrl).openConnection();
        //使用HEAD方法
        return httpConnection.getHeaderFieldLong("Content-Length",-1);
    }


}
