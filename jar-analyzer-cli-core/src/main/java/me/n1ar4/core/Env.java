package me.n1ar4.core;

import me.n1ar4.db.entity.ClassFileEntity;

import java.util.*;

public class Env {
    public static Set<ClassFileEntity> classFileList = new HashSet<>();
    public static final Set<ClassReference> discoveredClasses = new HashSet<>();
    public static final Set<MethodReference> discoveredMethods = new HashSet<>();
    public static final Map<ClassReference.Handle, List<MethodReference>> methodsInClassMap = new HashMap<>();
    public static final Map<ClassReference.Handle, ClassReference> classMap = new HashMap<>();
    public static final Map<MethodReference.Handle, MethodReference> methodMap = new HashMap<>();
    public static final HashMap<MethodReference.Handle,
            HashSet<MethodReference.Handle>> methodCalls = new HashMap<>();
    public static InheritanceMap inheritanceMap;
    public static Map<MethodReference.Handle, List<String>> strMap = new HashMap<>();
}
