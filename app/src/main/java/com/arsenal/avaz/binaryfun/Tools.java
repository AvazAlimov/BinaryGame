package com.arsenal.avaz.binaryfun;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

class Tools {
    static Context context;
    static int gameMode = 4;
    static String best4 = "null";
    static String best6 = "null";
    static String best8 = "null";
    static boolean isFirst = true;
    static boolean isZeros = true;
    static boolean isHighlight = true;
    static boolean isTimerVisible = true;

    static void createDatabase() throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("database.txt");
        File file = context.getFileStreamPath("database.txt");
        OutputStream outputStream = new FileOutputStream(file);

        ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);

        ReadableByteChannel ich = Channels.newChannel(inputStream);
        WritableByteChannel och = Channels.newChannel(outputStream);

        while (ich.read(buffer) > -1 || buffer.position() > 0) {
            buffer.flip();
            och.write(buffer);
            buffer.compact();
        }
        ich.close();
        och.close();
    }

    static void readData() throws IOException {
        File file = context.getFileStreamPath("database.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String data = reader.readLine();
        isFirst = Boolean.valueOf(data.substring(data.indexOf("<firstTime>"), data.indexOf("</firstTime>")));
        best4 = data.substring(data.indexOf("<4>") + 3, data.indexOf("</4>"));
        best6 = data.substring(data.indexOf("<6>") + 3, data.indexOf("</6>"));
        best8 = data.substring(data.indexOf("<8>") + 3, data.indexOf("</8>"));
        isZeros = Boolean.valueOf(data.substring(data.indexOf("<s1>") + 4, data.indexOf("</s1>")));
        isHighlight = Boolean.valueOf(data.substring(data.indexOf("<s2>") + 4, data.indexOf("</s2>")));
        isTimerVisible = Boolean.valueOf(data.substring(data.indexOf("<s3>") + 4, data.indexOf("</s3>")));
        reader.close();
    }

    static void writeData() throws IOException {
        File f = context.getFileStreamPath("database.txt");
        FileWriter fw = new FileWriter(f);
        fw.write("<firstTime>false</firstTime><4>" + best4 + "</4><6>" + best6 + "</6><8>" + best8 + "</8><s1>" + isZeros + "</s1><s2>" + isHighlight + "</s2><s3>" + isTimerVisible + "</s3>\n");
        fw.close();
    }
}
