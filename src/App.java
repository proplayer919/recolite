package org.fifly.recolite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.Reader.*;
import java.io.Writer.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class App {
    List<String> filesListInDir = new ArrayList<String>();

    public static void Main(String[] args) {
        if (args.length == 0 || args.length > 2 || args.length < 2) {
            System.out.println("Little, none or too many arguments provided.");
            // quit
            return;
        } else if(args.length == 2) {
            if(args[0] == "run") {
                runProgramFromRecolite(args[1]);
            } else if(args[0] == "compile") {
                compileProjectFromDir(args[1], "test");
            } else {
                System.out.println("Invalid argument provided.");
                // quit
                return;
            }
        } else {
            return;
        }
    }

    public static void runProgramFromRecolite(String path) {
        unzip(path, "C:\\Recolite\\Projects");
    }

    public static void compileProjectFromDir(String path, String name) {
        zipDirectory(new File(path), "C:\\Recolite\\Projects\\" + name + ".zip");
    }

    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    private void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : filesListInDir){
                System.out.println("Zipping "+filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()) filesListInDir.add(file.getAbsolutePath());
            else populateFilesList(file);
        }
    }

    /**
     * This method compresses the single file to zip format
     * @param file
     * @param zipFileName
     */
    private static void zipSingleFile(File file, String zipFileName) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //add a new Zip Entry to the ZipOutputStream
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            //read the file and write to ZipOutputStream
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            
            //Close the zip entry to write to zip file
            zos.closeEntry();
            //Close resources
            zos.close();
            fis.close();
            fos.close();
            System.out.println(file.getCanonicalPath()+" is zipped to "+zipFileName);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}