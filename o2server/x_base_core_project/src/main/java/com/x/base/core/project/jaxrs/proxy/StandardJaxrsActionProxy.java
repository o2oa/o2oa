package com.x.base.core.project.jaxrs.proxy;

import com.x.base.core.project.Context;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class StandardJaxrsActionProxy implements MethodInterceptor {
    private Enhancer enhancer = new Enhancer();
    private Context context;

    public StandardJaxrsActionProxy(Context context) {
        this.context = context;
    }

    public Object getProxy(Class clazz){
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept( Object o, Method method, Object[] objects, MethodProxy methodProxy ) throws Throwable {
        Object result = methodProxy.invokeSuper(o, objects);
        try{
            //尝试记录审计日志
            if( Config.logLevel().audit().enable() ){
                tryToRecordAuditLog( o, method, objects, methodProxy );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据调用方法的注释判断该类的操作是否需要记录到审计日志
     * 如果需要记录，则执行审计日志记录方法
     * @param o
     * @param method
     * @param objects
     * @param methodProxy
     * @throws ClassNotFoundException
     */
    private void tryToRecordAuditLog(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws ClassNotFoundException {
        //分析调用过程，记录审计日志
        Annotation[] annotations_auditLog = method.getAnnotationsByType(AuditLog.class);
        //该方法是否有AuditLog注解，如果有，则需要记录审计日志
        if( ArrayUtils.isNotEmpty( annotations_auditLog )){
            //获取操作名称，首选AuditLog的value属性属性，如果没有，则选择JaxrsMethodDescribe的value属性
            String operationName = getOperationName( method, annotations_auditLog );
            //有AuditLog注解，说明需要记录审计日志
            doRecordAuditLog( method, objects, operationName );
        }
    }

    /**
     * 获取方法的操作名称，首选AuditLog的value属性属性，如果没有，则选择JaxrsMethodDescribe的value属性
     * @param method
     * @param annotations_auditLog
     * @return
     */
    private String getOperationName( Method method, Annotation[] annotations_auditLog ) {
        String operationName = ((AuditLog)annotations_auditLog[0]).operation();
        Annotation[] annotations_jaxrsMethodDescribe = null;
        if( StringUtils.isEmpty(operationName)){
            //取JaxrsMethodDescribe
            annotations_jaxrsMethodDescribe = method.getAnnotationsByType(JaxrsMethodDescribe.class);
            if( ArrayUtils.isNotEmpty( annotations_jaxrsMethodDescribe )){
                operationName = ((JaxrsMethodDescribe)annotations_auditLog[0]).value();
            }
        }
        return operationName;
    }

    /**
     * 记录审计日志执行方法
     * @param method
     * @param objects
     * @param operationName
     */
    private void doRecordAuditLog( Method method, Object[] objects, String operationName ) throws ClassNotFoundException {
        if( StringUtils.isEmpty(operationName)){
            operationName = method.getName();
        }
        int parameterCount = method.getParameterCount();
        Class<?>[] parameterClasses = method.getParameterTypes();
        EffectivePerson effectivePerson = null;
        if( parameterCount > 0 && ArrayUtils.isNotEmpty( parameterClasses ) && ArrayUtils.isNotEmpty( objects ) ){
            //解析出参数effectivePerson
            int effectivePersonParamIndex = 99;
            for( int i = 0 ; i< parameterClasses.length; i++ ){
                if(StringUtils.equals( "com.x.base.core.project.http.EffectivePerson", parameterClasses[i].getName() )){
                    effectivePersonParamIndex = i;
                    break;
                }
            }
            if( effectivePersonParamIndex < 99 ){
                effectivePerson = (EffectivePerson) objects[effectivePersonParamIndex];
                Logger logger = LoggerFactory.getLogger(Class.forName( method.getDeclaringClass().getName() ));
                Audit audit = logger.audit(effectivePerson);
                audit.log(null, operationName);
            }
        }
    }
}
