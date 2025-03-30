package org.dopamine.service;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TotalService {
    public Map<String, Object> queryAllResult(File filePath) throws Exception;
    public Map<String, Object> queryHSAllResult(File filePath) throws Exception;
}
