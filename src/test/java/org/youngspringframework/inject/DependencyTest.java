package org.youngspringframework.inject;

import com.wewe.controller.fronted.MainPageController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.youngspringframework.core.BeanContainer;


public class DependencyTest {

    @Test
    public void doIocTest() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.wewe");
        Assertions.assertTrue(beanContainer.isLoaded());
        MainPageController mainPageController = (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertTrue(mainPageController instanceof MainPageController);
        Assertions.assertNull(mainPageController.getHeadLineShopCategoryCombineService());
        new DependencyInjector().doIoc();
        Assertions.assertNotEquals(null,mainPageController.getHeadLineShopCategoryCombineService());

    }
}
