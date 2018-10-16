package com.jgz.rxnet2sample.net.bean;

/**
 * Created by yjh on 2018/3/26.
 * 初始化
 */

public class InitData {

    private String channelVersionControl;
    private String deprecated;
    private boolean ipIsCn;
    private String newVersion;
    private String newVersionMessage;
    private String newVersionUrl;
    private String publicIp;
    private RnUpdate rnUpdate;
    private boolean safeguard;
    private String safeguardMessage;

    public String getChannelVersionControl() {
        return channelVersionControl;
    }

    public void setChannelVersionControl(String channelVersionControl) {
        this.channelVersionControl = channelVersionControl;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(String deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isIpIsCn() {
        return ipIsCn;
    }

    public void setIpIsCn(boolean ipIsCn) {
        this.ipIsCn = ipIsCn;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getNewVersionMessage() {
        return newVersionMessage;
    }

    public void setNewVersionMessage(String newVersionMessage) {
        this.newVersionMessage = newVersionMessage;
    }

    public String getNewVersionUrl() {
        return newVersionUrl;
    }

    public void setNewVersionUrl(String newVersionUrl) {
        this.newVersionUrl = newVersionUrl;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public RnUpdate getRnUpdate() {
        return rnUpdate;
    }

    public void setRnUpdate(RnUpdate rnUpdate) {
        this.rnUpdate = rnUpdate;
    }

    public boolean isSafeguard() {
        return safeguard;
    }

    public void setSafeguard(boolean safeguard) {
        this.safeguard = safeguard;
    }

    public String getSafeguardMessage() {
        return safeguardMessage;
    }

    public void setSafeguardMessage(String safeguardMessage) {
        this.safeguardMessage = safeguardMessage;
    }


    @Override
    public String toString() {
        return "InitData{" +
                "channelVersionControl='" + channelVersionControl + '\'' +
                ", deprecated=" + deprecated +
                ", ipIsCn=" + ipIsCn +
                ", newVersion='" + newVersion + '\'' +
                ", newVersionMessage='" + newVersionMessage + '\'' +
                ", newVersionUrl='" + newVersionUrl + '\'' +
                ", publicIp='" + publicIp + '\'' +
                ", rnUpdate=" + rnUpdate +
                ", safeguard=" + safeguard +
                ", safeguardMessage='" + safeguardMessage + '\'' +
                '}';
    }
}
