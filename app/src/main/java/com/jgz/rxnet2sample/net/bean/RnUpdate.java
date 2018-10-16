package com.jgz.rxnet2sample.net.bean;

/**
 * Created by Administrator on 2018/3/26.
 */

public class RnUpdate {

    private String hash;
    private long fileSize;
    private String createDate;
    private String updateType;//更新状态 1.部署成功 2.灰度测试 3.发布 4.失败 = ['1', '2', '3', '4'],
    private String version;
    private String versionMessage;
    private String versionUrl;

    /**add By Je 2018/05/10*/
    private String assetsHash;          // assets文件hash ,
    private String bundleHash;          // bundle文件hash ,
    private long patchFileSize;       // 文件大小
    private String patchHash;           // 补丁文件hash
    private String patchUrl;            //补丁包下载地址


    public String getAssetsHash() {
        return assetsHash;
    }

    public String getBundleHash() {
        return bundleHash;
    }

    public long getPatchFileSize() {
        return patchFileSize;
    }

    public String getPatchHash() {
        return patchHash;
    }

    public String getPatchUrl() {
        return patchUrl;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionMessage() {
        return versionMessage;
    }

    public void setVersionMessage(String versionMessage) {
        this.versionMessage = versionMessage;
    }

    public String getVersionUrl() {
        return versionUrl;
    }

    public void setVersionUrl(String versionUrl) {
        this.versionUrl = versionUrl;
    }

    @Override
    public String toString() {
        return "RnUpdate{" +
                "hash='" + hash + '\'' +
                ", fileSize=" + fileSize +
                ", createDate='" + createDate + '\'' +
                ", updateType='" + updateType + '\'' +
                ", version='" + version + '\'' +
                ", versionMessage='" + versionMessage + '\'' +
                ", versionUrl='" + versionUrl + '\'' +
                '}';
    }
}
