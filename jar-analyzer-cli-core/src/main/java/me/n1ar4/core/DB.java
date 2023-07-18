package me.n1ar4.core;

import me.n1ar4.db.core.SqlSessionFactoryUtil;
import me.n1ar4.db.entity.*;
import me.n1ar4.db.mapper.*;
import me.n1ar4.util.OSUtil;
import me.n1ar4.util.PartitionUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class DB {
    private static final Logger logger = LogManager.getLogger(DB.class);
    private static final SqlSession session;
    private static final ClassMapper classMapper;
    private static final MemberMapper memberMapper;
    private static final JarMapper jarMapper;
    private static final AnnoMapper annoMapper;
    private static final MethodMapper methodMapper;
    private static final StringMapper stringMapper;
    private static final InterfaceMapper interfaceMapper;
    private static final ClassFileMapper classFileMapper;
    private static final MethodImplMapper methodImplMapper;
    private static final MethodCallMapper methodCallMapper;

    static {
        SqlSessionFactory factory = SqlSessionFactoryUtil.sqlSessionFactory;
        session = factory.openSession(true);
        classMapper = session.getMapper(ClassMapper.class);
        jarMapper = session.getMapper(JarMapper.class);
        annoMapper = session.getMapper(AnnoMapper.class);
        methodMapper = session.getMapper(MethodMapper.class);
        memberMapper = session.getMapper(MemberMapper.class);
        stringMapper = session.getMapper(StringMapper.class);
        classFileMapper = session.getMapper(ClassFileMapper.class);
        interfaceMapper = session.getMapper(InterfaceMapper.class);
        methodCallMapper = session.getMapper(MethodCallMapper.class);
        methodImplMapper = session.getMapper(MethodImplMapper.class);
        InitMapper initMapper = session.getMapper(InitMapper.class);
        initMapper.createJarTable();
        initMapper.createClassTable();
        initMapper.createClassFileTable();
        initMapper.createMemberTable();
        initMapper.createMethodTable();
        initMapper.createAnnoTable();
        initMapper.createInterfaceTable();
        initMapper.createMethodCallTable();
        initMapper.createMethodImplTable();
        initMapper.createStringTable();
    }

    public static void saveJar(String jarPath) {
        JarEntity en = new JarEntity();
        en.setJarAbsPath(jarPath);
        if (OSUtil.isWindows()) {
            String[] temp = jarPath.split("\\\\");
            en.setJarName(temp[temp.length - 1]);
        } else {
            String[] temp = jarPath.split("/");
            en.setJarName(temp[temp.length - 1]);
        }
        List<JarEntity> js = new ArrayList<>();
        js.add(en);
        int i = jarMapper.insertJar(js);
        if (i != 0) {
            logger.debug("save jar file info finish");
        }
    }

    public static void saveClassFiles(Set<ClassFileEntity> classFileList) {
        logger.info("class file total: {}", classFileList.size());
        List<ClassFileEntity> list = new ArrayList<>();
        for (ClassFileEntity classFile : classFileList) {
            classFile.setPathStr(classFile.getPath().toAbsolutePath().toString());
            list.add(classFile);
        }
        List<List<ClassFileEntity>> partition = PartitionUtils.partition(list, 100);
        for (List<ClassFileEntity> data : partition) {
            int a = classFileMapper.insertClassFile(data);
            if (a == 0) {
                logger.warn("save class file error");
            }
        }
        logger.info("save class file info finish");
    }

    public static void saveClassInfo(Set<ClassReference> discoveredClasses) {
        logger.info("total class: {}", discoveredClasses.size());
        List<ClassEntity> list = new ArrayList<>();
        for (ClassReference reference : discoveredClasses) {
            ClassEntity classEntity = new ClassEntity();
            classEntity.setJarName(reference.getJar());
            classEntity.setClassName(reference.getName());
            classEntity.setSuperClassName(reference.getSuperClass());
            classEntity.setInterface(reference.isInterface());
            list.add(classEntity);
        }
        List<List<ClassEntity>> partition = PartitionUtils.partition(list, 100);
        for (List<ClassEntity> data : partition) {
            int a = classMapper.insertClass(data);
            if (a == 0) {
                logger.warn("save class error");
            }
        }
        logger.info("save class info finish");

        List<MemberEntity> mList = new ArrayList<>();
        List<AnnoEntity> aList = new ArrayList<>();
        List<InterfaceEntity> iList = new ArrayList<>();
        for (ClassReference reference : discoveredClasses) {
            for (ClassReference.Member member : reference.getMembers()) {
                MemberEntity memberEntity = new MemberEntity();
                memberEntity.setMemberName(member.getName());
                memberEntity.setModifiers(member.getModifiers());
                memberEntity.setTypeClassName(member.getType().getName());
                memberEntity.setClassName(reference.getName());
                mList.add(memberEntity);
            }
            for (String anno : reference.getAnnotations()) {
                AnnoEntity annoEntity = new AnnoEntity();
                annoEntity.setAnnoName(anno);
                annoEntity.setClassName(reference.getName());
                aList.add(annoEntity);
            }
            for (String inter : reference.getInterfaces()) {
                InterfaceEntity i = new InterfaceEntity();
                i.setClassName(reference.getName());
                i.setInterfaceName(inter);
                iList.add(i);
            }
        }
        List<List<MemberEntity>> mPartition = PartitionUtils.partition(mList, 100);
        for (List<MemberEntity> data : mPartition) {
            int a = memberMapper.insertMember(data);
            if (a == 0) {
                logger.warn("save class member error");
            }
        }
        logger.info("save class member info finish");

        List<List<AnnoEntity>> aPartition = PartitionUtils.partition(aList, 100);
        for (List<AnnoEntity> data : aPartition) {
            int a = annoMapper.insertAnno(data);
            if (a == 0) {
                logger.warn("save class anno error");
            }
        }
        logger.info("save class anno info finish");

        List<List<InterfaceEntity>> iPartition = PartitionUtils.partition(iList, 100);
        for (List<InterfaceEntity> data : iPartition) {
            int a = interfaceMapper.insertInterface(data);
            if (a == 0) {
                logger.warn("save class interface error");
            }
        }
        logger.info("save class interface info finish");
    }

    public static void saveMethods(Set<MethodReference> discoveredMethods) {
        logger.info("total methods: {}", discoveredMethods.size());
        List<MethodEntity> mList = new ArrayList<>();
        List<AnnoEntity> aList = new ArrayList<>();
        for (MethodReference reference : discoveredMethods) {
            MethodEntity methodEntity = new MethodEntity();
            methodEntity.setMethodName(reference.getName());
            methodEntity.setMethodDesc(reference.getDesc());
            methodEntity.setClassName(reference.getClassReference().getName());
            methodEntity.setStatic(reference.isStatic());
            methodEntity.setAccess(reference.getAccess());
            mList.add(methodEntity);
            for (String anno : reference.getAnnotations()) {
                AnnoEntity annoEntity = new AnnoEntity();
                annoEntity.setAnnoName(anno);
                annoEntity.setMethodName(reference.getName());
                annoEntity.setClassName(reference.getClassReference().getName());
                aList.add(annoEntity);
            }
        }
        List<List<MethodEntity>> mPartition = PartitionUtils.partition(mList, 100);
        for (List<MethodEntity> data : mPartition) {
            int a = methodMapper.insertMethod(data);
            if (a == 0) {
                logger.warn("save method error");
            }
        }
        logger.info("save method info finish");

        List<List<AnnoEntity>> aPartition = PartitionUtils.partition(aList, 100);
        for (List<AnnoEntity> data : aPartition) {
            int a = annoMapper.insertAnno(data);
            if (a == 0) {
                logger.warn("save method anno error");
            }
        }
        logger.info("save method anno info finish");
    }

    public static void saveMethodCalls(HashMap<MethodReference.Handle,
            HashSet<MethodReference.Handle>> methodCalls) {
        List<MethodCallEntity> mList = new ArrayList<>();
        for (Map.Entry<MethodReference.Handle, HashSet<MethodReference.Handle>> call :
                methodCalls.entrySet()) {
            MethodReference.Handle caller = call.getKey();
            HashSet<MethodReference.Handle> callee = call.getValue();

            for (MethodReference.Handle mh : callee) {
                MethodCallEntity mce = new MethodCallEntity();
                mce.setCallerClassName(caller.getClassReference().getName());
                mce.setCallerMethodName(caller.getName());
                mce.setCallerMethodDesc(caller.getDesc());
                mce.setCalleeClassName(mh.getClassReference().getName());
                mce.setCalleeMethodName(mh.getName());
                mce.setCalleeMethodDesc(mh.getDesc());
                mList.add(mce);
            }
        }

        List<List<MethodCallEntity>> mPartition = PartitionUtils.partition(mList, 100);
        for (List<MethodCallEntity> data : mPartition) {
            int a = methodCallMapper.insertMethodCall(data);
            if (a == 0) {
                logger.warn("save method call error");
            }
        }
        logger.info("save method call info finish");
    }

    public static void saveImpls(Map<MethodReference.Handle, Set<MethodReference.Handle>> implMap) {
        List<MethodImplEntity> mList = new ArrayList<>();
        for (Map.Entry<MethodReference.Handle, Set<MethodReference.Handle>> call :
                implMap.entrySet()) {
            MethodReference.Handle method = call.getKey();
            Set<MethodReference.Handle> impls = call.getValue();
            for (MethodReference.Handle mh : impls) {
                MethodImplEntity impl = new MethodImplEntity();
                impl.setImplClassName(mh.getClassReference().getName());
                impl.setClassName(method.getClassReference().getName());
                impl.setMethodName(mh.getName());
                impl.setMethodDesc(mh.getDesc());
                mList.add(impl);
            }
        }
        List<List<MethodImplEntity>> mPartition = PartitionUtils.partition(mList, 100);
        for (List<MethodImplEntity> data : mPartition) {
            int a = methodImplMapper.insertMethodImpl(data);
            if (a == 0) {
                logger.warn("save method impl error");
            }
        }
        logger.info("save method impl info finish");
    }

    public static void saveStrMap(Map<MethodReference.Handle, List<String>> strMap) {
        List<StringEntity> mList = new ArrayList<>();
        for (Map.Entry<MethodReference.Handle, List<String>> strEntry : strMap.entrySet()) {
            MethodReference.Handle method = strEntry.getKey();
            List<String> strList = strEntry.getValue();
            for (String s : strList) {
                MethodReference mr = Env.methodMap.get(method);
                ClassReference cr = Env.classMap.get(mr.getClassReference());
                StringEntity stringEntity = new StringEntity();
                stringEntity.setValue(s);
                stringEntity.setAccess(mr.getAccess());
                stringEntity.setClassName(cr.getName());
                stringEntity.setJarName(cr.getJar());
                stringEntity.setMethodDesc(mr.getDesc());
                stringEntity.setMethodName(mr.getName());
                mList.add(stringEntity);
            }
        }
        List<List<StringEntity>> mPartition = PartitionUtils.partition(mList, 100);
        for (List<StringEntity> data : mPartition) {
            int a = stringMapper.insertString(data);
            if (a == 0) {
                logger.warn("save string map error");
            }
        }
        logger.info("save string map info finish");
    }
}
