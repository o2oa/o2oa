package com.x.query.core.express.statement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.core.express.Organization;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;

public class ExecuteTarget {

    public static final Pattern QUESTMARK_PARAMETER_REGEX = Pattern.compile("(\\?\\d+)");
    public static final Pattern NAMED_PARAMETER_REGEX = Pattern.compile("(:(\\w+))");

    private String sql;
    private Map<String, Object> questionMarkParam = new LinkedHashMap<>();
    private Map<String, Object> namedParam = new LinkedHashMap<>();
    private net.sf.jsqlparser.statement.Statement parsedStatement;

    public String getSql() {
        return sql;
    }

    public Map<String, Object> getQuestionMarkParam() {
        return questionMarkParam;
    }

    public Map<String, Object> getNamedParam() {
        return namedParam;
    }

    public net.sf.jsqlparser.statement.Statement getParsedStatement() {
        return parsedStatement;
    }

    public ExecuteTarget(EffectivePerson effectivePerson, Organization organization, String sql,
            Runtime runtime, Map<String, Object> prevNamedParam) throws Exception {
        questionMarkParameter(effectivePerson, organization, runtime, sql);
        this.sql = namedParameterChangeToQuestionMark(effectivePerson, organization, runtime, sql, prevNamedParam);
        parsedStatement = CCJSqlParserUtil.parse(this.sql);
    }

    // 读取 QUESTION MARK PARAMETER
    // 的值,所有形如?1的参数只可能通过前端传递过来,通过runtime读取到.如果没有读到就直接设置为null
    private void questionMarkParameter(EffectivePerson effectivePerson, Organization organization,
            Runtime runtime, String sql) throws Exception {
        Matcher matcher = QUESTMARK_PARAMETER_REGEX.matcher(sql);
        while (matcher.find()) {
            String p = matcher.group(1);
            Object object = getParameterFromRuntime(effectivePerson, organization, p, runtime);
            this.questionMarkParam.put(p, object);
        }
    }

    // 将 NAMED PARAMETER 转换成 QUESTION MARK PARAMETER,在传入前次的PREVNAMEDPARAM中查找,减少运行次数
    private String namedParameterChangeToQuestionMark(EffectivePerson effectivePerson, Organization organization,
            Runtime runtime, String sql, Map<String, Object> prevNamedParam) throws Exception {
        Matcher matcher = NAMED_PARAMETER_REGEX.matcher(sql);
        while (matcher.find()) {
            String p = usableQuestionMark(questionMarkParam);
            String name = matcher.group(2);
            if ((null != prevNamedParam) && prevNamedParam.containsKey(name)) {
                Object object = prevNamedParam.get(name);
                this.namedParam.put(name, object);
                this.questionMarkParam.put(p, object);
            } else {
                Object object = getParameterFromRuntime(effectivePerson, organization, name, runtime);
                this.namedParam.put(name, object);
                this.questionMarkParam.put(p, object);
            }
            sql = StringUtils.replaceOnce(sql, matcher.group(1), p);
        }
        return sql;
    }

    private Object getParameterFromRuntime(EffectivePerson effectivePerson, Organization organization, String name,
            Runtime runtime) throws Exception {
        if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_PERSON)) {
            return effectivePerson.getDistinguishedName();
        }
        if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_IDENTITYLIST)) {
            return organization.identity().listWithPerson(effectivePerson);
        }
        if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_UNITLIST)) {
            return organization.unit().listWithPerson(effectivePerson);
        }
        if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_UNITALLLIST)) {
            return organization.unit().listWithPersonSupNested(effectivePerson);
        }
        if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_GROUPLIST)) {
            return organization.group().listWithPerson(effectivePerson);
        }
        if (StringUtils.equalsIgnoreCase(name, Runtime.PARAMETER_ROLELIST)) {
            return organization.role().listWithPerson(effectivePerson);
        }
        if (runtime.hasParameter(name)) {
            return runtime.getParameter(name);
        }
        return null;
    }

    private String usableQuestionMark(Map<String, Object> map) {
        int p = 1;
        while (map.keySet().contains("?" + p)) {
            p++;
        }
        return "?" + p;
    }
}