package com.example.spectraoil;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

public class OilPredictionModel {

    private static OrtEnvironment env;
    private static OrtSession session;
    private static String inputName;

    // ===== INIT MODEL =====
    public static void init(Context context) {
        try {
            env = OrtEnvironment.getEnvironment();

            InputStream is = context.getAssets().open("model.onnx");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int nRead;

            while ((nRead = is.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }

            byte[] modelBytes = buffer.toByteArray();
            is.close();

            session = env.createSession(modelBytes, new OrtSession.SessionOptions());

            inputName = session.getInputNames().iterator().next();

            Log.d("ONNX", "Model loaded");
            Log.d("ONNX", "Input name: " + inputName);
            Log.d("ONNX", "Inputs: " + session.getInputNames());
            Log.d("ONNX", "Outputs: " + session.getOutputNames());

        } catch (Exception e) {
            Log.e("ONNX", "Model init error", e);
        }
    }

    // ===== PREDICT =====
    public static float predict(float[] sensorData) {

        try {
            if (session == null) {
                Log.e("ONNX", "Session is null");
                return -1;
            }

            if (sensorData == null || sensorData.length != 12) {
                Log.e("ONNX", "Invalid input size");
                return -1;
            }

            // 🔥 DEBUG INPUT
            Log.d("ONNX", "Input values: " + Arrays.toString(sensorData));

            // ✅ Create proper 2D input (1 x 12)
            float[][] input = new float[1][12];
            for (int i = 0; i < 12; i++) {
                input[0][i] = sensorData[i];
            }

            // ✅ Compatible tensor creation
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, input);

            // ✅ Run inference
            OrtSession.Result result = session.run(
                    Collections.singletonMap(inputName, inputTensor)
            );

            Object value = result.get(0).getValue();

            float prediction;

            // ✅ Safe output handling
            if (value instanceof float[][]) {
                prediction = ((float[][]) value)[0][0];
            } else if (value instanceof float[]) {
                prediction = ((float[]) value)[0];
            } else {
                Log.e("ONNX", "Unknown output type: " + value.getClass());
                return -1;
            }

            Log.d("ONNX", "Prediction raw: " + prediction);

            inputTensor.close();
            result.close();

            return prediction;   // ⚠️ DO NOT multiply unless needed

        } catch (Exception e) {
            Log.e("ONNX", "Prediction error", e);
        }

        return -1;
    }

}
