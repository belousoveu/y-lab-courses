package belousov.eu.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator<T> {

    private final IdSupplier<T> idSupplier;

    private IdGenerator(IdSupplier<T> idSupplier1) {

        this.idSupplier = idSupplier1;
    }

    public T nextId() {
        return idSupplier.get();
    }

    @FunctionalInterface
    private interface IdSupplier<T> {
        T get();
    }

    public static <T> IdGenerator<T> create(Class<T> clazz) {
        if (clazz.equals(Long.class)) {
            AtomicLong nextId = new AtomicLong(0L);
            return (IdGenerator<T>) new IdGenerator<>(nextId::incrementAndGet);

        } else if (clazz.equals(Integer.class)) {
            AtomicInteger nextId = new AtomicInteger(0);
            return (IdGenerator<T>) new IdGenerator<>(nextId::incrementAndGet);

        } else if (clazz.equals(UUID.class)) {
            return (IdGenerator<T>) new IdGenerator<>(UUID::randomUUID);

        } else {
            throw new IllegalArgumentException("Unsupported generator type: " + clazz);
        }
    }

}



