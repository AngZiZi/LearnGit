package com.la.testftp.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("test")
public class test {

    private static final int  BUFFER_SIZE = 2 * 1024;

    @GetMapping("/test1")
    public String test1() throws Exception {
        List<String> aa = new ArrayList<>();
        aa.add("ftp://172.39.8.86/DPSStorageFolder//2019-09-21//bcs//spectrumChart_20190921102900.jpg.2110.picture");
        String pathin = downFTP(aa);

        String pathout = System.getProperty("user.dir");

        String localDir1 = pathout + "/src/导出压缩文件.zip";

        FileOutputStream fos1 = new FileOutputStream(new File(localDir1));

        toZip(pathin,fos1,true);

        //删除文件
        File file = new File(pathin);
        delFile(file);

        return pathin;
    }

    @GetMapping("/test2")
    public String test2() throws Exception {
        String pathout = System.getProperty("user.dir");

        String localDir1 = pathout + "/src/090904";

        File file = new File(localDir1);

        delFile(file);

        return localDir1;
    }

    @GetMapping("/test3")
    public void test3() throws Exception {
        InetAddress address2 = InetAddress.getByName("172.39.8.227");
        System.out.println(String.format("计算机名称为：%s",address2.getHostName()));
        System.out.println(String.format("计算机IP为：%s",address2.getHostAddress()));
    }

    @GetMapping("/test4")
    public void test4() throws Exception {
        URL imooc = new URL("https://www.imooc.com/");
        URL url = new URL(imooc, "/index.html?username=tom#test");
        System.out.println(url.getProtocol());
        System.out.println(url.getHost());
        System.out.println(url.getPort());
        System.out.println(url.getPath());
        System.out.println(url.getFile());
    }

    @GetMapping("/test5")
    public void test5() throws Exception {
        URL url = new URL("https://www.baidu.com/");
        InputStream is = url.openStream();
        InputStreamReader isr = new InputStreamReader(is,"utf-8");
        BufferedReader br = new BufferedReader(isr);
        String data = br.readLine();
        while (data != null) {
            System.out.println(data);
            data = br.readLine();
        }
        br.close();
        isr.close();
        is.close();
    }

    public static String downFTP(List<String> ftpPaths) throws Exception {
        //路径
        String path = System.getProperty("user.dir");
        SimpleDateFormat measureDateFormat = new SimpleDateFormat("HHMMss");
        String measureDate = measureDateFormat.format(new Date());

        String localDir1 = path + "/src/" + measureDate;
        for (String ftpPath : ftpPaths) {
            StringBuffer stringBuffer = new StringBuffer(ftpPath);
            stringBuffer.insert(6, "admin1:admin1@");
            URL url = new URL(stringBuffer.toString());
            URLConnection conn = url.openConnection();

            InputStream is = conn.getInputStream();
            List<Byte> data = new ArrayList<>();

            int bufferSize = 128;
            while (true) {
                byte[] buffer = new byte[bufferSize];
                int read = is.read(buffer);
                if (read == -1) {
                    break;
                }
                for (int i = 0; i < read; i++) {
                    data.add(buffer[i]);
                }
            }
            File file1 = new File(localDir1);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            //导出文件名
            String localDir2 = localDir1 + "/" + new Random();

            File file2 = new File(localDir2);
            if (file2.exists()) {
                file2.delete();
            }

            byte[] myData = new byte[data.size()];

            for (int i = 0; i < data.size(); i++) {
                myData[i] = data.get(i);
            }

            FileOutputStream fos = new FileOutputStream(file2);
            fos.write(myData, 0, myData.length);
            fos.flush();
            fos.close();
        }
        return localDir1;
    }




    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)
        throws RuntimeException{
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void toZip(List<File> srcFiles , OutputStream out)throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }
                }
            }
        }
    }

    static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }

//     public static void zipFile(String inputFile, String outputFile) throws IOException {
//        //Assign the original file : file to
//        //FileInputStream for reading data
//        FileInputStream fis = new FileInputStream(inputFile);
//
//        //Assign compressed file:file2 to FileOutputStream
//        FileOutputStream fos = new FileOutputStream(outputFile);
//
//        //Assign FileOutputStream to DeflaterOutputStream
//        DeflaterOutputStream dos = new DeflaterOutputStream(fos);
//
//        //read data from FileInputStream and write it into DeflaterOutputStream
//        int data;
//        while ((data = fis.read()) != -1) {
//            dos.write(data);
//        }
//
//        //close the file
//        fis.close();
//        dos.close();
//    }
}
