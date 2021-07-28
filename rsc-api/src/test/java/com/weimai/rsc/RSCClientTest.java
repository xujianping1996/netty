package com.weimai.rsc;

import java.util.Scanner;

import com.weimai.rsc.handler.NettyClientHandler;
import com.weimai.rsc.msg.DBTable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-17 14:39
 */
public class RSCClientTest {
    //private static Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    public static String IP = "127.0.0.1";
    public static int PORT = 8088;

    //public static void main(String[] args) throws InterruptedException {
    //    //NettyClientHandler rscClient = new NettyClientHandler();
    //    //NettyClientHandler connect = rscClient.connect(IP, PORT);
    //
    //    SendClient sendClient = new SendClient();
    //    sendClient.connect(IP,PORT);
    //    Scanner sc = new Scanner(System.in);
    //    //只要还有下一个
    //    while (sc.hasNext()) {
    //        //获取扫描器的下一个完整标记
    //        String line = sc.nextLine();
    //        //判断结束标记
    //        if ("over".equals(line))
    //            break;
    //        DBTable read = sendClient.executeQuery(line);
    //        Object[][] header = read.getHeader();
    //        for (int i = 0; i < header.length; i++) {
    //            System.out.print(header[i][0]+"\t\t");
    //        }
    //        System.out.println();
    //        Object[][] data = read.getData();
    //        for (Object[] datum : data) {
    //            for (int i = 0; i < datum.length; i++) {
    //                System.out.print(datum[i]+"\t\t");
    //            }
    //            System.out.println();
    //        }
    //    }
    //    sc.close();
    //}

    public static void main(String[] args) throws InterruptedException {
        //NettyClientHandler rscClient = new NettyClientHandler();
        //NettyClientHandler connect = rscClient.connect(IP, PORT);

        SendClient sendClient = new SendClient();
        sendClient.connect(IP,PORT);
        Scanner sc = new Scanner(System.in);
        //只要还有下一个
        while (sc.hasNext()) {
            //获取扫描器的下一个完整标记
            String line = sc.nextLine();
            //判断结束标记
            if ("over".equals(line))
                break;
            Integer read = sendClient.executeUpdate(line);
            System.out.println(read);
        }
        sc.close();
    }
}
