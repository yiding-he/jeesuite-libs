/**
 *
 */
package com.jeesuite.mybatis.plugin.shard;

import java.util.List;

/**
 * 数据库分库策略接口
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年2月2日
 * @Copyright (c) 2015, jwww
 */
public interface ShardStrategy<T> {

    /**
     * 分库字段
     * @return
     */
    public String shardDbField();

    /**
     * 分库字段对应实体属性名
     * @return
     */
    public String shardEntityField();


    /**
     * 分配逻辑
     * @param value
     * @return 数据库index
     */
    public int assigned(Object value);

    /**
     * 忽略分库表名列表
     * @return
     */
    public List<String> ignoreTables();

    /**
     * 是否全局
     * @return
     */
    public boolean isGlobal();
}
