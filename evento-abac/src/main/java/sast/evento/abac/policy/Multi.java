package sast.evento.abac.policy;

import sast.evento.abac.attribute.Attribute;


public abstract class Multi implements Policy {

    public static Policy and(Policy first, Policy second){
        return new And(first,second);
    }

    public static Policy or(Policy first, Policy second){
        return new Or(first,second);
    }

    protected final Policy first;
    protected final Policy second;

    protected Multi(Policy first, Policy second) {
        this.first = first;
        this.second = second;
    }

    static class And extends Multi{
        private And(Policy first, Policy second) {
            super(first, second);
        }

        @Override
        public boolean match(Attribute attribute) {
            return first.match(attribute) && second.match(attribute);
        }
    }

    static class Or extends Multi{
        private Or(Policy first, Policy second) {
            super(first, second);
        }

        @Override
        public boolean match(Attribute attribute) {
            return first.match(attribute) || second.match(attribute);
        }
    }


}
