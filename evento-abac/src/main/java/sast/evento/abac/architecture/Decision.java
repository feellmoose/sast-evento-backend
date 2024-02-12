package sast.evento.abac.architecture;

import sast.evento.abac.attribute.Attribute;
import sast.evento.abac.policy.Policies;

class Decision implements PolicyPoint{
    private volatile static Decision decision;
    private final Policies policies = new Policies();

    static Decision getInstance() {
        if(decision == null){
            synchronized (Decision.class){
                if(decision == null){
                    decision = new Decision();
                }
            }
        }
        return decision;
    }

    boolean decide(Attribute attribute){
        return policies.match(attribute);
    }

}
