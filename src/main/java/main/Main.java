package main;

import client.KeTangPaiClient;
import org.apache.log4j.Logger;
import utils.StringSwitchUtil;

import java.io.IOException;

public class Main {
    private static Logger log=Logger.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        log.info("enter main...");
        startKeTangPaiOkhttpClient();
    }

    private static void testHexStringToString() {
        log.info("testHexStringToString...");
        String string="访问成功";
        String hexString=StringSwitchUtil.StringToHexString(string);
        System.out.println(hexString);
        String nString=StringSwitchUtil.hexStringToString(hexString);
        System.out.println(nString);
        System.out.println(string.equals(nString));
    }

    private static void startKeTangPaiOkhttpClient() throws IOException {
        log.info("startKeTangPaiOkhttpClient...");
        KeTangPaiClient keTangPaiClient=new KeTangPaiClient();
        keTangPaiClient.start();
        log.info("timestamp["+System.currentTimeMillis()+"]...");
    }
}
