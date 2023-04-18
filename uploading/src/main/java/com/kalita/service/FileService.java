package com.kalita.service;

import java.io.File;
import java.io.IOException;

public interface FileService {


    File getWholeFile(Long segmentId) throws IOException;

    File getPartsFile(Long segmentId) throws IOException;
}
