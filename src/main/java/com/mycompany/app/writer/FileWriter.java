package com.mycompany.app.writer;

import java.util.Collection;

public interface FileWriter<T> {

    public void write(T object);

    default public void write(Collection<T> objects) {
        objects.forEach(this::write);
    }
}
