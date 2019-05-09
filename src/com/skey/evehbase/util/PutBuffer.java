package com.skey.evehbase.util;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Put缓冲器
 * <p>
 * Date: 2018/11/16 11:41
 *
 * @author A Lion~
 */
public class PutBuffer {

    private static final int BUFFER_SIZE = 2000;

    private Table table;

    private List<Put> putList = new ArrayList<>(BUFFER_SIZE);

    public PutBuffer(Table table) {
        this.table = table;
    }

    public void put(Put put) throws IOException {
        if (putList.size() < BUFFER_SIZE) {
            putList.add(put);
        } else {
            flush();
        }
    }

    public void flush() throws IOException {
        table.put(putList);
        putList.clear();
    }

}
