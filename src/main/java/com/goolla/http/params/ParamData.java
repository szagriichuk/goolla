package com.goolla.http.params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author szagriichuk.
 */
public class ParamData extends HashMap<String, Object> {
    private ParamData() {
    }

    private ParamData(ParamData veroData) {
        super(veroData);
    }

    public static ParamDataBuilder of(Param<?> param) {
        return new ParamDataBuilder().of(param);
    }

    public static ParamData empty() {
        return new ParamData();
    }

    public final <T> ParamData add(Param<T> param) {
        ParamData vero = new ParamData(this);
        vero.put(param.name(), param.value);
        return vero;
    }

    public final ParamData addAll(Param<?>... params) {
        ParamData vero = new ParamData(this);
        for (Param<?> param : params) {
            vero = vero.add(param);
        }
        return vero;
    }

    public static class ParamDataBuilder {
        private List<Param<?>> params = new ArrayList<>();

        public ParamDataBuilder of(Param<?> param) {
            params.add(param);
            return this;
        }

        public ParamData build() {
            return new ParamData().addAll(params.toArray(new Param<?>[params.size()]));
        }
    }
}