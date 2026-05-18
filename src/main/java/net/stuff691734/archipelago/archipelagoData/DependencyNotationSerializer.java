package net.stuff691734.archipelago.archipelagoData;

import com.google.gson.*;

import java.lang.reflect.Type;

public class DependencyNotationSerializer implements JsonSerializer<DependencyNotation> {

    @Override
    public JsonElement serialize(DependencyNotation src, Type typeOfSrc, JsonSerializationContext context) {
        DependencyNotation input = src;
        if (src.checks.isEmpty() && src.nested.size() == 1) {
            input = src.nested.get(0);
        }
        if (input.minimum != 0) {
            JsonObject output = new JsonObject();
            output.addProperty("minimum", input.minimum);

            JsonArray array = new JsonArray();
            input.checks.forEach(array::add);
            input.nested.forEach((dependency) -> {
                if (!dependency.isEmpty()) {
                    array.add(serialize(dependency, dependency.getClass(), context));
                }
            });
            output.add("checks", array);

            return output;
        } else {
            JsonArray output = new JsonArray();

            input.checks.forEach(output::add);
            input.nested.forEach((dependency) -> {
                if (!dependency.isEmpty()) {
                    output.add(serialize(dependency, dependency.getClass(), context));
                }
            });

            return output;
        }
    }
}
