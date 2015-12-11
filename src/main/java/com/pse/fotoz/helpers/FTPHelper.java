/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.helpers;

import java.io.File;
import java.io.FileInputStream;
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

    public static void SendFile(String localFile, String remoteFilePath, String remoteFileName) {

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(ConfigurationHelper.getFTPServerAdress(), ConfigurationHelper.getFTPServerPort());
            ftpClient.login(ConfigurationHelper.getFTPUserName(), ConfigurationHelper.getFTPPassword());
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File firstLocalFile = new File(localFile);

            InputStream inputStream = new FileInputStream(firstLocalFile);
            System.out.println("Start uploading " + localFile);
            //apache maakt zelf geen directories aan...
            ftpCreateDirectoryTree(ftpClient, remoteFilePath);
            if (ftpClient.changeWorkingDirectory(remoteFilePath)) {
                boolean done = ftpClient.storeFile(remoteFileName, inputStream);
                if (done) {
                    Logger.getLogger(FTPHelper.class.getName()).log(Level.SEVERE, null,
                            localFile + " uploaded succesfully!");
                }
            }
            inputStream.close();

        } catch (IOException ex) {
            Logger.getLogger(FTPHelper.class.getName()).log(Level.SEVERE, null,
                    localFile + ex.toString());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                Logger.getLogger(FTPHelper.class.getName()).log(Level.SEVERE, null,
                        localFile + ex.toString());
            }
        }
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
