/**
 *
 */
package com.jeesuite.common.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年11月3日
 */
public class NodeNameHolder {

    private static String nodeId;

    public static String getNodeId() {
        if (nodeId != null) return nodeId;
        try {
            nodeId = InetAddress.getLocalHost().getHostAddress() + "_" + RandomStringUtils.random(3, true, true).toLowerCase();
        } catch (Exception e) {
            nodeId = UUID.randomUUID().toString();
        }
        return nodeId;
    }

}
