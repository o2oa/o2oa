package com.x.query.core.express.statement;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据中心 Statement 原生 SQL 安全检查.
 *
 * <p>
 * Statement 的 sql 和 sqlScript 最终原样交给 EntityManager.createNativeQuery() 执行, 数据库自带的文件函数
 * 会被用来读取服务器任意文件, 例如 H2 的 FILE_READ, FILE_WRITE, CSVREAD, CSVWRITE, RUNSCRIPT, CREATE ALIAS,
 * MySQL 的 LOAD_FILE, LOAD DATA, PostgreSQL 的 pg_read_file, lo_import, Oracle 的 UTL_FILE, SQLServer 的
 * xp_cmdshell. 数据库账号是管理员时这些函数全部可用, 单纯依赖数据库权限无法阻止.
 *
 * <p>
 * 本类在执行之前做一次"默认拒绝"的检查, 规则如下.
 * <ol>
 * <li>语句只能以 SELECT, WITH, INSERT, UPDATE, DELETE 开头, DDL, DCL, CALL, SET, SHOW 等一律拒绝.</li>
 * <li>不允许用分号一次提交多条语句.</li>
 * <li>查询语句中不允许出现 INTO, H2 的 SELECT INTO 会把结果写到文件或者新建表.</li>
 * <li>SQL 中出现的每一个函数都必须在 {@link #ALLOWED_FUNCTIONS} 白名单之内, 不在白名单内的函数, 包括未知
 * 函数一律拒绝, 因此不需要维护一份不断膨胀的危险函数清单.</li>
 * <li>对不是以函数调用形式出现的危险写法, 例如 LOAD DATA, OPENROWSET, LINK SCHEMA, RUNSCRIPT 再做一次
 * 关键字拦截, 作为白名单之外的第二道防线.</li>
 * </ol>
 *
 * <p>
 * 检查在剥离注释, 字符串常量和标识符引用之后进行, 避免用注释或者常量把关键字拆开绕过检查, 未闭合的注释和
 * 引号直接拒绝.
 *
 * <p>
 * 白名单写在代码里而不是配置文件里, 因为配置缺失等同于不设防. 业务确实需要新的数据库函数时, 应当在确认该
 * 函数不触及文件系统和命令执行之后, 补充到 {@link #ALLOWED_FUNCTIONS}.
 */
public final class SqlSafetyChecker {

    /** 允许作为语句起始的关键字, 其余的 DDL, DCL, CALL, SET, SHOW 等一律拒绝. */
    private static final Set<String> ALLOWED_STATEMENT_STARTS = words("SELECT", "WITH", "INSERT", "UPDATE", "DELETE");

    /**
     * 允许调用的函数白名单, 只包含聚合, 空值和条件, 字符串, 正则, 类型转换, 日期时间, 数学, 窗口, JSON,
     * 编码和哈希这类展示型函数, 覆盖 H2, MySQL, Oracle, PostgreSQL, SQLServer, 达梦, 人大金仓的常用写法.
     */
    private static final Set<String> ALLOWED_FUNCTIONS = words(//
            // 聚合
            "COUNT", "SUM", "AVG", "MIN", "MAX", "MEDIAN", "GROUPING", "STDDEV", "STDDEV_POP", "STDDEV_SAMP",
            "VARIANCE", "VAR_POP", "VAR_SAMP", "BIT_AND", "BIT_OR", "BIT_XOR", "GROUP_CONCAT", "STRING_AGG",
            "LISTAGG", "WM_CONCAT", "ARRAY_AGG", "ARRAY_TO_STRING", "UNNEST", "JSON_ARRAYAGG", "JSON_OBJECTAGG",
            "XMLAGG", "PERCENTILE_CONT", "PERCENTILE_DISC", "APPROX_COUNT_DISTINCT", //
            // 空值和条件
            "COALESCE", "NVL", "NVL2", "IFNULL", "ISNULL", "NULLIF", "DECODE", "IF", "GREATEST", "LEAST", //
            // 字符串
            "CONCAT", "CONCAT_WS", "SUBSTRING", "SUBSTR", "SUBSTRING_INDEX", "LEFT", "RIGHT", "LENGTH",
            "CHAR_LENGTH", "CHARACTER_LENGTH", "OCTET_LENGTH", "BIT_LENGTH", "DATA_LENGTH", "LOWER", "UPPER",
            "LCASE", "UCASE", "TRIM", "LTRIM", "RTRIM", "BTRIM", "LPAD", "RPAD", "REPLACE", "REVERSE", "INSTR",
            "LOCATE", "POSITION", "ASCII", "CHR", "UNICODE", "SPACE", "REPEAT", "REPLICATE", "ELT", "FIELD",
            "FORMAT", "QUOTE", "SOUNDEX", "DIFFERENCE", "TRANSLATE", "LEVENSHTEIN", "TO_BASE64", "FROM_BASE64", //
            // 正则
            "REGEXP_LIKE", "REGEXP_REPLACE", "REGEXP_SUBSTR", "REGEXP_INSTR", "REGEXP_COUNT", //
            // 类型转换
            "CAST", "CONVERT", "TRY_CAST", "TRY_CONVERT", "TRY_TO_NUMBER", "TRY_TO_DATE", "TRY_TO_TIMESTAMP",
            "VALIDATE_CONVERSION", "TO_CHAR", "TO_NUMBER", "TO_DATE", "TO_TIMESTAMP", "TO_TIMESTAMP_TZ",
            "TO_CLOB", "TO_BLOB", "TO_BINARY_FLOAT", "TO_BINARY_DOUBLE", "FORMATDATETIME", "PARSEDATETIME", //
            // 日期和时间
            "EXTRACT", "YEAR", "QUARTER", "MONTH", "MONTHNAME", "WEEK", "WEEKDAY", "DAY", "DAYNAME", "DAYOFMONTH",
            "DAYOFWEEK", "DAYOFYEAR", "HOUR", "MINUTE", "SECOND", "MILLISECOND", "MICROSECOND", "NANOSECOND",
            "EPOCH", "LAST_DAY", "MAKEDATE", "MAKETIME", "PERIOD_ADD", "PERIOD_DIFF", "TO_DAYS", "FROM_DAYS",
            "FROM_UNIXTIME", "UNIX_TIMESTAMP", "DATE", "TIME", "TIMESTAMP", "DATETIME", "DATEDIFF", "DATEADD",
            "DATESUB", "DATE_ADD", "DATE_SUB", "DATE_FORMAT", "TIME_FORMAT", "DATETIME_FORMAT", "DATETIMEFROMPARTS",
            "TIMESTAMPDIFF", "TIMESTAMPADD", "ADD_MONTHS", "MONTHS_BETWEEN", "NEXT_DAY", "EOMONTH", "TRUNC",
            "TRUNCATE", "NOW", "TODAY", "SYSDATE", "SYSTIMESTAMP", "CURRENT_DATE", "CURRENT_TIME",
            "CURRENT_TIMESTAMP", "LOCALTIME", "LOCALTIMESTAMP", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP",
            "FROMTODATETIMEZONE", //
            // 数学
            "ABS", "CEIL", "CEILING", "FLOOR", "ROUND", "MOD", "POW", "POWER", "SQRT", "CBRT", "SIGN", "RAND",
            "RANDOM", "EXP", "LN", "LOG", "LOG2", "LOG10", "SIN", "COS", "TAN", "COT", "ASIN", "ACOS", "ATAN",
            "ATAN2", "SINH", "COSH", "TANH", "DEGREES", "RADIANS", "WIDTH_BUCKET", //
            // 窗口
            "ROW_NUMBER", "RANK", "DENSE_RANK", "PERCENT_RANK", "CUME_DIST", "NTILE", "LAG", "LEAD", "FIRST_VALUE",
            "LAST_VALUE", "NTH_VALUE", //
            // JSON
            "JSON_VALUE", "JSON_QUERY", "JSON_EXTRACT", "JSON_EXISTS", "JSON_ARRAY", "JSON_OBJECT", //
            // 编码和哈希,不触及文件系统
            "HEX", "UNHEX", "BIN", "OCT", "MD5", "SHA", "SHA1", "SHA256", "SHA512", "HASH", "BASE64",
            "BASE64_BINARY", "RAWTOHEX", "HEXTORAW", "UID", "UUID", "GEN_RANDOM_UUID", "RANDOM_UUID", "NEWID",
            "SYS_GUID", "ORA_HASH", "TYPEOF", "IDENTITY", "LAST_INSERT_ID", "SCOPE_IDENTITY");

    /** 紧跟左括号但不是函数调用的词, 包含 SQL 关键字和 CAST(X AS VARCHAR(20)), DECIMAL(10,2) 中的类型名. */
    private static final Set<String> NOT_FUNCTION_WORDS = words(//
            // 结构和关键字
            "SELECT", "FROM", "WHERE", "GROUP", "GROUPING", "ORDER", "BY", "HAVING", "LIMIT", "OFFSET", "FETCH",
            "UNION", "EXCEPT", "MINUS", "INTERSECT", "AND", "OR", "NOT", "IN", "EXISTS", "ANY", "ALL", "SOME", "AS",
            "ON", "USING", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "CROSS", "NATURAL", "CASE", "WHEN",
            "THEN", "ELSE", "END", "IS", "NULL", "LIKE", "ILIKE", "RLIKE", "BETWEEN", "DISTINCT", "VALUES", "INTO",
            "SET", "ASC", "DESC", "WITH", "FILTER", "OVER", "PARTITION", "ROWS", "RANGE", "UNBOUNDED", "PRECEDING",
            "FOLLOWING", "CURRENT", "INTERVAL", "ESCAPE", "SIMILAR", "COLLATE", "DEFAULT", "FOR", "NULLS", "FIRST",
            "LAST", "SEPARATOR", "ROLLUP", "CUBE", "LATERAL", "RECURSIVE", "RETURNING", "TOP", "TIES", "WINDOW", //
            // 数据类型
            "VARCHAR", "VARCHAR2", "CHAR", "NCHAR", "NVARCHAR", "NVARCHAR2", "CLOB", "NCLOB", "BLOB", "BINARY",
            "VARBINARY", "BINARY_FLOAT", "BINARY_DOUBLE", "INT", "INTEGER", "TINYINT", "SMALLINT", "MEDIUMINT",
            "BIGINT", "DECIMAL", "DEC", "NUMERIC", "FIXED", "FLOAT", "DOUBLE", "REAL", "BOOLEAN", "BOOL", "BIT",
            "DATE", "TIME", "DATETIME", "SMALLDATETIME", "DATETIME2", "TIMESTAMP", "TIMESTAMPTZ", "TIMESTAMP_LTZ",
            "TIMESTAMP_TZ", "TEXT", "TINYTEXT", "MEDIUMTEXT", "LONGTEXT", "TINYBLOB", "MEDIUMBLOB",
            "LONGBLOB", "BYTEA", "YEAR", "SERIAL", "BIGSERIAL", "UUID", "JSON", "JSONB", "XML", "STRING", "NUMBER",
            "RAW", "LONG", "IMAGE", "MONEY", "SMALLMONEY", "SQL_VARIANT", "UNIQUEIDENTIFIER", "ROWVERSION", "ARRAY");

    /** 不是以函数调用形式出现的危险写法, 白名单之外的第二道防线. */
    private static final Pattern[] DENIED_PATTERNS = new Pattern[] { //
            Pattern.compile("\\bINTO\\s+(OUTFILE|DUMPFILE)\\b"), //
            Pattern.compile("\\bLOAD\\s+(LOCAL\\s+)?DATA\\b"), //
            Pattern.compile("\\bLOAD\\s+INFILE\\b"), //
            Pattern.compile("\\bBULK\\s+INSERT\\b"), //
            Pattern.compile("\\b(OPENROWSET|OPENQUERY|OPENDATASOURCE)\\b"), //
            Pattern.compile("\\b(RUNSCRIPT|SCRIPTFILE)\\b"), //
            Pattern.compile("\\bLINK\\s+SCHEMA\\b"), //
            Pattern.compile("\\b(FILE_READ|FILE_WRITE|FILE_LENGTH|FILE_EXISTS|CSVREAD|CSVWRITE|LOAD_FILE)\\b"), //
            Pattern.compile("\\b(DBLINK|DBLINK_[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\b(PG_[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\bLO_(IMPORT|EXPORT)\\b"), //
            Pattern.compile("\\b(UTL_[A-Z0-9_]+|DBMS_[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\b(XP_[A-Z0-9_]+|SP_OA[A-Z0-9_]+)\\b"), //
            Pattern.compile("\\b(SLEEP|BENCHMARK)\\b"), //
            Pattern.compile("\\bWAITFOR\\s+DELAY\\b"), //
            Pattern.compile("\\b(CREATE|ALTER|DROP)\\s+(ALIAS|AGGREGATE|ROLE|USER|SCHEMA|LINKED|FUNCTION|PROCEDURE|TRIGGER|SEQUENCE|VIEW|MATERIALIZED|TABLE|INDEX)\\b"), //
            Pattern.compile("\\b(GRANT|REVOKE|SHUTDOWN)\\b"), //
            Pattern.compile("\\b(CALL|EXEC|EXECUTE)\\s+[A-Z_(]"), //
            Pattern.compile("\\bSET\\s+@"), //
            Pattern.compile("\\bINTO\\s+@") };

    /** 标识符紧跟左括号时视为函数调用. */
    private static final Pattern FUNCTION_CALL_PATTERN = Pattern.compile("([A-Z_][A-Z0-9_$]*)\\s*\\(");

    private static final Pattern INSERT_TABLE_PATTERN = Pattern.compile("(\\bINTO\\s+)[A-Z0-9_$.]+(?=\\s*\\()");

    private static final Pattern STATEMENT_START_PATTERN = Pattern.compile("^[A-Z_][A-Z0-9_$]*");

    private static final Pattern QUERY_PATTERN = Pattern.compile("^SELECT\\b|^WITH\\b");

    private static final Pattern INTO_PATTERN = Pattern.compile("\\bINTO\\b");

    private static final Pattern WHITE_SPACE_PATTERN = Pattern.compile("\\s+");

    private SqlSafetyChecker() {
        // nothing
    }

    /**
     * 检查一条即将交给 createNativeQuery 执行的原生 SQL.
     *
     * @param sql 参数已经替换成 ?N 占位符之后的最终 SQL 文本
     * @throws Exception 检查不通过
     */
    public static void check(String sql) throws Exception {
        if (StringUtils.isBlank(sql)) {
            return;
        }
        String normalized = normalize(sql);
        if (normalized.isEmpty()) {
            return;
        }
        checkStatementType(normalized);
        checkDeniedClause(normalized);
        checkFunction(normalized);
    }

    private static void checkStatementType(String normalized) throws Exception {
        String text = normalized;
        while (text.startsWith("(")) {
            text = text.substring(1).trim();
        }
        Matcher matcher = STATEMENT_START_PATTERN.matcher(text);
        if (!matcher.find()) {
            throw new ExceptionSqlUnsafe("无法识别语句类型.");
        }
        String start = matcher.group();
        if (!ALLOWED_STATEMENT_STARTS.contains(start)) {
            throw new ExceptionSqlUnsafe("不允许的关键字 \"" + start + "\".");
        }
        // H2 的 SELECT INTO 把查询结果写到文件或者新建表, 只有 INSERT INTO 是合法的
        if (QUERY_PATTERN.matcher(start).find() && INTO_PATTERN.matcher(normalized).find()) {
            throw new ExceptionSqlUnsafe("SELECT INTO is not allowed.");
        }
    }

    private static void checkDeniedClause(String normalized) throws Exception {
        for (Pattern pattern : DENIED_PATTERNS) {
            Matcher matcher = pattern.matcher(normalized);
            if (matcher.find()) {
                throw new ExceptionSqlUnsafe("不允许的关键字 \"" + matcher.group().trim() + "\".");
            }
        }
    }

    private static void checkFunction(String normalized) throws Exception {
        // INSERT INTO T (XID, XNAME) 中的表名紧跟左括号但不是函数调用, 先替换成占位符
        String text = INSERT_TABLE_PATTERN.matcher(normalized).replaceAll("$1#");
        Matcher matcher = FUNCTION_CALL_PATTERN.matcher(text);
        while (matcher.find()) {
            String function = matcher.group(1);
            if (NOT_FUNCTION_WORDS.contains(function)) {
                continue;
            }
            if (!ALLOWED_FUNCTIONS.contains(function)) {
                throw new ExceptionSqlUnsafe("function " + function + " is not allowed.");
            }
        }
    }

    /**
     * 剥离注释和字符串常量并转换成大写, 避免用注释和常量把关键字拆开来绕过检查. 双引号和反引号是标识符
     * 引用而不是常量, 其内容保留下来参与检查.
     *
     * @param sql 原始 SQL
     * @return 归一化之后的 SQL, 已去除首尾空白并把连续空白合并为单个空格
     * @throws Exception 存在未闭合的注释或者引号
     */
    private static String normalize(String sql) throws Exception {
        char[] chars = sql.toCharArray();
        StringBuilder buffer = new StringBuilder(chars.length);
        int index = 0;
        while (index < chars.length) {
            char c = chars[index];
            if (('/' == c) && ((index + 1) < chars.length) && ('*' == chars[index + 1])) {
                // MySQL 的 /*!...*/ 会把注释内的 SQL 当作正常语句执行, 属于可执行注释, 直接拒绝
                if (((index + 2) < chars.length) && ('!' == chars[index + 2])) {
                    throw new ExceptionSqlUnsafe("executable comment is not allowed.");
                }
                int end = indexOfSequence(chars, "*/", index + 2);
                if (-1 == end) {
                    throw new ExceptionSqlUnsafe("unterminated block comment.");
                }
                buffer.append(' ');
                index = end + 2;
                continue;
            }
            if (('-' == c) && ((index + 1) < chars.length) && ('-' == chars[index + 1])) {
                buffer.append(' ');
                index = skipToLineEnd(chars, index);
                continue;
            }
            if ('\'' == c) {
                // 字符串常量是数据不是代码, 整体用空格替换, 常量内的关键字不参与检查
                buffer.append(' ');
                index = indexOfClosingQuote(chars, c, index) + 1;
                continue;
            }
            if (('"' == c) || ('`' == c)) {
                // 双引号和反引号是标识符引用, H2 中 SELECT "FILE_READ"('/etc/passwd', NULL)
                // 和不加引号的 FILE_READ 调用效果完全一样, 所以引号内的内容要保留下来一起检查
                int end = indexOfClosingQuote(chars, c, index);
                buffer.append(chars, index + 1, end - index - 1);
                index = end + 1;
                continue;
            }
            buffer.append(c);
            index++;
        }
        return WHITE_SPACE_PATTERN.matcher(buffer.toString().toUpperCase(Locale.ROOT).trim()).replaceAll(" ").trim();
    }

    private static int skipToLineEnd(char[] chars, int from) {
        for (int i = from; i < chars.length; i++) {
            if (('\n' == chars[i]) || ('\r' == chars[i])) {
                return i;
            }
        }
        return chars.length;
    }

    private static int indexOfSequence(char[] chars, String target, int from) {
        int offset = new String(chars, from, chars.length - from).indexOf(target);
        return (-1 == offset) ? -1 : offset + from;
    }

    /**
     * 返回成对引号中结束引号的下标, 支持连续两个引号表示引号自身以及 MySQL 的反斜杠转义.
     *
     * @param chars  字符数组
     * @param quote  引号
     * @param from   开始引号的下标
     * @return 结束引号的下标
     * @throws Exception 引号未闭合
     */
    private static int indexOfClosingQuote(char[] chars, char quote, int from) throws Exception {
        for (int i = from + 1; i < chars.length; i++) {
            char c = chars[i];
            if ((('\'' == quote) && ('\\' == c))) {
                i++;
                continue;
            }
            if (c == quote) {
                if (((i + 1) < chars.length) && (chars[i + 1] == quote)) {
                    i++;
                    continue;
                }
                return i + 1;
            }
        }
        throw new ExceptionSqlUnsafe("unterminated quoted string.");
    }

    private static Set<String> words(String... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }
}
