package tisse.dto;

public enum EventKind {

    WI("Вход"), WO("Выход"), DI("На обед"), DO("С обеда"), PE("Ошибка"), FE("Ошибка");

    private String label;

    EventKind(String s) {
        label = s;
    }

    public String getLabel() {
        return label;
    }
}
