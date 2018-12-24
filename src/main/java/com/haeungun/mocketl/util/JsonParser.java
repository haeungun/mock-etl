package com.haeungun.mocketl.util;

import com.haeungun.mocketl.exceptions.MockEtlJsonConvertException;
import org.json.JSONArray;

public class JsonParser {

    public JSONArray strToJsonArr(String str) throws MockEtlJsonConvertException {
        try {
            return new JSONArray(str);
        } catch (Exception e) {
            throw new MockEtlJsonConvertException(str);
        }
    }

}
