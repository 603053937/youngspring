package org.youngspringframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.youngspringframework.core.BeanContainer;
import org.youngspringframework.mvc.RequestProcessorChain;
import org.youngspringframework.mvc.annotation.RequestMapping;
import org.youngspringframework.mvc.annotation.RequestParam;
import org.youngspringframework.mvc.annotation.ResponseBody;
import org.youngspringframework.mvc.processor.RequestProcessor;
import org.youngspringframework.mvc.render.ResultRender;
import org.youngspringframework.mvc.render.impl.JsonResultRender;
import org.youngspringframework.mvc.render.impl.ResourceNotFoundResultRender;
import org.youngspringframework.mvc.render.impl.ViewResultRender;
import org.youngspringframework.mvc.type.ControllerMethod;
import org.youngspringframework.mvc.type.RequestPathInfo;
import org.youngspringframework.util.ConverterUtil;
import org.youngspringframework.util.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Controller请求处理器
 * 功能：
 * 1. 针对特定请求，选择匹配的Controller方法进行处理
 * 2. 解析请求里的参数及其对应的值，并赋值给Controller方法的参数
 * 3. 选择合适的Render，为后续请求处理结果的渲染做准备
 */
@Slf4j
public class ControllerRequestProcessor implements RequestProcessor {
    //IOC容器
    private BeanContainer beanContainer;
    //http请求和controller方法的映射集合
    private Map<RequestPathInfo, ControllerMethod> pathControllerMethodMap = new ConcurrentHashMap<>();

    /**
     * 依靠容器的能力，建立起请求路径、请求方法与Controller方法实例的映射
     */
    public ControllerRequestProcessor() {
        // beanContainer在DispatcherServlet中已被初始化完毕，具备完整的IOC和AOP的bean
        this.beanContainer = BeanContainer.getInstance();
        // 获取被RequestMapping注解标记的所有class对象的集合
        Set<Class<?>> requestMappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        // 解析被RequestMapping标注的类并将获取到的信息封装成RequestPathInfo实例和ControllerMethod实例，放置到映射表里
        initPathControllerMethodMap(requestMappingSet);
    }

