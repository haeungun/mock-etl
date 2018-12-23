package com.example.mocketl.util;

import java.io.IOException;
import java.util.Map;

public interface IRequest {
    String requestGet(String path, Map<String, String> header) throws IOException;
}
