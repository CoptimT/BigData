package com.rtmap.hive.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HDFSUtils {
    private static Configuration conf = new Configuration();

    public FileSystem getFileSystem() throws IOException {
        FileSystem fs = FileSystem.get(conf);
        return fs;
    }

    /**
     * 上传文件到HDFS
     * @throws IOException
     */
    public void uploadDataToHDFS(File file, String remotedir) throws IOException {
        if (file == null || file.length() == 0) {
            return;
        }
        //LOG.info("begin write file to dfs : " + file.getName());
        FileSystem fs = getFileSystem();
        Path remoteFilePath = new Path(remotedir + File.separator + file.getName());
        FSDataOutputStream fos = fs.create(remoteFilePath, true);
        FileInputStream fis = new FileInputStream(file);

        IOUtils.copyBytes(fis, fos, 4096, true);
    }

    /**
     * delete HDFS files or path
     *
     * @param path
     * @throws IOException
     */
    public void delete(String path) throws IOException {
        if (path == null) {
            return;
        }
        FileSystem fs = getFileSystem();
        Path p = new Path(path);
        fs.delete(p, true);
        //LOG.info("deleted : " + path);
    }

    /**
     * 汇总运算结果，将多个输出合并成一个流文件
     *
     * @param resultPath
     * @return
     * @throws IOException
     */
    public InputStream getResultFromHDFS(String resultPath) throws IOException {
        SequenceInputStream sis = null;
        if (resultPath != null) {
            FileSystem fileSys = getFileSystem();
            FileStatus[] files = fileSys.listStatus(new Path(resultPath));
            if (files != null) {
                Vector<InputStream> vector = new Vector<InputStream>();
                for (FileStatus fs : files) {
                    Path filePath = fs.getPath();
                    if (filePath.getName().startsWith("part")) {
                        System.out.println("name : " + filePath.getName());
                        InputStream ins = fileSys.open(filePath);
                        vector.add(ins);
                    }
                }
                Enumeration<InputStream> enumer = vector.elements();
                sis = new SequenceInputStream(enumer);
            }
        }
        return sis;
    }

    public List<String> listHdfsFilenames(String path) throws IOException {
        List<String> names = new ArrayList<String>();
        if (path != null) {
            FileSystem fileSys = getFileSystem();
            FileStatus[] files = fileSys.listStatus(new Path(path));
            if (files != null) {
                for (FileStatus fs : files) {
                    Path filePath = fs.getPath();
                    names.add(filePath.getName());
                }
            }
        }
        return names;
    }
    
}
