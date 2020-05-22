package org.youngspringframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     * 根据包名获取包下类集合
     * @return 类集合
     * @parampackageName包名
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        //1.获取到类的加载器。
        ClassLoader classLoader = getClassLoader();
        //2.通过类加载器获取到加载的资源
        // ClassLoader.getResource(String path) 获取资源路径
        // getResource里输入的地址以 / 间隔 ，需要将包名的 . 给替换成 /
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if (url == null) {
            log.warn("unable to retrieve anything from package: " + packageName);
            return null;
        }
        //3.依据不同的资源类型，采用不同的方式获取资源的集合
        Set<Class<?>> classSet = null;
        // 过滤出文件类型的资源即URL协议为file的文件资源
        // getProtocol()获取URL的协议
        // equalsIgnoreCase()将字符串与指定的对象比较，不考虑大小写。
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<Class<?>>();
            // 获取资源的实际路径
            // getPath()返回URL路径部分
            File packageDirectory = new File(url.getPath());
            //递归获取目标package里面的所有class文件(包括子package里的class文件)
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return classSet;
    }

    /**
     * 递归获取目标package里面的所有class文件(包括子package里的class文件)
     * @param emptyClassSet 装载目标类的集合
     * @param fileSource    文件或者目录
     * @param packageName   包名
     * @return 类集合
     */
    private static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if (!fileSource.isDirectory()) {
            return;
        }
        // listFiles()返回某个目录下所有文件和目录的绝对路径,不包括子文件夹
        // 可以通过文件过滤器FileFilter()筛选
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                //只获取目录，class文件直接加载
                if (file.isDirectory()) {
                    return true;
                } else {
                    //class文件
                    //获取文件的绝对值路径
                    String absoluteFilePath = file.getAbsolutePath();
                    //判断是否是以.class结尾
                    if (absoluteFilePath.endsWith(".class")) {
                        //若是class文件，则直接加载
                        addToClassSet(absoluteFilePath);
                    }
                }
                return false;
            }

            //根据class文件的绝对值路径，获取并生成class对象，并放入classSet中
            private void addToClassSet(String absoluteFilePath) {
                //1.从class文件的绝对值路径里提取出包含了package的类名
                //如/Users/baidu/imooc/springframework/sampleframework/target/classes/com/imooc/entity/dto/MainPageInfoDTO.class
                //需要弄成com.imooc.entity.dto.MainPageInfoDTO

                // 将绝对路径 分隔符 替换为 .
                absoluteFilePath = absoluteFilePath.replace(File.separator, ".");
                // 找出 packageName 开始的位置，并从其开始获取路径 entity.dto.MainPageInfoDTO.class
                String className = absoluteFilePath.substring(absoluteFilePath.indexOf(packageName));
                // 获取路径 从 坐标0 开始到最后一个 . 号结束 entity.dto.MainPageInfoDTO
                className = className.substring(0, className.lastIndexOf("."));
                //2.通过反射机制获取对应的Class对象并加入到classSet里
                Class targetClass = loadClass(className);
                // 添加
                emptyClassSet.add(targetClass);
            }
        });

        if (files != null) {
            for (File f : files) {
                //递归调用
                extractClassFile(emptyClassSet, f, packageName);
            }
        }
    }

    /**
     * 获取Class对象
     * @param className class全名=package + 类名
     * @return Class
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化class
     *
     * @param clazz      Class
     * @param <T>        class的类型
     * @param accessible 是否支持创建出私有class对象的实例
     * @return 类的实例化
     */
    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            // getDeclaredConstructor()返回制定参数类型的所有构造器
            Constructor constructor = clazz.getDeclaredConstructor();
            // 反射的访问权限setAccessible,为true时反射可以访问私有变量
            constructor.setAccessible(accessible);
            return (T)constructor.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取classLoader
     * @return 当前ClassLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }



    /**
     * 设置类的属性值
     * @param field      成员变量
     * @param target     类实例
     * @param value      成员变量的值
     * @param accessible 是否允许设置私有属性
     */
    public static void setField(Field field, Object target, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("setField error", e);
            throw new RuntimeException(e);
        }
    }

}
