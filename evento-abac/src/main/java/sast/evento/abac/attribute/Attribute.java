package sast.evento.abac.attribute;

public record Attribute(Action action, Effect effect, Resource resource, Condition condition) {
}
