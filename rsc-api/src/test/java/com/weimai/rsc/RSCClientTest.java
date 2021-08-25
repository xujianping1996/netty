package com.weimai.rsc;

import java.time.Month;
import java.util.List;
import java.util.Map;

import com.weimai.rsc.clients.SqlQueryClient;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-17 14:39
 */
public class RSCClientTest {
    //private static Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);


    public static void main(String[] args) throws InterruptedException {
        //String sql = "SELECT * FROM request_log WHERE institution_id = 100214 AND method_name = 'open_third_user'";
        for (int i = 0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0;i<10;i++) {
                        String sql = "SELECT * FROM request_log WHERE institution_id = ? AND method_name = ?";
                         String IP = "127.0.0.1";
                         int PORT = 8091;
                        //String sql = "SELECT * FROM request_log";
                        String method_name = "open_third_user";
                        Long institution_id = 100214L;
                        //try {
                        //    Thread.sleep(Math.round((Math.random()+1) * 1000));
                        //} catch (InterruptedException e) {
                        //    e.printStackTrace();
                        //}
                        List<Map<String, String>> execute = new SqlQueryClient(IP, PORT).sql(sql).param(institution_id).param(method_name).execute();
                        //List<Map<String, String>> execute = new QuerySqlClient(IP, PORT).sql(sql).execute();
                        //System.out.println(execute.size());
                        //try {
                        //    Thread.sleep(Math.round((Math.random()+1) * 1000));
                        //} catch (InterruptedException e) {
                        //    e.printStackTrace();
                        //}

                    }
                }
            },"用户线程:"+i).start();
        }



    }

    //public static void main(String[] args) throws InterruptedException {
    //    //NettyClientHandler rscClient = new NettyClientHandler();
    //    //NettyClientHandler connect = rscClient.connect(IP, PORT);
    //
    //    Client sendClient = new Client();
    //    sendClient.connect(IP,PORT);
    //    Scanner sc = new Scanner(System.in);
    //    //只要还有下一个
    //    while (sc.hasNext()) {
    //        //获取扫描器的下一个完整标记
    //        String line = sc.nextLine();
    //        //判断结束标记
    //        if ("over".equals(line))
    //            break;
    //        DBTable read = null;
    //        try {
    //            read = sendClient.executeQuery(line);
    //            Object[][] header = read.getHeader();
    //            for (int i = 0; i < header.length; i++) {
    //                System.out.print(header[i][0]+"\t\t");
    //            }
    //            System.out.println();
    //            Object[][] data = read.getData();
    //            for (Object[] datum : data) {
    //                for (int i = 0; i < datum.length; i++) {
    //                    System.out.print(datum[i]+"\t\t");
    //                }
    //                System.out.println();
    //            }
    //        } catch (Exception e) {
    //            System.out.println(e.getMessage());
    //        }
    //
    //    }
    //    sc.close();
    //}

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
    //        Integer read = sendClient.executeUpdate(line);
    //        System.out.println(read);
    //    }
    //    sc.close();
    //}
}
