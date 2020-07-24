import com.wewe.controller.superadmin.HeadLineOperationController;
import org.junit.jupiter.api.Test;
import org.youngspringframework.aop.AspectWeaver;
import org.youngspringframework.core.BeanContainer;
import org.youngspringframework.inject.DependencyInjector;

public class test {
    @Test
    public void doAopTest(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.wewe");
        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();
        HeadLineOperationController headLineOperationController = (HeadLineOperationController) beanContainer.getBean(HeadLineOperationController.class);

    }

    @Test
    public void encoding() {
        System.out.println("为什么是乱码");

    }
}
