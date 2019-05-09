package com.skey.evehbase.anno;

import java.lang.annotation.*;

/**
 * 列簇注解
 * <p>
 * Date: 2019/1/18 10:23
 *
 * @author A Lion~
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ColumnFamily {
}
