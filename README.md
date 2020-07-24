# youngspring
mini spring 
根据对spring源码的解读，编写的spring框架青春版
具有简单的IOC，AOP和MVC架构功能

# Bean容器实现
1. api:
    1. 加载bean的注解列表:List<Class<? extends Annotation>> BEAN_ANNOTATION
    2. 获取Bean容器实例:BeanContainer getInstance()
    3. 容器是否已经加载过bean: boolean isLoaded()
    4. 扫描加载包内所有Bean: void loadBeans(String packageName)
        1. 判断bean容器是否被加载过,防止重复加载，提高效率
        2. 根据包名获取包下类集合,判断是否为空
        3. 遍历类,遍历注解集合,如果类上面标记了定义的注解,将目标类本身作为键，目标类的实例作为值，放入到beanMap中
    5. 添加一个class对象及其Bean实例:Object addBean(Class<?> clazz, Object bean)
    6. 移除一个IOC容器管理的对象:Object removeBean(Class<?> clazz)
    7. 根据Class对象获取Bean实例:Object getBean(Class<?> clazz)
    8. 根据注解筛选出Bean的Class集合:Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation)
        1. 获取beanMap的所有class对象
        2. 通过注解筛选被注解标记的class对象，并添加到classSet里
    9. 通过接口或者父类获取实现类或者子类的Class集合，不包括其本身:Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass)
        1. 获取beanMap的所有class对象
        2. 判断keySet里的元素是否是传入的接口或者类的子类，如果是，就将其添加到classSet里
2. 以ConcurrentHashMap为容器存放所有被注释标记的目标对象的Map
3. 拖过枚举实现单例模式,解决反射机制和序列化的侵入，保证线程安全


# DI实现
1. api
    1. 获取Bean容器
    2. 执行Ioc:doIoc()
        1. 遍历Bean容器中所有的Class对象
        2. 遍历Class对象的所有成员变量
        3. 找出被Autowired标记的成员变量
            1. 获取Autowired的实例
            2. 获取其value值，防止有多个实现类，不知道装载具体哪一个
        4. 获取这些成员变量的类型
        5. 获取这些成员变量的类型在容器里对应的实例
        6. 通过反射将对应的成员变量实例注入到成员变量所在类的实例里
    3. 根据Class在beanContainer里获取其实例或者实现类:Object getFieldInstance(Class<?> fieldClass, String autowiredValue)
        1. 根据传入类的类型获取bean
        2. 若获取成功则直接返回
        3. 若获取失败后,说明该类型为接口,则根据传入的autowiredValue获取接口实现类
    4. 获取接口的实现类:Class<?> getImplementedClass(Class<?> fieldClass, String autowiredValue)
        1. 通过接口或者父类获取实现类或者子类的Class集合，不包括其本身
        2. 对class集合判空,如果为空，表明没有指定实现类
        3. 对autowiredValue判空,如果为空
            1. 若只有一个实现类，则直接返回
            2. 如果多于两个实现类且用户未指定其中一个实现类，则抛出异常
        4. autowiredValue不为空,遍历实现类，找出指明的那个
2. Autowired注释内部value值用于指明具体类,以防接口或父类有多个实现类



# AOP实现
## 1. 框架
1. CGLIB实现动态代理:不需要业务类实现接口,相对灵活
2. 集成AspectJ
    1. 定义切面语法以及切面语法的解析机制
    2. 提供了强大的织入工具
    3. 不仅仅织入方法
3. 大体思路
    1. 解决标记的问题,定义横切逻辑的骨架
    2. 定义Aspect横切逻辑以及被代理方法的执行顺序
    3. 将横切逻辑织入到被代理对象以生成动态代理对象
4. 主要类:
    1. AspectListExecutor.java 方法拦截器
    2. ProxyCreator.java 创建动态代理对象并返回
    3. AspectInfo 用于封装Aspect的实现类以及Order的value值等信息
    4. DefaultAspect.java  横切逻辑框架
    5. AspectWeaver.java   将横切逻辑织入到被代理的对象以生成动态代理对象
    6. PointcutLocator.java 解析Aspect表达式并且定位被织入的目标
## 2. 步骤
### 1. 定义与横切逻辑相关的注解
1. Aspect标签:pointcut()存储AspectJ表达式
2. Order标签:标记优先级,value越小优先级越高
### 2. 定义供外部使用的横切逻辑骨架
1. DefaultAspect.java抽象类作为框架
    1. 事前拦截
    2. 事后拦截
    3. 异常后拦截
