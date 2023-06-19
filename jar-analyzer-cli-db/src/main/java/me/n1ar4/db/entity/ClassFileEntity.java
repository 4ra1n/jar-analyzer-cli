package me.n1ar4.db.entity;

import java.nio.file.Files;
import java.nio.file.Path;

public class ClassFileEntity {
    // SAVE
    private int cfId;
    // SAVE
    private String className;
    private Path path;
    // SAVE
    private String pathStr;
    // SAVE
    private String jarName;

    public int getCfId() {
        return cfId;
    }

    public void setCfId(int cfId) {
        this.cfId = cfId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getPathStr() {
        return pathStr;
    }

    public void setPathStr(String pathStr) {
        this.pathStr = pathStr;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public ClassFileEntity() {
    }

    public ClassFileEntity(String className, Path path) {
        this.className = className;
        this.path = path;
    }

    public byte[] getFile() {
        try {
            return Files.readAllBytes(this.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "ClassFileEntity{" +
                "cfId=" + cfId +
                ", className='" + className + '\'' +
                ", path=" + path +
                ", pathStr='" + pathStr + '\'' +
                ", jarName='" + jarName + '\'' +
                '}';
    }
}
