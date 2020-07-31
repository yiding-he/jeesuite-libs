/**
 *
 */
package com.jeesuite.filesystem;

import java.io.Closeable;
import java.util.Map;

/**
 * 上传接口
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年1月5日
 */
public interface FSProvider extends Closeable {

    String name();

    /**
     * 文件上传
     * @param object
     * @return
     */
    public String upload(UploadObject object);

    /**
     * 获取文件下载地址
     * @param file 文件（全路径或者fileKey）
     * @return
     */
    public String getDownloadUrl(String fileKey);

    /**
     * 删除图片
     * @return
     */
    public boolean delete(String fileKey);

    public String downloadAndSaveAs(String fileKey, String localSaveDir);

    public Map<String, Object> createUploadToken(UploadTokenParam param);
}
