package bdd.steps;

import com.jcraft.jsch.*;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.io.FilenameUtils;

import java.io.IOException;
import java.util.List;

import static bdd.steps.TestData.PATH_RESOURCES;

@Slf4j
public class SFTP {
    private static final Config ftpConfig = ConfigFactory.load().getConfig("ftp");
    private JSch sftpClient;

    @Before
    public void setUp() throws Exception {
        this.sftpClient = new JSch();

        if (StringUtils.isNoneBlank(ftpConfig.getString("privateKeyFile"))) {
            IdentityResource identityResource = new IdentityResource(
                    "sftp",
                    ftpConfig.getString("privateKeyFile"),
                    sftpClient);
            this.sftpClient.addIdentity(identityResource, null);

        }
    }

    private void putFile(List<String> files, ChannelSftp channel, String remotePath) throws SftpException {
        for (String file : files) {
            String fileName = "/" + FilenameUtils.getName(file);
            channel.put(PATH_RESOURCES + file, remotePath + fileName);

        }
    }

    private Session connect(Config ftpConfig) throws IOException {
        try {
            Session session = sftpClient.getSession(ftpConfig.getString("user"), ftpConfig.getString("host"),
                    ftpConfig.getInt("port"));
            session.setPassword(ftpConfig.getString("password"));
            if (StringUtils.isBlank(ftpConfig.getString("knownHostsFile"))) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
            session.connect();
            return session;
        } catch (JSchException e) {
            throw new IOException(e);
        }
    }

    private String normalizePath(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }

    @Given("^CRM data (\\S+) was uploaded to SFTP in (\\S+)$")
    public void uploadFileToSftp(List<String> files, String folder) throws Exception {
        Session session = connect(ftpConfig);
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");

        try {
            channel.connect();
            String remotePath = channel.realpath(folder);
            putFile(files, channel, remotePath);
        } finally {
            channel.disconnect();
            session.disconnect();
        }
        log.info("Put file on SFTP :" + files);
        Thread.sleep(60000);
    }
}
