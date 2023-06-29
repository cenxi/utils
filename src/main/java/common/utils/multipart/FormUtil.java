package common.utils.multipart;

import org.springframework.web.multipart.MultipartFile;

/**
 * form工具
 * @author renchao
 */
public final class FormUtil {

    public static final String DEFAULT_PARAM_NAME = "file";

    public static final String DEFAULT_ARRAY_PARAM_NAME = "files";

    public static final String DEFAULT_CONTENT_TYPE = "multipart/form-data";

    private FormUtil() {}

    public static MultipartFile generateMultipartFile(String fileName, byte[] file){
        return new ByteArrayMultipartFile(DEFAULT_PARAM_NAME, fileName, DEFAULT_CONTENT_TYPE, file);
    }

    public static MultipartFile generateMultipartFiles(String fileName, byte[] file){
        return new ByteArrayMultipartFile(DEFAULT_ARRAY_PARAM_NAME, fileName, DEFAULT_CONTENT_TYPE, file);
    }

}
