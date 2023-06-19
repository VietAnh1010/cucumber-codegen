package com.mycompany.app.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.cucumber.core.backend.CucumberBackendException;

/**
 * Static utilities that can invoke a method of a particular class on an
 * object of that class.
 */
public final class Invoker {

    private Invoker() {
    }

    /**
     * Invokes an instance method on the target object with some arguments.
     * 
     * @param target
     * @param method
     * @param args
     * @return
     */
    public static Object invoke(Object target, Method method, Object... args) {
        Method targetMethod = targetMethod(target, method);
        return doInvoke(target, targetMethod, args);
    }

    /**
     * Invokes a static method with some arguments.
     * 
     * @param method
     * @param args
     * @return
     */
    public static Object invokeStatic(Method method, Object... args) {
        return doInvoke(null, method, args);
    }

    private static Method targetMethod(Object target, Method method) {
        Class<?> targetClass = target.getClass();
        Class<?> declaringClass = method.getDeclaringClass();
        // Immediately return the provided method if the class loaders are the
        // same.
        if (targetClass.getClassLoader().equals(declaringClass.getClassLoader())) {
            return method;
        }
        try {
            // Check if the method is publicly accessible. Note that methods
            // from interfaces are always public.
            if (Modifier.isPublic(method.getModifiers())) {
                return targetClass.getMethod(method.getName(), method.getParameterTypes());
            }

            // Loop through all the super classes until the declared method is found.
            Class<?> currentClass = targetClass;
            while (currentClass != Object.class) {
                try {
                    return currentClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    currentClass = currentClass.getSuperclass();
                }
            }
            // The method does not exist in the class hierarchy.
            throw new NoSuchMethodException(String.valueOf(method));
        } catch (NoSuchMethodException e) {
            throw new CucumberBackendException("Could not find target method", e);
        }
    }

    private static Object doInvoke(Object target, Method targetMethod, Object[] args) {
        @SuppressWarnings("deprecation")
        boolean accessible = targetMethod.isAccessible();
        try {
            targetMethod.setAccessible(true);
            return targetMethod.invoke(target, args);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new CucumberBackendException("Failed to invoke " + targetMethod, e);
        } catch (InvocationTargetException e) {
            throw new CucumberBackendException("Failed to invoke " + targetMethod, e);
        } finally {
            targetMethod.setAccessible(accessible);
        }
    }
}
