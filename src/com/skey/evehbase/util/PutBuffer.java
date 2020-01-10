package com.skey.evehbase.util;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Put缓冲器
 * <p>
 *     当bufferSize达到指定大小，或者距离上次入库时间达到duration毫秒，将会直接入库
 * </p>
 *
 * Date: 2018/11/16 11:41
 *
 * @author A Lion~
 */
public class PutBuffer {

    private final int bufferSize;

    private final int duration;

    private final Table table;

    private final List<Put> putList ;

    private long lastTime = 0;

    public PutBuffer(Table table) {
        this(table, 1000, 5000);
    }

    public PutBuffer(Table table, int bufferSize, int duration) {
        this.table = table;
        this.bufferSize = bufferSize;
        this.duration = duration;
        putList = new ArrayList<>(bufferSize);
    }

    /**
     * 将Put放入缓冲区
     * @param put HBase的 {@link Put}
     * @throws IOException
     */
    public void put(Put put) throws IOException {
        if (putList.size() < bufferSize && System.currentTimeMillis() - lastTime < duration) {
            putList.add(put);
        } else {
            flush();
        }
    }

    /**
     * 将当前缓冲区的数据直接写入HBase
     * @throws IOException
     */
    public void flush() throws IOException {
        table.put(putList);
        putList.clear();

        lastTime = System.currentTimeMillis();
    }

}
