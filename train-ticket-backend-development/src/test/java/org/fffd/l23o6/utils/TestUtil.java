package org.fffd.l23o6.utils;

import java.util.Random;

/**
 * @author xueruichen
 * @description 测试工具类
 */
public class TestUtil {
    private static TestUtil instance = new TestUtil();
    public static TestUtil getInstance() {
        return instance;
    }
    public int generateRandomNumber(int origin, int bound) {
        Random random = new Random();
        return random.nextInt(origin, bound);
    }

    public int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt();
    }

}
