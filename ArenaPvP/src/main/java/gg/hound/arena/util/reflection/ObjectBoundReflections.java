package gg.hound.arena.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


public class ObjectBoundReflections {

    private Object bound;

    public ObjectBoundReflections(Object bound) {
        this.bound = bound;
    }

    public ObjectBoundReflections(Class<?> clazz) {
        try {
            this.bound = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void invoke(String name) {
        try {
            bound.getClass().getDeclaredMethod(name).invoke(bound);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }


    public ObjectBoundReflections set(String name, Object object) {
        try {
            access(name).set(bound, object);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }


    public ObjectBoundReflections setInt(String name, int value) {
        try {
            access(name).setInt(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public ObjectBoundReflections setFloat(String name, float value) {
        try {
            access(name).setFloat(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }


    public ObjectBoundReflections setDouble(String name, double value) {
        try {
            access(name).setDouble(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public ObjectBoundReflections setByte(String name, byte value) {
        try {
            access(name).setByte(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public ObjectBoundReflections setLong(String name, long value) {
        try {
            access(name).setLong(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public ObjectBoundReflections setShort(String name, short value) {
        try {
            access(name).setShort(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public ObjectBoundReflections setChar(String name, char value) {
        try {
            access(name).setChar(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public ObjectBoundReflections setBoolean(String name, boolean value) {
        try {
            access(name).setBoolean(bound, value);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }

        return this;
    }

    public Object get(String name) {
        try {
            return access(name).get(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public boolean getBoolean(String name) {
        try {
            return access(name).getBoolean(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }


    public int getInt(String name) {
        try {
            return access(name).getInt(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public double getDouble(String name) {
        try {
            return access(name).getDouble(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public float getFloat(String name) {
        try {
            return access(name).getFloat(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public byte getByte(String name) {
        try {
            return access(name).getByte(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public char getChar(String name) {
        try {
            return access(name).getChar(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public long getLong(String name) {
        try {
            return access(name).getLong(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public short getShort(String name) {
        try {
            return access(name).getShort(bound);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Field access(String name) throws NoSuchFieldException {
        Field field = bound.getClass().getDeclaredField(name);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field;
    }

    public ObjectBoundReflections bind(Object object) {
        this.bound = object;
        return this;
    }

    public <E> E bound() {
        return (E) bound;
    }

    public <E> E unbind() {
        Object old = bound;
        this.bound = null;
        return (E) old;
    }


}

