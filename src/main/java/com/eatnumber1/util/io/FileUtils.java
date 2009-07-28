package com.eatnumber1.util.io;/*
 * Copyright 2007 Russell Harmon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
    @NotNull
    private static Log log = LogFactory.getLog(FileUtils.class);

    @NotNull
    private static final Map<File, List<File>> fileElementCache = Collections.synchronizedMap(new WeakHashMap<File, List<File>>());

    protected FileUtils() {
    }

    /**
     * Checks if f2 is in a subdirectory of f1.
     *
     * @param f2 The file to check if it is in a subdir of f1.
     * @param f1 The file which may be in a superdir of f2.
     * @return Whether f2 is in a subdirectory of f1.
     */
    public static boolean isSubdirOf( @NotNull File f2, @NotNull File f1 ) {
        if( !f1.isDirectory() ) return false;
        List<File> f1_dirs = getFileElements(f1), f2_dirs = getFileElements(f2);
        Iterator<File> f1_iter = f1_dirs.iterator(), f2_iter = f2_dirs.iterator();
        while( f1_iter.hasNext() && f2_iter.hasNext() ) {
            if( !f1_iter.next().equals(f2_iter.next()) ) return false;
        }
        return true;
    }

    @NotNull
    private static List<File> getFileElements( @NotNull File f ) {
        List<File> cachedDirs = fileElementCache.get(f);
        if( cachedDirs != null ) return cachedDirs;
        List<File> dirs = new LinkedList<File>();
        File dir = f.getParentFile();
        while( dir != null ) {
            dirs.add(dir);
            dir = dir.getParentFile();
        }
        Collections.reverse(dirs);
        dirs = Collections.unmodifiableList(dirs);
        fileElementCache.put(f, dirs);
        return dirs;
    }

    public static void delete( @NotNull File file ) {
        // The single & is intentional to prevent short-circuit evaluation.
        if( file.exists() & !file.delete() ) {
            log.warn("Unable to delete file " + file);
        }
    }

    public static boolean isEmpty( @NotNull File file ) {
        if( !file.isDirectory() ) throw new IllegalArgumentException("File is not a directory.");
        return file.list().length == 0;
    }

    public static void forceMkdir( @NotNull File file ) throws IOException {
        if( file.exists() ) forceDelete(file);
        if( !file.mkdir() ) throw new IOException("Unable to create directory.");
    }

    public static void forceCreateNewFile( @NotNull File file ) throws IOException {
        if( file.exists() ) forceDelete(file);
        createNewFile(file);
    }

    public static void createNewFile( @NotNull File file ) throws IOException {
        if( !file.createNewFile() ) throw new IOException("Unable to create file.");
    }

    public static boolean contains( @NotNull File directory, @NotNull String fileName ) {
        return Arrays.asList(directory.list()).contains(fileName);
    }

    public static boolean contains( @NotNull File directory, @NotNull File file ) {
        return Arrays.asList(directory.listFiles()).contains(file);
    }

    @NotNull
    public static File createTempFile( @NotNull String prefix ) throws IOException {
        return File.createTempFile(prefix, null);
    }

    @NotNull
    public static File createTempDirectory( @NotNull String prefix ) throws IOException {
        return createTempDirectory(prefix, null);
    }

    @NotNull
    public static File createTempDirectory( @NotNull String prefix, @Nullable String suffix ) throws IOException {
        return createTempDirectory(prefix, suffix, null);
    }

    @NotNull
    public static File createTempDirectory( @NotNull String prefix, @Nullable String suffix, @Nullable File directory ) throws IOException {
        File tempFile = File.createTempFile(prefix, suffix, directory);
        forceDelete(tempFile);
        forceMkdir(tempFile);
        return tempFile;
    }
}
