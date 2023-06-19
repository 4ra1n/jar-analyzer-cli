package me.n1ar4.db.mapper;

public interface InitMapper {
    void createJarTable();

    void createClassTable();

    void createClassFileTable();

    void createMemberTable();

    void createMethodTable();

    void createAnnoTable();

    void createInterfaceTable();

    void createMethodCallTable();

    void createMethodImplTable();

    void createStringTable();
}