2. AspectINfo.java类:用于封装Aspect的实现类以及Order的value值等信息
### 3. 定义Aspect横切逻辑的织入以及被代理方法的定序执行
1. 创建MethodInterceptor的实现类
    1. AspectListExecutor.java往被代理对象织入横切逻辑
2. 定义必要的成员变量:被代理的类以及Aspect列表
3. 按照Order对Aspect进行排序:确保order值小的aspect先被织入
4. 实现对横切逻辑以及被代理对象的定序执行:重写intercept()方法
    1. 根据方法对AspectInfo进行精筛
    2. 按照order的顺序**升序**执行完所有Aspect的before方法
    3. 执行被代理类的方法
    4. 如果被代理方法正常返回，则按照order的顺序**降序**执行完所有Aspect的afterReturning方法
    5. 如果被代理方法抛出异常，则按照order的顺序**降序**执行完所有Aspect的afterThrowing方法
#### 4. 解析Aspect表达式并且定位被织入的目标
1. 创建PointcutParser,Pointcut解析器，直接给它赋值上Aspectj的所有支持的语法树，以便支持对众多表达式的解析
2. 创建PointcutExpression,Pointcut表达式,PointcutParser根据表达式解析出来的产物,用来判定某个类是否与表达式匹配
3. 是否匹配判断
    1. 判断传入的Class对象是否是Aspect的目标代理类，即匹配Pointcut表达式(初筛)
    2. 判断传入的Method对象是否是Aspect的目标代理方法，即匹配Pointcut表达式(精筛) 
#### 5. 将横切逻辑织入到被代理的对象以生成动态代理对象
1. 获取Bean容器
2. doAOP()织入Aspect
    1. 获取所有被Aspect注释的切面类
    2. 根据切面类获取AspectInfo集合
        1. 遍历切面类集合并判断是否符合规定:框架中一定要遵守给Aspect类添加@Aspect和@Order标签的规范，同时必须继承自DefaultAspect.class
        2. 获取切面类Order,Aspect标签的实例
        3. 获取切面类实例
        4. 根据Aspect内pointcut的值初始化表达式定位器PointcutLoader
        5. 将orderTag.value(), defaultAspect, pointcutLocator三个属性拼装成AspectInfo实例
        6. 将AspectInfo添加到AspectInfoList
    3. 遍历容器里的类
        1. 排除AspectClass自身，避免死循环
        2. 粗筛符合条件的Aspect
            1. 遍历AspectInfoList
            2. 调用每个AsepctInfo中PointcutLocator的roughMathches,判断与目标切面类是否符合
            3. 将符合的AspectInfo封装到List中返回
    5. 尝试进行Aspect的织入
        1. 创建动态代理对象
        2. 将对象放入bean容器中,代替原来的实例
# MVC实现








# 工具类实现
## 1. ClassUtil.class
1. 根据包名获取包下类集合:Set<Class<?>> extractPackageClass(String packageName)
    1. 获取项目类加载器,以此来获取项目发布的实际路径
    2. ClassLoader.getResource(String path) 获取资源路径
    3. 判断是否是文件类型的资源即URL协议为file的文件资源
    4. 获取资源的实际路径
    5. 递归获取目标package里面的所有class文件(包括子package里的class文件)
2. 递归获取目标package里面的所有class文件(包括子package里的class文件):extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName)
    1. 判断fileSource是否是文件夹,若不是则直接返回
    2. 如果是文件夹,则调用listFiles()返回某个目录下所有文件和文件夹的绝对路径,不包括子文件夹
    3. 重写FileFilter()的accpet()过滤,获取files
        1. 如果是文件夹,则接受,返回true
        2. 如果不是文件夹
            1. 获取绝对值路径,判断是否以.class结尾
            2. 若是class文件，则根据class文件的绝对值路径，获取并生成class对象，并放入classSet中
                1. 将绝对路径分隔符替换为.
                2. 找出packageName开始的位置，并从其开始获取路径  entity.dto.MainPageInfoDTO.class
                3. 获取路径从坐标0开始到最后一个.号结束  entity.dto.MainPageInfoDTO
                4. 通过反射机制获取对应的Class对象并加入到classSet里
    4. 若获取的files不是null,则递归进行以上操作
            
        