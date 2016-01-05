/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 *
 * @author rene
 */
public class FTPHelper {

    /**
     * Sends a filestream to the ftp server specified in application.cfg.xml
     *
     * @param inputStream the stream to send
     * @param remoteFilePath remote path on ftp server, full path will be
     * created if not exists
     * @param remoteFileName remote filename, will overwrite if exists!
     * @return true if transfer successful
     */
    public static boolean SendFile(InputStream inputStream, String remoteFilePath, String remoteFileName) {
        boolean success = false;
        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(ConfigurationHelper.getFTPServerAdress(), ConfigurationHelper.getFTPServerPort());
            ftpClient.login(ConfigurationHelper.getFTPUserName(), ConfigurationHelper.getFTPPassword());
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //apache maakt zelf geen directories aan...
            ftpCreateDirectoryTree(ftpClient, remoteFilePath);
            if (ftpClient.changeWorkingDirectory(remoteFilePath)) {
                success = ftpClient.storeFile(remoteFileName, inputStream);
                if (success) {
                    Logger.getLogger(FTPHelper.class.getName()).log(Level.SEVERE, null,
                            remoteFileName + " uploaded succesfully!");
                }
            }
            inputStream.close();

        } catch (IOException ex) {
            Logger.getLogger(FTPHelper.class.getName()).log(Level.SEVERE, null,
                    ex.toString());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                Logger.getLogger(FTPHelper.class.getName()).log(Level.SEVERE, null,
                        ex.toString());
            }
        }
        return success;
    }

    /**
     * Sends a local file to the ftp server specified in application.cfg.xml
     *
     * @param localFile full path of local file
     * @param remoteFilePath remote path on ftp server, full path will be
     * created if not exists
     * @param remoteFileName remote filename, will overwrite if exists!
     * @return true if transfer successful
     * @throws FileNotFoundException if local file not exists
     */
    public static boolean SendFile(String localFile, String remoteFilePath, String remoteFileName) throws FileNotFoundException {

        InputStream inputStream = new FileInputStream(localFile);
        return SendFile(inputStream, remoteFilePath, remoteFileName);
    }

    private static void ftpCreateDirectoryTree(FTPClient client, String dirTree) throws IOException {

        boolean dirExists = true;

        //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
        String[] directories = dirTree.split("\\\\");
        for (String dir : directories) {
            if (!dir.isEmpty()) {
                if (dirExists) {
                    dirExists = client.changeWorkingDirectory(dir);
                }
                if (!dirExists) {
                    if (!client.makeDirectory(dir)) {
                        throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                    if (!client.changeWorkingDirectory(dir)) {
                        throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + client.getReplyString() + "'");
                    }
                }
            }
        }
    }
}
