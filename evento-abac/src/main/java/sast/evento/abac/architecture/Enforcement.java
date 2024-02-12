package sast.evento.abac.architecture;

import sast.evento.abac.attribute.Attribute;

import java.util.function.Supplier;

public class Enforcement implements PolicyPoint{
    private final Decision decision = Decision.getInstance();
    private final Attribute attribute;

    private Enforcement(Attribute attribute){
        this.attribute = attribute;
    }

    public static Enforcement collect(Supplier<Attribute> supplier){
        return new Enforcement(supplier.get());
    }

    public void enforce(){
        //TODO throw exception
        if(!decision.decide(this.attribute))
            throw new RuntimeException();
    }


}
