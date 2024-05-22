package com.itgnostic.test4sandbox.utils;

import com.itgnostic.test4sandbox.service.OperationResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static JSONArray collectionToJson(Collection<?> collection) {
        JSONArray out = new JSONArray();

        for (Object o : collection) {
            if (o instanceof Boolean b)
                out.put(b);
            else if (o instanceof String s)
                out.put(s);
            else if (o instanceof Number n)
                out.put(n);
            else if (o instanceof Map<?, ?> m)
                out.put(mapToJson(m));
            else if (o instanceof Collection<?> c)
                out.put(collectionToJson(c));
        }

        return out;
    }

    public static JSONObject mapToJson(Map<?, ?> map) {
        JSONObject out = new JSONObject();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() instanceof String key) {
                Object value = entry.getValue();

                if (value instanceof Boolean b)
                    out.put(key, b);
                else if (value instanceof String s)
                    out.put(key, s);
                else if (value instanceof Number n)
                    out.put(key, n);
                else if (value instanceof Map<?, ?> m)
                    out.put(key, mapToJson(m));
                else if (value instanceof Collection<?> c)
                    out.put(key, collectionToJson(c));
            }
        }

        return out;
    }

    public static JSONObject operationResultToJson(OperationResult operationResult, List<String> errors) {
        JSONObject out = new JSONObject();

        if (operationResult.isSuccess()) {
            out.put("result", operationResult.getResultList().isEmpty()
                    ? "success" : new JSONArray(operationResult.getResultsAsList()));

            if (operationResult.hasErrors())
                out.put("warning", operationResult.getErrorDetails());
        }
        else if (!errors.isEmpty())
            out.put("errors", new JSONArray(errors));

        return out;
    }

}
