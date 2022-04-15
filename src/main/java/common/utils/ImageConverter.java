package common.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Base64;
import java.util.Objects;

/**
 *  图片格式转换
 *  支持格式 url, base64, byte[], File
 *  @author renchao
 *  @date 2018/10/15
 */
public final class ImageConverter {

    private ImageConverter() {}

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageConverter.class);
    
    private static final String BASE64_HEADER = "base64,";

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    //byte to
    /**
     * byte[] -> base64
     * @param bytes 图片(byte[])
     * @return 图片(base64)
     */
    public static String bytesToBase64(byte[] bytes){
        return ENCODER.encodeToString(bytes);
    }

    /**
     * byte[] -> File
     * @param bytes 图片(byte[])
     * @param filePath 图片(file path)
     * @return 图片(File); null if error
     */
    public static File bytesToFile(byte[] bytes, String filePath) {
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists() && !fileParent.mkdirs()){
            LOGGER.debug("mkdirs [{}] fail", filePath);
            return null;
        }
        try (FileOutputStream os = new FileOutputStream(file);
            ByteArrayInputStream is = new ByteArrayInputStream(bytes)) {
            byte[] buffer = new byte[1024];
            int length;
            //开始填充数据
            while ((length = is.read(buffer))>0){
                os.write(buffer, 0, length);
            }
            return file;
        } catch (IOException e){
            LOGGER.debug("write to file [{}] error {}-{}", filePath, e.getClass(), e.getLocalizedMessage());
            return null;
        }
    }
    
    //base64 to
    /**
     * Clean base64Header as "image/jpeg;base64,"
     * @param base64 图片(base64)
     * @return 图片(base64) without "image/jpeg;base64,"
     */
    public static String base64HeaderClean(String base64){
        int i = base64.indexOf(BASE64_HEADER);
        if (i >= 0){
            return base64.substring(i + BASE64_HEADER.length());
        }
        return base64;
    }

    /**
     * base64 -> byte[]
     * @param base64 图片(base64)
     * @return 图片(byte[])
     */
    public static byte[] base64ToBytes(String base64){
        return DECODER.decode(base64HeaderClean(base64));
    }

    /**
     * base64 -> File
     * @param base64 图片(base64)
     * @param filePath 图片(file path)
     * @return 图片(File); null if error
     */
    public static File base64ToFile(String base64, String filePath){
        return bytesToFile(base64ToBytes(base64), filePath);
    }
    
    //url to
    /**
     * url -> base64
     * @param url 图片(url)
     * @return 图片(base64); null if error
     */
    public static String urlToBase64(String url){
        if (StringUtils.isEmpty(url)){
            return null;
        }
        byte[] bytes = urlToBytes(url);
        if (null == bytes){
            return null;
        }
        return bytesToBase64(bytes);
    }

    /**
     * url -> File
     * @param url 图片(url)
     * @param filePath 图片(file path)
     * @return 图片(File); null if error
     */
    public static File urlToFile(String url, String filePath){
        byte[] bytes = urlToBytes(url);
        if (null == bytes){
            return null;
        }
        return bytesToFile(bytes, filePath);
    }

    /**
     * url -> byte[]
     * @param url 图片(url)
     * @return 图片(byte[]); null if error
     */
    public static byte[] urlToBytes(String url){
        try {
            Response response = CLIENT.newCall(new Request.Builder().url(url).get().build()).execute();
            if (Objects.isNull(response) || !response.isSuccessful() || Objects.isNull(response.body())){
                LOGGER.error("url to bytes error, response body is null");
                return new byte[0];
            }
            return response.body().bytes();
        } catch (Exception e) {
            LOGGER.error("url to bytes error, msg:{}", e.getLocalizedMessage());
            return new byte[0];
        }
    }
    
    //file to
    /**
     * file path -> byte[]
     * @param filePath 图片(file path)
     * @return 图片(byte[]); null if error
     */
    public static byte[] fileToBytes(String filePath){
        return fileToBytes(new File(filePath));
    }

    /**
     * File -> byte[]
     * @param file 图片(File)
     * @return 图片(byte[]); null if error
     */
    public static byte[] fileToBytes(File file) {
        try(InputStream is = new FileInputStream(file);
            ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            //开始填充数据
            while ((length = is.read(buffer)) > 0){
                os.write(buffer, 0, length);
            }
            return os.toByteArray();
        } catch (IOException e){
            LOGGER.debug("read from file [{}] error {}-{}", file.getName(), e.getClass(), e.getLocalizedMessage());
            return new byte[0];
        }
    }

    /**
     * File -> base64
     * @param file 图片(File)
     * @return 图片(base64); null if error
     */
    public static String fileToBase64(File file){
        return bytesToBase64(fileToBytes(file));
    }

    /**
     * file path -> base64
     * @param filePath 图片(file path)
     * @return 图片(base64); null if error
     */
    public static String fileToBase64(String filePath){
        return bytesToBase64(fileToBytes(filePath));
    }
    
}
