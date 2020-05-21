import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

public class test {
    public static void main(String[] args) {
        test test = new test();
        Object o = new Object();
        System.out.println(test.getClass());
        System.out.println(o.getClass());
        System.out.println(test.getClass()==o.getClass());
    }
}
