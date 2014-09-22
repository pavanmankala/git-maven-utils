package org.apache.git.maven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestChildRedirection {

    @Test
    public void testChildRedirection() {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(new String[] {"git", "log"});
        builder.directory(new File("/home/mankala/work/eclipse-luna-jee/DockingFrames"));
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException ex) {
            // --
        }
        OutputStream os = process.getOutputStream();
        final PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));

        final InputStream is = process.getInputStream();
        new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (java.io.IOException e) {
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = -1;
                    while ((i = System.in.read()) != -1) {
                        pw.write(i);
                        pw.flush();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                pw.close();
            }
        }) {
            {
                setDaemon(true);
            };
        }.start();

        int returnCode = -1;
        try {
            returnCode = process.waitFor();
        } catch (InterruptedException ex) {
            // --
        }
        System.out.println("Exit code" + returnCode);
    }

    public static void main(String[] args) {
        TestChildRedirection tcr = new TestChildRedirection();
        tcr.testChildRedirection();
    }
}
