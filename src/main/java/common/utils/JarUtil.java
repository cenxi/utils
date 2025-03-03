package common.utils;

/**
 * @author fengxi
 * @className JarUtil
 * @description
 * @date 2025年03月03日 14:21
 */
public class JarUtil {

    /**
     * 获取当前jar包所在路径
     * @return
     */
    public static String getJarDir() {
        String path = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (System.getProperty("os.name").contains("dows")) {
            path = path.substring(1, path.length());
        }
        if (path.contains("jar")) {
            path = path.substring(0, path.lastIndexOf("."));
            path = path.substring(0, path.lastIndexOf("/"));
        }
        path = path.replace("file:", "");
        return path;
    }
}
