package sast.evento.abac.policy;

import sast.evento.abac.attribute.Attribute;

public abstract class Single implements Policy {
    protected Policy strategy;

    protected Single(Policy strategy){
        this.strategy = strategy;
    }

    public static Policy not(Policy strategy){
        return new Not(strategy);
    }

    static class Not extends Single{

        protected Not(Policy strategy) {
            super(strategy);
        }

        @Override
        public boolean match(Attribute attribute) {
            return !strategy.match(attribute);
        }
    }
}
