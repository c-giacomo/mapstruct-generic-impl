package ${packageName};

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import com.generic.mapper.interfaces.BaseMapper;
import com.generic.mapper.model.SourceTarget;
import com.generic.mapper.interfaces.GenericAbstractMapper;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class GenericBeansMapper implements GenericAbstractMapper {

    <#list items as item>
    private final ${item.mapperType} mapper${item_index};
    </#list>

    private final Map<SourceTarget, BaseMapper> serializers;
    private final Map<SourceTarget, BaseMapper> deserializers;

    public GenericBeansMapper(
    <#list items as item>
        ${item.mapperType} mapper${item_index}<#if item_has_next>,</#if>
    </#list>
    ) {
        <#list items as item>
            this.mapper${item_index} = mapper${item_index};
        </#list>

        Map<SourceTarget, BaseMapper> ser = new HashMap<>();
        Map<SourceTarget, BaseMapper> deser = new HashMap<>();
        <#list items as item>
            ser.put(new SourceTarget(${item.sourceType}.class, ${item.targetType}.class), mapper${item_index});
            deser.put(new SourceTarget(${item.targetType}.class, ${item.sourceType}.class), mapper${item_index});
        </#list>
        this.serializers = Collections.unmodifiableMap(ser);
        this.deserializers = Collections.unmodifiableMap(deser);
    }

    @Override
    public <T> T serializza(Object from, Class<T> to) {
        BaseMapper mapper = serializers.get(new SourceTarget(from.getClass(), to));
        return mapper != null ? (T) mapper.serialize(from) : null;
    }

    @Override
    public <T> T deserializza(Class<T> to, Object from) {
        BaseMapper mapper = deserializers.get(new SourceTarget(from.getClass(), to));
        return mapper != null ? (T) mapper.deserialize(from) : null;
    }

    @Override
    public <T> T aggiorna(Object from, T to) {
        BaseMapper mapper = deserializers.get(new SourceTarget(from.getClass(), to.getClass()));
        return mapper != null ? (T) mapper.update(from, to) : null;
    }

    @Override
    public <T> T invoke(String methodName, Object from, Class<T> to) {
        <#list items as item>
            <#list item.methods as m>
                if ("${m.name}".equals(methodName)
                && ${m.parameterType}.class.isInstance(from)
                && to.isAssignableFrom(${m.returnType}.class)) {
                return (T) mapper${item_index}.${m.name}((${m.parameterType}) from);
                }
            </#list>
        </#list>
        throw new IllegalArgumentException("No method '" + methodName + "' for " + from.getClass().getName() + " -> " + to.getName());
    }

    @Override
    public boolean hasRegisteredMapper(Class from, Class to) {
        SourceTarget key = new SourceTarget(from, to);
        return serializers.containsKey(key) || deserializers.containsKey(key);
    }
}