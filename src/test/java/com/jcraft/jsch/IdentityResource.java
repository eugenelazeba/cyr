package com.jcraft.jsch;

import org.apache.tika.io.IOUtils;

import java.io.IOException;

public class IdentityResource implements Identity {

    private IdentityFile identityFile;

    public IdentityResource(String name, String privateKey, JSch jsch) throws JSchException, IOException {
        byte[] privateKeyBytes = IOUtils.toByteArray(getClass().getResourceAsStream(privateKey));
        identityFile = IdentityFile.newInstance(name, privateKeyBytes, null, jsch);
    }

    @Override
    public boolean setPassphrase(byte[] passphrase) throws JSchException {
        return identityFile.setPassphrase(passphrase);
    }

    @Override
    public byte[] getPublicKeyBlob() {
        return identityFile.getPublicKeyBlob();
    }

    @Override
    public byte[] getSignature(byte[] data) {
        return identityFile.getSignature(data);
    }

    @Override
    public boolean decrypt() {
        return identityFile.decrypt();
    }

    @Override
    public String getAlgName() {
        return identityFile.getAlgName();
    }

    @Override
    public String getName() {
        return identityFile.getName();
    }

    @Override
    public boolean isEncrypted() {
        return identityFile.isEncrypted();
    }

    @Override
    public void clear() {
        identityFile.clear();
    }
}
