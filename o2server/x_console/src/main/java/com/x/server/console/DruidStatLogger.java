package com.x.server.console;

import static com.alibaba.druid.util.JdbcSqlStatUtils.rtrim;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import com.alibaba.druid.pool.DruidDataSourceStatLoggerAdapter;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

public class DruidStatLogger extends DruidDataSourceStatLoggerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DruidStatLogger.class);

    private static final String FILE_NAME_PREFIX = "sqlStat_";
    private static final String FILE_NAME_EXTENSION = ".json";

    private static final Integer MAXSIZE = 50;

    @Override
    public void log(DruidDataSourceStatValue statValue) {

        Map<String, Object> map = new LinkedHashMap<>();

        setBase(map, statValue);

        setConnection(statValue, map);

        setWaitThreadCount(map, statValue);

        setNotEmptyWaitCount(map, statValue);

        setNotEmptyWaitMillis(map, statValue);

        setLogicConnectErrorCount(map, statValue);

        setPhysicalConnectCount(map, statValue);

        setPhysicalCloseCount(map, statValue);

        setPhysicalConnectErrorCount(map, statValue);

        setExecuteCount(map, statValue);

        setErrorCount(map, statValue);

        setCommitCount(map, statValue);

        setRollbackCount(map, statValue);

        setPstmtCacheHitCount(map, statValue);

        setPstmtCacheMissCount(map, statValue);

        setStartTransactionCount(map, statValue);

        setConnectCount(map, statValue);

        setClobOpenCount(map, statValue);

        setBlobOpenCount(map, statValue);

        setSqlSkipCount(map, statValue);

        setSqlList(map, statValue);

        setKeepAliveCheckCount(map, statValue);

        write(XGsonBuilder.toJson(map));

    }

    private void write(String text) {
        try {
            File dir = Config.dir_logs();
            if (dir.exists() && dir.isDirectory()) {
                List<File> list = new ArrayList<>();
                for (File f : FileUtils.listFilesAndDirs(dir, FalseFileFilter.FALSE, new RegexFileFilter(
                        "^" + FILE_NAME_PREFIX
                                + "[1,2][0,9][0-9][0-9][0,1][0-9][0-3][0-9][0-5][0-9][0-5][0-9][0-5][0-9]"
                                + FILE_NAME_EXTENSION + "$"))) {
                    if (dir != f) {
                        list.add(f);
                    }
                }
                list = list.stream().sorted(Comparator.comparing(File::getName).reversed())
                        .collect(Collectors.toList());
                if (list.size() > MAXSIZE) {
                    for (int i = MAXSIZE; i < list.size(); i++) {
                        File file = list.get(i);
                        FileUtils.forceDelete(file);
                    }
                }
            }
            Path path = dir.toPath().resolve(FILE_NAME_PREFIX + DateTools.compact(new Date()) + FILE_NAME_EXTENSION);
            Files.writeString(path, text, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void setWaitThreadCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getWaitThreadCount() > 0) {
            map.put("waitThreadCount", statValue.getWaitThreadCount());
        }
    }

    private void setNotEmptyWaitCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getNotEmptyWaitCount() > 0) {
            map.put("notEmptyWaitCount", statValue.getNotEmptyWaitCount());
        }
    }

    private void setNotEmptyWaitMillis(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getNotEmptyWaitMillis() > 0) {
            map.put("notEmptyWaitMillis", statValue.getNotEmptyWaitMillis());
        }
    }

    private void setLogicConnectErrorCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getLogicConnectErrorCount() > 0) {
            map.put("logicConnectErrorCount", statValue.getLogicConnectErrorCount());
        }
    }

    private void setPhysicalConnectCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getPhysicalConnectCount() > 0) {
            map.put("physicalConnectCount", statValue.getPhysicalConnectCount());
        }
    }

    private void setPhysicalCloseCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getPhysicalCloseCount() > 0) {
            map.put("physicalCloseCount", statValue.getPhysicalCloseCount());
        }
    }

    private void setPhysicalConnectErrorCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getPhysicalConnectErrorCount() > 0) {
            map.put("physicalConnectErrorCount", statValue.getPhysicalConnectErrorCount());
        }
    }

    private void setExecuteCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getExecuteCount() > 0) {
            map.put("executeCount", statValue.getExecuteCount());
        }
    }

    private void setErrorCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getErrorCount() > 0) {
            map.put("errorCount", statValue.getErrorCount());
        }
    }

    private void setCommitCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getCommitCount() > 0) {
            map.put("commitCount", statValue.getCommitCount());
        }
    }

    private void setRollbackCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getRollbackCount() > 0) {
            map.put("rollbackCount", statValue.getRollbackCount());
        }
    }

    private void setPstmtCacheHitCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getPstmtCacheHitCount() > 0) {
            map.put("pstmtCacheHitCount", statValue.getPstmtCacheHitCount());
        }
    }

    private void setPstmtCacheMissCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getPstmtCacheMissCount() > 0) {
            map.put("pstmtCacheMissCount", statValue.getPstmtCacheMissCount());
        }
    }

    private void setStartTransactionCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getStartTransactionCount() > 0) {
            map.put("startTransactionCount", statValue.getStartTransactionCount());
            map.put("transactionHistogram", rtrim(statValue.getTransactionHistogram()));
        }
    }

    private void setConnectCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getConnectCount() > 0) {
            map.put("connectionHoldTimeHistogram", rtrim(statValue.getConnectionHoldTimeHistogram()));
        }
    }

    private void setClobOpenCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getClobOpenCount() > 0) {
            map.put("clobOpenCount", statValue.getClobOpenCount());
        }
    }

    private void setBlobOpenCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getBlobOpenCount() > 0) {
            map.put("blobOpenCount", statValue.getBlobOpenCount());
        }
    }

    private void setSqlSkipCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getSqlSkipCount() > 0) {
            map.put("sqlSkipCount", statValue.getSqlSkipCount());
        }
    }

    private void setKeepAliveCheckCount(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        if (statValue.getKeepAliveCheckCount() > 0) {
            map.put("keepAliveCheckCount", statValue.getKeepAliveCheckCount());
        }
    }

    private void setSqlList(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        ArrayList<Map<String, Object>> sqlList = new ArrayList<>();
        if (!statValue.getSqlList().isEmpty()) {
            for (JdbcSqlStatValue sqlStat : statValue.getSqlList()) {
                Map<String, Object> sqlStatMap = new LinkedHashMap<>();
                sqlStatMap.put("sql", sqlStat.getSql());

                setSqlListSetExecuteCount(sqlStatMap, sqlStat);

                setSqlListSetExecuteErrorCount(sqlStatMap, sqlStat);

                setSqlListSetRunningCount(sqlStatMap, sqlStat);

                setSqlListSetConcurrentMax(sqlStatMap, sqlStat);

                setSqlListSetFetchRowCount(sqlStatMap, sqlStat);

                setSqlListSetUpdateCount(sqlStatMap, sqlStat);

                setSqlListSetInTransactionCount(sqlStatMap, sqlStat);

                setSqlListSetClobOpenCount(sqlStatMap, sqlStat);

                setSqlListSetBlobOpenCount(sqlStatMap, sqlStat);

                sqlList.add(sqlStatMap);
            }

            map.put("sqlList", sqlList);
        }
    }

    private void setSqlListSetBlobOpenCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        if (sqlStat.getBlobOpenCount() > 0) {
            sqlStatMap.put("blobOpenCount", sqlStat.getBlobOpenCount());
        }
    }

    private void setSqlListSetClobOpenCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        if (sqlStat.getClobOpenCount() > 0) {
            sqlStatMap.put("clobOpenCount", sqlStat.getClobOpenCount());
        }
    }

    private void setSqlListSetInTransactionCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        if (sqlStat.getInTransactionCount() > 0) {
            sqlStatMap.put("inTransactionCount", sqlStat.getInTransactionCount());
        }
    }

    private void setSqlListSetUpdateCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        if (sqlStat.getUpdateCount() > 0) {
            sqlStatMap.put("updateCount", sqlStat.getUpdateCount());
            sqlStatMap.put("updateCountMax", sqlStat.getUpdateCountMax());
            sqlStatMap.put("updateHistogram", rtrim(sqlStat.getUpdateHistogram()));
        }
    }

    private void setSqlListSetFetchRowCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        if (sqlStat.getFetchRowCount() > 0) {
            sqlStatMap.put("fetchRowCount", sqlStat.getFetchRowCount());
            sqlStatMap.put("fetchRowCountMax", sqlStat.getFetchRowCountMax());
            sqlStatMap.put("fetchRowHistogram", rtrim(sqlStat.getFetchRowHistogram()));
        }
    }

    private void setSqlListSetConcurrentMax(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        int concurrentMax = sqlStat.getConcurrentMax();
        if (concurrentMax > 0) {
            sqlStatMap.put("concurrentMax", concurrentMax);
        }
    }

    private void setSqlListSetRunningCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        int runningCount = sqlStat.getRunningCount();
        if (runningCount > 0) {
            sqlStatMap.put("runningCount", runningCount);
        }
    }

    private void setSqlListSetExecuteErrorCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        long executeErrorCount = sqlStat.getExecuteErrorCount();
        if (executeErrorCount > 0) {
            sqlStatMap.put("executeErrorCount", executeErrorCount);
        }
    }

    private void setSqlListSetExecuteCount(Map<String, Object> sqlStatMap, JdbcSqlStatValue sqlStat) {
        if (sqlStat.getExecuteCount() > 0) {
            sqlStatMap.put("executeCount", sqlStat.getExecuteCount());
            sqlStatMap.put("executeMillisMax", sqlStat.getExecuteMillisMax());
            sqlStatMap.put("executeMillisTotal", sqlStat.getExecuteMillisTotal());
            sqlStatMap.put("executeHistogram", rtrim(sqlStat.getExecuteHistogram()));
            sqlStatMap.put("executeAndResultHoldHistogram", rtrim(sqlStat.getExecuteAndResultHoldHistogram()));
        }
    }

    private void setConnection(DruidDataSourceStatValue statValue, Map<String, Object> map) {
        if (statValue.getActivePeak() > 0) {
            map.put("activePeak", statValue.getActivePeak());
            map.put("activePeakTime", statValue.getActivePeakTime());
        }
        map.put("poolingCount", statValue.getPoolingCount());
        if (statValue.getPoolingPeak() > 0) {
            map.put("poolingPeak", statValue.getPoolingPeak());
            map.put("poolingPeakTime", statValue.getPoolingPeakTime());
        }
        map.put("connectCount", statValue.getConnectCount());
        map.put("closeCount", statValue.getCloseCount());
    }

    private void setBase(Map<String, Object> map, DruidDataSourceStatValue statValue) {
        map.put("url", statValue.getUrl());
        map.put("dbType", statValue.getDbType());
        map.put("name", statValue.getName());
        map.put("activeCount", statValue.getActiveCount());
    }

}