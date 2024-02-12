package sast.evento.abac.attribute;

public record Resource(Type type,Integer id,String name,String description) {
    public enum Type{EVENT,PERMISSION,SLIDE,LOCATION,CODE,FEEDBACK,USER,NONE}
}

