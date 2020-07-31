/**
 *
 */
package com.jeesuite.mybatis.datasource;

import com.jeesuite.mybatis.MybatisConfigs;
import com.jeesuite.mybatis.MybatisRuntimeContext;
import com.jeesuite.mybatis.plugin.JeesuiteMybatisInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多数据源管理器
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年2月2日
 * @Copyright (c) 2015, jwww
 */
public class MuitDataSourceManager {

    private static final Logger logger = LoggerFactory.getLogger(MuitDataSourceManager.class);

    private static final String NON_TENANT_KEY = "_none";

    private final static AtomicLong counter = new AtomicLong(10);

    private static final Map<String, String> masters = new HashMap<>();

    private static final Map<String, List<String>> slaves = new HashMap<>();

    private static volatile MuitDataSourceManager instance = new MuitDataSourceManager();

    private MuitDataSourceManager() {
    }

    public static MuitDataSourceManager get() {
        return instance;
    }

    protected void registerDataSourceKey(String dsKey) {

        String tenantId = dsKey.startsWith("tenant") ? dsKey.split("\\[|\\]")[1] : NON_TENANT_KEY;
        if (dsKey.contains("master")) {
            masters.put(tenantId, dsKey);
        } else {
            if (!slaves.containsKey(tenantId)) {
                slaves.put(tenantId, new ArrayList<>());
            }
            slaves.get(tenantId).add(dsKey);
        }
    }

    /**
     * 获取当前数据源名
     *
     * @return
     */
    protected String getDataSourceKey() {
        String tenantId = MybatisRuntimeContext.getTenantId();
        if (tenantId == null || !MybatisConfigs.isTenantModeEnabled()) {
            tenantId = NON_TENANT_KEY;
        }

        if (tenantId == null) {
            throw new DataSourceRouteException("Tenant routeKey Not found");
        }

        String dsKey = null;
        DataSourceContextVals vals = MybatisRuntimeContext.getDataSourceContextVals();
        if (JeesuiteMybatisInterceptor.isRwRouteEnabled() == false) {
            dsKey = masters.get(tenantId);
        } else {
            if (vals.forceMaster || !vals.userSlave) {
                dsKey = masters.get(tenantId);
            } else {
                dsKey = selectSlave(tenantId);
            }
            vals.dsKey = dsKey;
        }

        if (dsKey == null) {
            throw new DataSourceRouteException("Not found any dsKey for [" + tenantId + "]");
        }
        logger.debug("current route rule is:tenantId:[{}],userSlave[{}]|forceMaster[{}], use dataSource key is [{}]!", tenantId, vals.userSlave, vals.forceMaster, vals.dsKey);

        return dsKey;
    }

    /**
     * 轮循分配slave节点
     *
     * @return
     */
    private static String selectSlave(String tenantId) {
        //  无从库则路由到主库
        if (slaves.isEmpty()) {
            logger.debug("current no slave found ,default use [{}]!", masters.get(tenantId));
            return masters.get(tenantId);
        }
        if (slaves.size() == 1)
            return slaves.get(tenantId).get(0);

        int selectIndex = (int) (counter.getAndIncrement() % slaves.size());
        String slaveKey = slaves.get(tenantId).get(selectIndex);
        return slaveKey;
    }

    public List<String> getStandaloneDadaSourceTenantIds() {
        ArrayList<String> list = new ArrayList<>(masters.keySet());
        list.remove(NON_TENANT_KEY);
        return list;
    }
}


