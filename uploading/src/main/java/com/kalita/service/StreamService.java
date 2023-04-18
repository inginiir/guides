package com.kalita.service;

import java.io.OutputStream;

public interface StreamService {

    void streamProfiles(OutputStream out, Long segmentId);
}
