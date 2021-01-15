package common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.stream.FileImageInputStream;
import java.io.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Base64ImageUtil {

    private static String baes64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    public static boolean isBase64(String str) {
        return Pattern.matches(baes64Pattern, str);
    }

    public static String getImageStr(File photo) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;

        // 读取图片字节数组
        InputStream in = null;
        try {
            in = new FileInputStream(photo);
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            log.error("GetImageStr : read error ", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("GetImageStr : close error ", e);
            }
        }

        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(data).replaceAll("\n", "");

        return base64;// 返回Base64编码过的字节数组字符串
    }

    public static boolean generateImage(String base64ImgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
        if (base64ImgStr == null) // 图像数据为空
        {
            return false;
        }
        // 生成jpeg图片
        OutputStream out = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(base64ImgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
        } catch (Exception e) {
            log.error("GenerateImage : read error ", e);
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("GenerateImage : close error ", e);
                return false;
            }
        }

        return true;
    }

    public static boolean generateImage(byte[] imgBytes, String imgFilePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgFilePath);
            fos.write(imgBytes);
            fos.flush();
        } catch (FileNotFoundException e) {
            log.error("文件路径不存在", e);
            return false;
        } catch (IOException e) {
            log.error("IO Exception", e);
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                log.error("IO Exception", e);
            }
        }
        return true;
    }


    /**
     * Base64字符串转 二进制流
     *
     * @param base64String Base64
     * @return base64String
     * @throws IOException 异常
     */
    public static byte[] getStringImage(String base64String) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return base64String != null ? decoder.decodeBuffer(base64String) : null;
    }

    /**
     * 字符串转图片
     *
     * @param base64Str
     * @return
     */
    public static byte[] decode(String base64Str) {
        byte[] b = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            b = decoder.decodeBuffer(replaceEnter(base64Str));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 图片转字符串
     *
     * @param image
     * @return
     */
    public static String encode(byte[] image) {
        BASE64Encoder decoder = new BASE64Encoder();
        return replaceEnter(decoder.encode(image));
    }

    /**
     * 字符串转base64
     *
     * @param str
     * @return
     */
    public static String encode(String str) {
        BASE64Encoder encoder = new BASE64Encoder();
        return replaceEnter(encoder.encode(str.getBytes()));
    }

    /**
     * @path 图片路径
     * * @return
     */
    public static byte[] imageTobyte(String path) {
        byte[] data = null;
        FileImageInputStream input = null;
        try {
            input = new FileImageInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public static byte[] getBytesByImgUrl(String picUrl) {
        Request.Builder builder = new Request.Builder().url(picUrl);
        Request request = builder.build();
        Response response = null;
        try {
            response = OK_HTTP_CLIENT.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().bytes();
            } else {
                log.error("图片下载失败,picUrl->{}", picUrl);
                return null;
            }
        } catch (IOException e) {
            log.error("图片下载异常,picUrl->" + picUrl, e);
            return null;
        } finally {
            if (!Objects.isNull(response)) {
                response.close();
            }
        }
    }

    public static String getBase64ByImgUrl(String picUrl) {
        byte[] bytes = getBytesByImgUrl(picUrl);
        if (bytes == null) {
            return null;
        }
        return encode(bytes);
    }


    public static String replaceEnter(String str) {
        String reg = "[\n-\r]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

}
