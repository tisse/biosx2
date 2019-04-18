package tisse.service.ftp;

public class FTPAuthInfo {

    private String url;
    private String login;
    private String pass;
    private String serverName;
    private int port;

    public FTPAuthInfo(String serverName, String url, int port, String login, String pass) {
        this.url = url;
        this.login = login;
        this.pass = pass;
        this.serverName = serverName;
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }
}
