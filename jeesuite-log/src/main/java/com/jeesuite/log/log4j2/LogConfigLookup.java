package com.jeesuite.log.log4j2;

import com.jeesuite.common.util.ResourceUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.AbstractLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(name = "context", category = StrLookup.CATEGORY)
@Order(value = -1)
public class LogConfigLookup extends AbstractLookup {

    public LogConfigLookup() {
        super();
    }

    @Override
    public String lookup(LogEvent event, String key) {
        return ResourceUtils.getProperty(key);
    }

}
