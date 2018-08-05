package com.gaofan.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MyUploadUtil {
    public static String uploadForFDFS(MultipartFile file){
        String imgUrl = "http://192.168.91.135";
        String trackerFile = MyUploadUtil.class.getClassLoader().getResource("tracker.conf").getFile();
        try {
            ClientGlobal.init(trackerFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageClient storageClient = new StorageClient(trackerServer,null);

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") +1);

        try {
            String[] imgs = storageClient.upload_appender_file(file.getBytes(), ext, null);
            for (String img : imgs) {
                System.out.println(img);
                imgUrl += "/" + img;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        System.out.println("imgUrlimgUrl============" + imgUrl);
        return imgUrl;
    }

}
