package sast.evento.abac.attribute;

public record Action(Method method, Type type, String path, String description) {
    public enum Method{GET,POST,PATCH,PUT,DELETE}
    public enum Type{INVISIBLE, PUBLIC, LOGIN}
}



