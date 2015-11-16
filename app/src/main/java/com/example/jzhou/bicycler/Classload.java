package com.example.jzhou.bicycler;

import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Stack;

/**
 * Created by jzhou on 19/10/2015.
 */
public class Classload {

    Classload() throws NoSuchMethodException, ClassNotFoundException {
        File clazzPath = new File("libs/postgresql-9.4-1204.jdbc41");

// 记录加载.class文件的数量  
        int clazzCount = 0;

        if (clazzPath.exists() && clazzPath.isDirectory()) {
            // 获取路径长度  
            int clazzPathLen = clazzPath.getAbsolutePath().length() + 1;

            Stack<File> stack = new Stack<>();
            stack.push(clazzPath);

            // 遍历类路径  
            while (stack.isEmpty() == false) {
                File path = stack.pop();
                File[] classFiles = path.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.isDirectory() || pathname.getName().endsWith(".class");
                    }
                });
                for (File subFile : classFiles) {
                    if (subFile.isDirectory()) {
                        stack.push(subFile);
                    } else {
                        if (clazzCount++ == 0) {
                            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                            boolean accessible = method.isAccessible();
                            try {
                                if (accessible == false) {
                                    method.setAccessible(true);
                                }
                                // 设置类加载器  
                                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                                // 将当前类路径加入到类加载器中  
                                method.invoke(classLoader, clazzPath.toURI().toURL());
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } finally {
                                method.setAccessible(accessible);
                            }
                        }
                        // 文件名称  
                        String className = subFile.getAbsolutePath();
                        className = className.substring(clazzPathLen, className.length() - 6);
                        className = className.replace(File.separatorChar, '.');
                        // 加载Class类  
                        Class.forName(className);
                      //  LOG.debug("读取应用程序类文件[class={}]", className);
                    }
                }
            }
        }
    }
}
