package io.fusion.java.gc;

/**
 * @author enhao
 */
public class GarbageCollectionTest {

    /**
     * -XX:+PrintGCDetails
     */
    public static void main(String[] args) {
        byte[] allocation1, allocation2;
        allocation1 = new byte[30900 * 1024];
        allocation2 = new byte[900*1024];

    }
}
