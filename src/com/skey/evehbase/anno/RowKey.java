package com.skey.evehbase.anno;

import java.lang.annotation.*;

/**
 * HBase Rowkey注解 用于解析Result
 * <p>
 * Date: 2019/1/18 10:22
 *
 * @author A Lion~
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RowKey {
}
