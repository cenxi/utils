package common.utils;

import java.net.ServerSocket;

/**
 * @author fengxi
 * @className NetUtils
 * @description
 * @date 2024年07月04日 15:39
 */
public class NetUtil {

    /**
     * 判断本地端口是否空闲可用
     * @param port
     * @return
     */
    public static boolean isPortAvailable(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // If the code reaches this point, the port is available
            return true;
        } catch (Exception e) {
            // Port is not available
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(isPortAvailable(5555));
    }
}
