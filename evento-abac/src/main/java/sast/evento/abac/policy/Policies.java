package sast.evento.abac.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sast.evento.abac.attribute.Attribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Policies implements Policy {
    private final Map<String, Policy> holder = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Policies.class);
    public static Policy EMPTY = attribute -> true;
    public void register(String resource, Policy policy){
        if(holder.containsKey(resource)){
            throw new IllegalArgumentException("only one strategy can be registered for a resource,use strategy.Multi.class if you want to use more than one.");
        }
        holder.put(resource,policy);
    }

    public void remove(String resource){
        holder.remove(resource);
    }

    @Override
    public boolean match(Attribute attribute) {
        var name = attribute.resource().name();
        var strategy = holder.get(name);
        if(strategy == null) {
            logger.warn("no resource matches, please check and register empty strategy for the resource:{}",name);
            return false;
        }
        return strategy.match(attribute);
    }

}