package io.jokester.tidyj_android_demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.jokester.tidyj.TidyDoc;
import io.jokester.tidyj.TidyJ;
import io.jokester.tidyj.TidyJException;

public class MainActivity extends AppCompatActivity {

    private ListView textList;
    private MessageAdapter results;
    private Button startButton;

    private Thread computeThread;

    public static final String[] assetFilenames = new String[]{
            "bunengkan.html",
            "topic-342254.html",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textList = findViewById(R.id.benchmark_result);

        startButton = findViewById(R.id.start_benchmark);
        startButton.setOnClickListener(view -> startBenchMark());

        textList.setAdapter(results = new MessageAdapter(
                this, 0));
    }

    @UiThread
    void startBenchMark() {
        if (computeThread != null) {
            throw new IllegalStateException("compute thread already exists");
        }

        Thread t = new Thread() {
            @Override
            public void run() {
                runBenchmark();
                onBenchmarkFinish();
            }
        };
        startButton.setEnabled(false);
        (computeThread = t).start();
    }

    void benchmarkString1(String assetFilename) {
        appendMessage("START: benchmarkString1 " + assetFilename);

        try {
            String s = readString(assetFilename);
            appendMessage("READ: benchmarkString1 " + assetFilename);

            try (TidyDoc d = TidyJ.parseString(s)) {
                appendMessage("FINISH: benchmarkString1 " + assetFilename);
            }
        } catch (IOException | TidyJException e) {
            appendMessage("ERROR: benchmarkString1 " + assetFilename);
            e.printStackTrace();
        }
    }

    void benchmarkString2(String assetFilename) {
        appendMessage("START: benchmarkString2 " + assetFilename);

        try {
            String s = readString(assetFilename);
            appendMessage("READ: benchmarkString2 " + assetFilename);

            Jsoup.parse(s);
            appendMessage("FINISH: benchmarkString2 " + assetFilename);
        } catch (IOException e) {
            appendMessage("ERROR: benchmarkString2 " + assetFilename);
            e.printStackTrace();
        }
    }

    void benchmarkStream1(String assetFilename) {
        appendMessage("START: benchmarkStream1 " + assetFilename);
        try {
            InputStream s = new BufferedInputStream(
                    getAssets().open(assetFilename));
            try (TidyDoc d = TidyJ.parseStream(s)) {
                appendMessage("FINISH: benchmarkStream1 " + assetFilename);
            }
        } catch (IOException | TidyJException e) {
            appendMessage("ERROR: benchmarkStream1 " + assetFilename);
            e.printStackTrace();
        }
    }

    void benchmarkStream2(String assetFilename) {
        appendMessage("START: benchmarkStream2 " + assetFilename);
        try {
            InputStream s = new BufferedInputStream(
                    getAssets().open(assetFilename));
            Jsoup.parse(s, "utf-8", "https://example.com");
            appendMessage("FINISH: benchmarkStream2 " + assetFilename);
        } catch (IOException e) {
            appendMessage("ERROR: benchmarkStream2 " + assetFilename);
            e.printStackTrace();
        }
    }

    String readString(String assetFilename) throws IOException {
        String s = readStream(
                getAssets().open(assetFilename));
        return s;
    }

    static String readStream(InputStream i) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(i));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }

    void appendMessage(final String s) {
        textList.post(() -> results.add(new MessageAdapter.Message(s)));
    }

    @SuppressLint("DefaultLocale")
    void runBenchmark() {

        textList.post(() -> results.clear());

        for (String assertFilename : assetFilenames) {
            benchmarkString2(assertFilename);
            benchmarkString1(assertFilename);
            benchmarkStream2(assertFilename);
            benchmarkStream1(assertFilename);
        }
    }

    void onBenchmarkFinish() {
        final Thread t = computeThread;

        if (t == null) {
            throw new IllegalStateException("compute thread not exist");
        }

        textList.post(() -> {
            try {
                t.join();
                computeThread = null;
                startButton.setEnabled(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
