package com.sxilverr.unchangedworld.world.gen;

public final class Generation164 {

    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> BOOKS = ThreadLocal.withInitial(() -> false);

    private Generation164() {}

    public static boolean isActive() {
        return ACTIVE.get();
    }

    public static boolean isBooks164() {
        return BOOKS.get();
    }

    public static void begin(boolean books164) {
        ACTIVE.set(true);
        BOOKS.set(books164);
    }

    public static void end() {
        ACTIVE.set(false);
        BOOKS.set(false);
    }
}
