package com.kalita.app;

import com.kalita.context.Context;
import com.kalita.context.annotation.PackageScan;

@PackageScan(packageName = "com.kalita")
public class Application {

    public static void main(String[] args) {
        Context.run(Application.class);
    }
}