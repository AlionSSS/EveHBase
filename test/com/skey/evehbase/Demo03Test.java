package com.skey.evehbase;

/**
 * Descr
 * <p>
 * Date: 2019/1/18 16:10
 *
 * @author A Lion~
 */
public class Demo03Test {

    public static void main(String[] args) {
        try {
            Class<?> c = Class.forName("com.skey.evehbase.bean.Person");
            Class<?> clazz = Class.forName("com.skey.evehbase.bean.Person$Info");
            System.out.println("clazz = " + clazz.getDeclaredConstructors()[0].newInstance(c.newInstance()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
