package tisse.controller;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class BaseBean {

    @Inject
    protected FacesContext facesContext;

    public abstract String getTitle();

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) facesContext.getExternalContext().getRequest();
    }

    protected HttpSession getSession() {
        return this.getRequest().getSession();
    }

    protected HttpServletResponse getResponse() {
        return ((HttpServletResponse) facesContext.getExternalContext().getResponse());
    }

    protected String getContextPath() {
        return getSession().getServletContext().getContextPath();
    }

    protected void redirect(String target) {
        NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
        nh.handleNavigation(facesContext, null, target + "?faces-redirect=true");
    }

    protected void redirect(String target, String param) {
        NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
        nh.handleNavigation(facesContext, null, target + "?" + param + "&faces-redirect=true");
    }

    protected void errorMessage(String message){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка!", message));
    }

    protected void infoMessage(String message){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK!", message));
    }

}
