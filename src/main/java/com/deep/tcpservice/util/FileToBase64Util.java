package com.deep.tcpservice.util;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;

public class FileToBase64Util {

    public static String file2Base64(File file) throws Exception {
        if (file == null) {
            return null;
        }
        String base64 = null;
        FileInputStream fin = null;
        fin = new FileInputStream(file);
        byte[] buff = new byte[fin.available()];
        fin.read(buff);
        base64 = Base64.encode(buff);

        try {
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public static File base64ToFile(String base64) throws Exception {
        if (base64 == null || "".equals(base64)) {
            return null;
        }
        byte[] buff = Base64.decode(base64);
        File file = null;
        FileOutputStream fout = null;

        file = File.createTempFile("tmp", null);
        fout = new FileOutputStream(file);
        fout.write(buff);

        try {
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
