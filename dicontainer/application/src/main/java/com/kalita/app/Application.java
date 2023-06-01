package com.kalita.app;

import com.kalita.context.Context;
import com.kalita.context.annotation.PackageScan;

import java.io.IOException;

@PackageScan(packageName = "com.kalita")
public class Application {

    public static void main(String[] args) throws IOException {
        Context.run(Application.class);
    }
}