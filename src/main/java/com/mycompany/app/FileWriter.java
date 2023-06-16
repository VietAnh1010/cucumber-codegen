package com.mycompany.app;

import java.util.Collection;

public interface FileWriter<T> {

    public void write(T object);

    default public void write(Collection<T> objects) {
        objects.forEach(this::write);
    }
}
