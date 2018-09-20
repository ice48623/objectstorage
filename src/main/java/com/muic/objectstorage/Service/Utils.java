package com.muic.objectstorage.Service;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.ServletInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class Utils {
    public static String calculateMd5(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return DigestUtils.md5Hex(fis);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate md5");
        }
    }

    public static String calculateMd5FromString(String file) {
        try {
            return DigestUtils.md5Hex(file);
        } catch (Exception e) {
            throw new RuntimeException("Unable to calculate md5");
        }
    }

    public static String computeETag(List<String> md5List) {
        StringBuilder eTag = new StringBuilder();
        for (String s : md5List) {
            eTag.append(s);
        }
        return Utils.calculateMd5FromString(eTag.toString()) + "-" + md5List.size();
    }
}
