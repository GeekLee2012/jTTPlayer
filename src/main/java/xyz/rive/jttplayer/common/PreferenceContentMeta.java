package xyz.rive.jttplayer.common;

import xyz.rive.jttplayer.controller.CommonController;

public class PreferenceContentMeta {
    private String navTitle;
    private String contentTitle;
    private String resource;
    private Class<? extends CommonController> controllerClass;

    public PreferenceContentMeta() {

    }

    public PreferenceContentMeta(String navTitle, String contentTitle, String resource, Class<? extends CommonController> controllerClass) {
        this.navTitle = navTitle;
        this.contentTitle = contentTitle;
        this.resource = resource;
        this.controllerClass = controllerClass;
    }

    public String getNavTitle() {
        return navTitle;
    }

    public void setNavTitle(String navTitle) {
        this.navTitle = navTitle;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public Class<? extends CommonController> getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(Class<? extends CommonController> controllerClass) {
        this.controllerClass = controllerClass;
    }
}
