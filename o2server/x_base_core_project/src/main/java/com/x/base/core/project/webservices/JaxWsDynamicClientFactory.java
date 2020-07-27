package com.x.base.core.project.webservices;

public  class JaxWsDynamicClientFactory extends DynamicClientFactory {

    protected JaxWsDynamicClientFactory(Bus bus) {
        super(bus);
    }

    @Override
    protected EndpointImplFactory getEndpointImplFactory() {
        return JaxWsEndpointImplFactory.getSingleton();
    }

    protected boolean allowWrapperOps() {
        return true;
    }

    /**
     * Create a new instance using a specific <tt>Bus</tt>.
     *
     * @param b the <tt>Bus</tt> to use in subsequent operations with the
     *            instance
     * @return the new instance
     */
    public static JaxWsDynamicClientFactory newInstance(Bus b) {
        return new JaxWsDynamicClientFactory(b);
    }

    /**
     * Create a new instance using a default <tt>Bus</tt>.
     *
     * @return the new instance
     * @see CXFBusFactory#getDefaultBus()
     */
    public static JaxWsDynamicClientFactory newInstance() {
        Bus bus = CXFBusFactory.getThreadDefaultBus();
        return new JaxWsDynamicClientFactory(bus);
    }

    /**
     * 覆写父类的该方法<br/>
     * 注：解决此（错误：编码GBK的不可映射字符）问题
     *
     * @return
     */
    @Override
    protected boolean compileJavaSrc(String classPath, List<File> srcList, String dest) {
        org.apache.cxf.common.util.Compiler javaCompiler
                = new org.apache.cxf.common.util.Compiler();

        // 设置编译编码格式（此处为新增代码）
        javaCompiler.setEncoding("UTF-8");

        javaCompiler.setClassPath(classPath);
        javaCompiler.setOutputDir(dest);
        javaCompiler.setTarget("1.6");

        return javaCompiler.compileFiles(srcList);
    }

}
