package com.muic.objectstorage.Service;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Utils {
    public static String calculateMd5(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return DigestUtils.md5Hex(fis);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate md5");
        }
    }
}
