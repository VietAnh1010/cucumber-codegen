package com.github.vanh1010.cucumber.codegen.backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Static utilities that can invoke a method of a particular class on an
 * object of that class.
 */
public final class Invoker {

    private Invoker() {
    }

    /**
     * Invokes an instance method on the target object with some arguments.
     */
    public static Object invoke(Object target, Method method, Object... args) {
        Method targetMethod = targetMethod(target, method);
        return doInvoke(target, targetMethod, args);
    }

    /**
     * Invokes a static method with some arguments.
     */
    public static Object invokeStatic(Method method, Object... args) {
        return doInvoke(null, method, args);
    }

    private static Method targetMethod(Object target, Method method) {
        Class<?> targetClass = target.getClass();
        Class<?> declaringClass = method.getDeclaringClass();
        if (targetClass.getClassLoader().equals(declaringClass.getClassLoader())) {
            return method;
        }
        try {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            // Check if the method is publicly accessible. Note that methods
            // from interfaces are always public.
            if (Modifier.isPublic(method.getModifiers())) {
                return targetClass.getMethod(methodName, parameterTypes);
            }

            // Loop through all the super classes until the declared method is found.
            Class<?> currentClass = targetClass;
            while (currentClass != Object.class) {
                try {
                    return currentClass.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException ex) {
                    currentClass = currentClass.getSuperclass();
                }
            }
            throw new NoSuchMethodException(String.valueOf(method));
        } catch (NoSuchMethodException ex) {
            throw new BackendException("Could not find target method", ex);
        }
    }

    private static Object doInvoke(Object target, Method targetMethod, Object[] args) {
        boolean accessible = targetMethod.canAccess(target);
        try {
            targetMethod.setAccessible(true);
            return targetMethod.invoke(target, args);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            throw new BackendException("Failed to invoke " + targetMethod, ex);
        } finally {
            targetMethod.setAccessible(accessible);
        }
    }
}
