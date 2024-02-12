package sast.evento.abac.policy;

import sast.evento.abac.attribute.Attribute;

public interface Policy {
    boolean match(Attribute attribute);
}