    private void initPathControllerMethodMap(Set<Class<?>> requestMappingSet) {
        if (ValidationUtil.isEmpty(requestMappingSet)) {
            return;
        }
        //1.遍历所有被@RequestMapping标记的类，获取类上面该注解的属性值作为一级路径
        for (Class<?> requestMappingClass : requestMappingSet) {
            // 获取注解标记实例
            RequestMapping requestMapping = requestMappingClass.getAnnotation(RequestMapping.class);
            // 获取注解的value 即 一级路径
            String basePath = requestMapping.value();
            // 确保路径以'/'开头
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }
            //2.遍历类里所有被@RequestMapping标记的方法，获取方法上面该注解的属性值，作为二级路径
            // 获取方法数组
            Method[] methods = requestMappingClass.getDeclaredMethods();
            if (ValidationUtil.isEmpty(methods)) {
                continue;
            }
            for (Method method : methods) {
                // 找出被RequestMapping标记的方法
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    // 获取注解标记实例
                    RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                    // 提取二级路径
                    String methodPath = methodRequest.value();
                    // 确保前缀为'/'
                    if (!methodPath.startsWith("/")) {
                        methodPath = "/" + basePath;
                    }
                    // 拼接为请求路径
                    String url = basePath + methodPath;
                    //3.解析方法里被@RequestParam标记的参数，
                    // 获取该注解的属性值，作为参数名，
                    // 获取被标记的参数的数据类型，建立参数名和参数类型的映射
                    Map<String, Class<?>> methodParams = new HashMap<>();
                    // 获取method实例中的参数
                    Parameter[] parameters = method.getParameters();
                    if (!ValidationUtil.isEmpty(parameters)) {
                        for (Parameter parameter : parameters) {
                            // 获取被RequestParam标记的参数
                            RequestParam param = parameter.getAnnotation(RequestParam.class);
                            //目前暂定为Controller方法里面所有的参数都需要@RequestParam注解
                            if (param == null) {
                                throw new RuntimeException("The parameter must have @RequestParam");
                            }
                            // 建立参数名和参数类型的映射
                            methodParams.put(param.value(), parameter.getType());
                        }
                    }
                    //4.将获取到的信息封装成RequestPathInfo实例和ControllerMethod实例，放置到映射表里
                    // 获取请求方法，转换为String类型
                    String httpMethod = String.valueOf(methodRequest.method());
                    // 封装到RequestPathInfo
                    RequestPathInfo requestPathInfo = new RequestPathInfo(httpMethod, url);
                    // 判断映射中是否已经存在对应的key
                    if (this.pathControllerMethodMap.containsKey(requestPathInfo)) {
                        // 若存在则提出报警信息
                        log.warn("duplicate url:{} registration，current class {} method{} will override the former one",
                                requestPathInfo.getHttpPath(), requestMappingClass.getName(), method.getName());
                    }
                    // 封装成ControllerMethod实例
                    ControllerMethod controllerMethod = new ControllerMethod(requestMappingClass, method, methodParams);
                    this.pathControllerMethodMap.put(requestPathInfo, controllerMethod);
                }
            }
        }

    }

    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1.解析HttpSevletRequest的请求方法,请求路径
        String method = requestProcessorChain.getRequestMethod();
        String path = requestProcessorChain.getRequestPath();
        // 根据请求方法，请求路径封装成RequestPathInfo，获取对应的ControllerMethod实例
        ControllerMethod controllerMethod = this.pathControllerMethodMap.get(new RequestPathInfo(method, path));
        if (controllerMethod == null) {
            // 找不到处理方法的结果渲染器
            requestProcessorChain.setResultRender(new ResourceNotFoundResultRender(method, path));
            return false;
        }
        //2.解析请求参数，并传递给获取到的ControllerMethod实例去执行
        Object result = invokeControllerMethod(controllerMethod, requestProcessorChain.getRequest());
        //3.根据处理的结果，选择对应的render进行渲染
        setResultRender(result, controllerMethod, requestProcessorChain);
        return true;
    }

    /**
     * 根据不同情况设置不同的渲染器
     */
    private void setResultRender(Object result, ControllerMethod controllerMethod, RequestProcessorChain requestProcessorChain) {
        if (result == null) {
            return;
        }
        ResultRender resultRender;
        // 判断是否有ResponseBody注解 ，返回的是否是Json数据
        boolean isJson = controllerMethod.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            resultRender = new JsonResultRender(result);
        } else {
            resultRender = new ViewResultRender(result);
        }
        requestProcessorChain.setResultRender(resultRender);
    }

    private Object invokeControllerMethod(ControllerMethod controllerMethod, HttpServletRequest request) {
        //1.从请求里获取GET或者POST的参数名及其对应的值
        // 用于存储从请求中解析出的参数名和对应的值
        Map<String, String> requestParamMap = new HashMap<>();
        //GET，POST方法的请求参数获取方式
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> parameter : parameterMap.entrySet()) {
            if (!ValidationUtil.isEmpty(parameter.getValue())) {
                //只支持一个参数对应一个值的形式，value只获取第一个值
                requestParamMap.put(parameter.getKey(), parameter.getValue()[0]);
            }
        }
        //2.根据获取到的请求参数名及其对应的值，以及controllerMethod里面的参数和类型的映射关系，去实例化出方法对应的参数
        List<Object> methodParams = new ArrayList<>();
        // 方法参数名称以及对应的参数类型
        Map<String, Class<?>> methodParamMap = controllerMethod.getMethodParameters();
        for (String paramName : methodParamMap.keySet()) {
            // 根据参数名获取参数实例类型
            Class<?> type = methodParamMap.get(paramName);
            // 根据参数名获取参数的值
            String requestValue = requestParamMap.get(paramName);
            // 用于接收实例化后的参数的值
            Object value;
            //只支持String 以及基础类型char,int,short,byte,double,long,float,boolean,及它们的包装类型
            if (requestValue == null) {
                // 返回基本数据类型的空值
                value = ConverterUtil.primitiveNull(type);
            } else {
                // String类型转换成对应的参数类型
                value = ConverterUtil.convert(type, requestValue);
            }
            methodParams.add(value);
        }
        //3.执行Controller里面对应的方法并返回结果
        //Controller对应的Class对象所对应的bean
        Object controller = beanContainer.getBean(controllerMethod.getControllerClass());
        //执行的Controller方法实例
        Method invokeMethod = controllerMethod.getInvokeMethod();
        // 方便反射执行
        invokeMethod.setAccessible(true);
        // 接受方法执行后的返回值
        Object result;
        try {
            // 是否有参数
            if (methodParams.size() == 0) {
                // 无参数直接执行controller
                result = invokeMethod.invoke(controller);
            } else {
                // 有参需传入方法参数数组
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (InvocationTargetException e) {
            // 如果是调用异常的话，需要通过e.getTargetException()
            // 去获取执行方法抛出的异常
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException e) {
            // 非法访问异常，直接抛出
            throw new RuntimeException(e);
        }
        // 返回方法处理的结果
        return result;
    }
}
