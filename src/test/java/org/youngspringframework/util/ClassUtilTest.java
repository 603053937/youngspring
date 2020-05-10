package org.youngspringframework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ClassUtilTest {

    @Test
    public void test() {
        Set<Class<?>> classSet = ClassUtil.extractPackageClass("com.wewe.entity");
        System.out.println(classSet);
        Assertions.assertEquals(4,classSet.size());
    }
}
