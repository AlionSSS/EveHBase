package com.skey.evehbase.bean;

import com.skey.evehbase.anno.ColumnFamily;
import com.skey.evehbase.anno.RowKey;

/**
 * 示例-人员信息Bean
 * <p>
 * Date: 2019/1/18 9:16
 *
 * @author A Lion~
 */
public class Person {

    @RowKey
    public String rowkey;

    @ColumnFamily
    public Info f;

    public class Info {

        public String address;

        public String phone;

        public String name;

        public String age;

        @Override
        public String toString() {
            return "Info{" +
                    "address='" + address + '\'' +
                    ", phone='" + phone + '\'' +
                    ", name='" + name + '\'' +
                    ", age='" + age + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Person{" +
                "rowkey='" + rowkey + '\'' +
                ", f=" + f +
                '}';
    }

}
