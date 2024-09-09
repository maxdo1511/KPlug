package ru.kiscode.kplugdi.util;

/**
 * Class burying all exception messages that may occur during plugin operation
 */
public class ErrorMessages {

    private ErrorMessages(){
    }

    private static final String PREFIX = "[KPlug] ";
    public static final String CONSTRUCTOR_CREATING = setPrefix("error creating bean class <%s>. The class must have a public empty constructor, not be an interface or abstract class");
    public static final String METHOD_VOID_RESULT = setPrefix("@Bean method <%s> in class <%s> must not return void class");
    public static final String OBJECT_STATIC = setPrefix("&s <%s> in class <%s> should not be static");
    public static final String METHOD_PARAMETER_ERROR = setPrefix("@Autowired method <%s> in class <%s> should has 1 parameter for inject");
    public static final String QUALIFIER_NOT_FOUND = setPrefix("not found bean with @Qualifier path - [class - <%s>, method - <%s>] for %s <%s> in class <%s>");
    public static final String MULTIPLY_BEANS = setPrefix("class <%s> has multiple beans. Use @Qualifier annotation to select one bean for %s <%s> in class <%s>");
    public static final String CLASS_NOT_REGISTERED = setPrefix("filed inject %s <%s> in class <%s>. Bean class <%s> not registered in bean context");
    public static final String FILED_INJECT = setPrefix("reflection error. Filed inject %s <%s> in class <%s>");
    public static final String PROCESSOR_NOT_FOUND = setPrefix("not found bean processor for bean type <%s>");
    public static final String CYCLE_DETECTED = setPrefix("Cyclic dependency detected: %s");

    private static String setPrefix(String message){
        return PREFIX+message;
    }
}
