package com.eatnumber1.util;/*
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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Russell Harmon
 * @since Jul 13, 2007
 */
public class FileUtils {
    @NotNull
    private static Logger log = Logger.getLogger(FileUtils.class);

    @NotNull
    private static final Map<File, List<File>> fileElementCache = Collections.synchronizedMap(new WeakHashMap<File, List<File>>());

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
}
