package com.example.demo;

import com.example.demo.validation.Item;
import com.example.demo.validation.ItemDictionary;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ItemAnnotationDictionaryKeyTest {

    @Test
    void allItemKeysMustExistInDictionary() throws Exception {
        Set<Class<?>> classes = scanClasses("com.example.demo");

        List<String> missing = new ArrayList<>();

        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Item.class)) {
                    String key = field.getAnnotation(Item.class).value();
                    if (ItemDictionary.get(key) == null) {
                        missing.add(clazz.getName() + "." + field.getName() + " => " + key);
                    }
                }
            }
        }

        if (!missing.isEmpty()) {
            fail("Missing dictionary entries for @Item keys:\n" + String.join("\n", missing));
        }
    }

    private Set<Class<?>> scanClasses(String basePackage) throws IOException {
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        Set<Class<?>> classes = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            try {
                if (resource.getProtocol().equals("file")) {
                    Path dir = Paths.get(resource.toURI());
                    Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (file.toString().endsWith(".class")) {
                                String className = basePackage + "." + dir.relativize(file).toString()
                                        .replace("\\", "/")
                                        .replace("/", ".")
                                        .replaceAll("\.class$", "");
                                try {
                                    classes.add(Class.forName(className));
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } else if (resource.getProtocol().equals("jar")) {
                    JarURLConnection jarConn = (JarURLConnection) resource.openConnection();
                    try (JarFile jarFile = jarConn.getJarFile()) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(path) && name.endsWith(".class")) {
                                String className = name.replace('/', '.').replaceAll("\.class$", "");
                                try {
                                    classes.add(Class.forName(className));
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed scanning classes in package " + basePackage, e);
            }
        }

        return classes;
    }
}
