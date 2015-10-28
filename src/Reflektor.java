import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by clvcooke on 10/27/2015.
 */
public class Reflektor {

    Class targetClass;
    Object target;
    HashMap<String, Method> publicMethodMap = new HashMap<>();
    HashMap<String, Method> privateMethodMap = new HashMap<>();

    /**
     * @param classPath the full path of the target class
     * @throws IllegalAccessException the class can not be publicly accessed
     * @throws InstantiationException given class does not have a constructor
     * @throws ClassNotFoundException the class name given does not resolve to an actual class
     */
    public Reflektor(String classPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        targetClass = Class.forName(classPath);
        target = targetClass.newInstance();
    }

    /**
     * @param classPath  the full path of the target class
     * @param parameters an array of the parameters (if using Reflektor objects call #getTarget on them before adding them to the array)
     * @throws ClassNotFoundException    the class name given does not resolve to an actual class
     * @throws NoSuchMethodException     there is no constructor with these parameters
     * @throws IllegalAccessException    the class/method can not be publicly accessed
     * @throws InvocationTargetException
     * @throws InstantiationException    the constructor is not valid
     */

    public Reflektor(String classPath, Object... parameters) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        targetClass = Class.forName(classPath);
        int length = parameters.length;
        Class<?>[] classArray = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            classArray[i] = parameters[i].getClass();
        }
        target = targetClass.getConstructor(classArray).newInstance(parameters);
    }

    /**
     * @param classPath  the full path of the target class
     * @param methodName the method used instead of a normal constructor
     * @throws ClassNotFoundException    the class name given does not resolve to an actual class
     * @throws NoSuchMethodException     there is no method with this name
     * @throws IllegalAccessException    the class/method can not be publicly accessed
     * @throws InvocationTargetException
     * @throws InstantiationException    the constructor is not valid
     */

    public Reflektor(String classPath, String methodName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        targetClass = Class.forName(classPath);
        Method m = targetClass.getMethod(methodName);
        target = m.invoke(null);
    }


    /**
     * @param classPath  the full path of the target class
     * @param methodName the method used instead of a normal constructor
     * @param parameters an array of the parameters (if using Reflektor objects call #getTarget on them before adding them to the array)\
     * @throws ClassNotFoundException    the class name given does not resolve to an actual class
     * @throws NoSuchMethodException     there is no method with this name
     * @throws IllegalAccessException    the class/method can not be publicly accessed
     * @throws InvocationTargetException
     * @throws InstantiationException    the method is not valid
     */

    public Reflektor(String classPath, String methodName, Object... parameters) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        targetClass = Class.forName(classPath);
        int length = parameters.length;
        Class<?>[] classArray = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            classArray[i] = parameters[i].getClass();
        }
        Method m = targetClass.getMethod(methodName, classArray);
        target = m.invoke(null, parameters);
    }

    /**
     * @param methodName the method which you want to invoke
     * @return the return value of the method (ignore if void)
     * @throws NoSuchMethodException     the method name given was incorrect
     * @throws IllegalAccessException    the method is most likely private
     * @throws InvocationTargetException
     */

    public Object invoke(String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (!publicMethodMap.containsKey(methodName)) {
            publicMethodMap.put(methodName, targetClass.getMethod(methodName));
        }
        return publicMethodMap.get(methodName).invoke(target);
    }

    /**
     * @param methodName the method which you want to invoke
     * @param parameters the parameters for the method
     * @return the return value of the method (ignore if void)
     * @throws NoSuchMethodException     the method name given was incorrect or the parameters were incorrect or the method is private
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    public Object invoke(String methodName, Object... parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //for now....do some speed tests later
        int length = parameters.length;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodName);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(parameters[i].getClass());
        }

        String fullMethodName = stringBuilder.toString();
        if (!publicMethodMap.containsKey(fullMethodName)) {
            //Do we need to do this?
            Class<?>[] classArray = new Class<?>[length];
            for (int i = 0; i < length; i++) {
                classArray[i] = parameters[i].getClass();
            }
            Method m = targetClass.getMethod(fullMethodName, classArray);
            publicMethodMap.put(fullMethodName, m);
        }
        return publicMethodMap.get(fullMethodName).invoke(target, parameters);
    }

    /**
     * @param methodName the method which you want to invoke
     * @return the return value of the method (ignore if void)
     * @throws NoSuchMethodException     the method name given was incorrect
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    public Object invokePrivate(String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!privateMethodMap.containsKey(methodName)) {
            privateMethodMap.put(methodName, targetClass.getDeclaredMethod(methodName));
        }
        return privateMethodMap.get(methodName).invoke(target);
    }

    /**
     * @param methodName the method which you want to invoke
     * @param parameters the parameters for the method
     * @return the return value of the method (ignore if void)
     * @throws NoSuchMethodException     the method name given was incorrect or the parameters were incorrect
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */

    public Object invokePrivate(String methodName, Object... parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //for now....do some speed tests later
        int length = parameters.length;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methodName);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(parameters[i].getClass());
        }

        String fullMethodName = stringBuilder.toString();
        if (!privateMethodMap.containsKey(fullMethodName)) {
            //Do we need to do this?
            Class<?>[] classArray = new Class<?>[length];
            for (int i = 0; i < length; i++) {
                classArray[i] = parameters[i].getClass();
            }
            Method m = targetClass.getMethod(fullMethodName, classArray);
            m.setAccessible(true);
            privateMethodMap.put(fullMethodName, m);
        }
        return privateMethodMap.get(fullMethodName).invoke(target, parameters);
    }

    /**
     * @param fieldName the name of the variable
     * @return the variables value
     * @throws NoSuchFieldException   the field name is incorrect or the field is private
     * @throws IllegalAccessException
     */

    public Object getVar(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return targetClass.getField(fieldName).get(target);
    }

    /**
     * @param fieldName the name of the variable
     * @return the variables value
     * @throws NoSuchFieldException   the field name is incorrect
     * @throws IllegalAccessException
     */

    public Object getPrivateVar(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field f = targetClass.getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(target);
    }

    /**
     * @param fieldName the name of the variable
     * @param value     the variables value
     * @throws NoSuchFieldException   the field name is incorrect or the field is private
     * @throws IllegalAccessException
     */

    public void setVar(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        targetClass.getField(fieldName).set(target, value);
    }

    /**
     * @param fieldName the name of the variable
     * @param value     the variables value
     * @throws NoSuchFieldException   the field name is incorrect
     * @throws IllegalAccessException
     */

    public void setPrivateVar(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = targetClass.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }


    /**
     * @param classPath  the full path of the class
     * @param methodName the name of the method
     * @return the return value of the method (ignore if void)
     * @throws ClassNotFoundException    the class path given is incorrect
     * @throws NoSuchMethodException     the method given is wrong or the method is private
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public static Object invokeStatic(String classPath, String methodName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return Class.forName(classPath).getMethod(methodName).invoke(null);
    }

    /**
     * @param classPath  the full path of the class
     * @param methodName the name of the method
     * @param parameters the parameters for the method
     * @return the return value of the method (ignore if void)
     * @throws ClassNotFoundException    the class path given is incorrect
     * @throws NoSuchMethodException     the method given is wrong or the parameters are wrong or the method is private
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public static Object invokeStatic(String classPath, String methodName, Object... parameters) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int length = parameters.length;
        Class<?>[] classArray = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            classArray[i] = parameters[i].getClass();
        }
        return Class.forName(classPath).getMethod(methodName, classArray).invoke(null, parameters);
    }


    /**
     * @param classPath  the full path of the class
     * @param methodName the name of the method
     * @return the return value of the method (ignore if void)
     * @throws ClassNotFoundException    the class path given is incorrect
     * @throws NoSuchMethodException     the method name is wrong or the method is public
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public static Object invokePrivateStatic(String classPath, String methodName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = Class.forName(classPath).getMethod(methodName);
        m.setAccessible(true);
        return m.invoke(null);
    }

    /**
     * @param classPath  the full path of the class
     * @param methodName the name of the method
     * @param parameters the parameters of the method
     * @return the return value of the method (ignore if void)
     * @throws ClassNotFoundException    the class path given is incorrect
     * @throws NoSuchMethodException     the method name is wrong or the method is public or the parameters are wrong
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */

    public static Object invokePrivateStatic(String classPath, String methodName, Object... parameters) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int length = parameters.length;
        Class<?>[] classArray = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            classArray[i] = parameters[i].getClass();
        }
        Method m = Class.forName(classPath).getMethod(methodName, classArray);
        m.setAccessible(true);
        return m.invoke(null, parameters);
    }

    /**
     * @param classPath the full path of the class
     * @param fieldName the name of the variable
     * @return the value of the variable
     * @throws ClassNotFoundException the class path given is incorrect
     * @throws NoSuchFieldException   the variable name is incorrect or the variable is private
     * @throws IllegalAccessException
     */

    public static Object getStaticVar(String classPath, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return Class.forName(classPath).getDeclaredField(fieldName).get(null);
    }

    /**
     * @param classPath the full path of the class
     * @param fieldName the name of the variable
     * @return the value of the variable
     * @throws ClassNotFoundException the class path given is incorrect
     * @throws NoSuchFieldException   the variable name is incorrect
     * @throws IllegalAccessException
     */

    public static Object getPrivateStaticVar(String classPath, String fieldName) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        Field f = Class.forName(classPath).getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(null);
    }

    /**
     * @return the class of the target object
     */

    public Class getTargetClass() {
        return targetClass;
    }

    /**
     * @return the target object
     */
    public Object getTarget() {
        return target;
    }

}
