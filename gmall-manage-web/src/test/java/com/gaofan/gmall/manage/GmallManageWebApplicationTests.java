package com.gaofan.gmall.manage;

import com.gaofan.gmall.manage.util.MyUploadUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.applet.Main;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTests {

    @Test
    public void contextLoads() {
    }

    public static void main(String[] args) {
        String imgUrl = "http://192.168.91.135";
        String trackerFile = GmallManageWebApplicationTests.class.getClassLoader().getResource("tracker.conf").getFile();
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

        try {
            String[] imgs = storageClient.upload_appender_file("D:/123.jpg", "jpg", null);
            for (String img : imgs) {
                System.out.println(img);
                System.out.println("--------------------------------");
                imgUrl += "/" + img;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        System.out.println(imgUrl);
    }
}
