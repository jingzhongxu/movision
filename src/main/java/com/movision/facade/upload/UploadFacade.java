package com.movision.facade.upload;

import com.movision.common.constant.ApiConstants;
import com.movision.common.constant.MsgCodeConstant;
import com.movision.exception.BusinessException;
import com.movision.utils.PropertiesUtils;
import com.movision.utils.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhuangyuhao
 * @Date 2017/2/27 17:02
 */
@Service
@Transactional
public class UploadFacade {
    private static final Logger log = LoggerFactory.getLogger(UploadFacade.class);

    @Autowired
    private ApiConstants apiConstants;

    /**
     * 图片|文件上传 (测试环境) commons-upload
     *
     * @param file
     * @param type  目前支持三种：img/doc/video
     * @param chann 如：post，person
     * @return
     */
    public Map<String, String> upload(MultipartFile file, String type, String chann) {
        Map<String, String> result = new HashMap<>();
        try {
            /**
             * 这是测试环境上传的图片的路径
             * saveDirectory = /WWW/tomcat-8100/apache-tomcat-7.0.73/webapps/upload/$chan/img
             */
            String saveDirectory;
            long maxPostSize;
            String imgDomain = PropertiesUtils.getValue("upload.img.domain");
//            String docDomain = PropertiesUtils.getValue("doc.domain");
            String fileName = FileUtil.renameFile(file.getOriginalFilename());
            String data;
            switch (type) {
                case "img":
                    if (chann != null) {
                        saveDirectory = apiConstants.getUploadDir() + "/" + chann + "/img";
                        maxPostSize = apiConstants.getUploadPicMaxPostSize();
                        /**
                         * 这是外界访问该图片的地址
                         * 其中data = http://120.77.214.187:8100/upload/$chan/img/$filename
                         */
                        data = imgDomain + "/upload/" + chann + "/img/" + fileName;
                    } else {
                        saveDirectory = apiConstants.getUploadDir();
                        maxPostSize = apiConstants.getUploadPicMaxPostSize();
                        data = imgDomain + "/upload/" + fileName;
                    }

                    break;
                case "doc":
                    if (chann != null) {
                        saveDirectory = apiConstants.getUploadDir() + "/" + chann + "/doc";
                        maxPostSize = apiConstants.getUploadDocMaxPostSize();
                        /*if (chann.equals("tech")) {
                            maxPostSize = apiConstants.getUploadTechMaxPostSize();
                        }*/
                    } else {
                        saveDirectory = apiConstants.getUploadDir();
                        maxPostSize = apiConstants.getUploadDocMaxPostSize();
                    }
                    data = fileName;
                    break;
                case "video":
                    if (chann != null) {
                        saveDirectory = apiConstants.getUploadDir() + "/" + chann + "/video";
                        maxPostSize = apiConstants.getUploadVideoMaxPostSize();
                        /**
                         * 这是外界访问该视频的地址
                         * 其中data = http://120.77.214.187:8100/upload/$chan/video/$filename
                         */
                        data = imgDomain + "/upload/" + chann + "/video/" + fileName;
                    } else {
                        saveDirectory = apiConstants.getUploadDir();
                        maxPostSize = apiConstants.getUploadVideoMaxPostSize();
                        data = imgDomain + "/upload/" + fileName;
                    }
                    break;

                //简历压缩包上传
                /*case "zip":
                    if (chann != null && chann.equals("51job")){
                        saveDirectory = apiConstants.getUploadDir() + "/" + chann + "/zip";
                        maxPostSize = apiConstants.getUploadTechMaxPostSize();
                    }else if (chann != null && chann.equals("zhilian")){
                        saveDirectory = apiConstants.getUploadDir() + "/" + chann + "/zip";
                        maxPostSize = apiConstants.getUploadTechMaxPostSize();
                    }else if (chann != null && chann.equals("rencai")){
                        saveDirectory = apiConstants.getUploadDir() + "/" + chann + "/zip";
                        maxPostSize = apiConstants.getUploadTechMaxPostSize();
                    }else if (chann != null && chann.equals("liepin")){
                        saveDirectory = apiConstants.getUploadDir() + "/" + chann + "/zip";
                        maxPostSize = apiConstants.getUploadTechMaxPostSize();
                    }
                    else {
                        saveDirectory = apiConstants.getUploadDir();
                        maxPostSize = apiConstants.getUploadDocMaxPostSize();
                    }
                    data = fileName;
                    break;*/

                default:
                    log.error("上传类型不支持");
                    throw new BusinessException(MsgCodeConstant.SYSTEM_ERROR, "上传类型不支持");
            }


            //目录不存在则创建
            log.info("【saveDirectory】=" + saveDirectory);
            File dir = new File(saveDirectory);
            if (!dir.exists() && !dir.isDirectory()) {
                dir.mkdirs();
                log.info("mk dir susscess dirName = " + saveDirectory);
            }

            long size = file.getSize();
            if (size > maxPostSize) {
                throw new BusinessException(MsgCodeConstant.SYSTEM_ERROR, "文件大小超过限制");
            }

            File upfile = new File(saveDirectory + "/" + fileName);
            //使用transferTo（dest）方法将上传文件写到服务器上指定的文件
            file.transferTo(upfile);

            result.put("status", "success");
            result.put("data", data);

        } catch (Exception e) {
            log.error("upload error!", e);
            result.put("status", "fail");
        }
        return result;
    }
}
