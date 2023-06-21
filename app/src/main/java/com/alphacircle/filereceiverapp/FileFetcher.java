package com.alphacircle.filereceiverapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileFetcher {

    private FileFetchListener fileFetchListener;
    private Context context;

    public FileFetcher(FileFetchListener listener, Context context) {
        this.fileFetchListener = listener;
        this.context = context;
    }

    public void fetchFiles() {
        new FetchFilesTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchFilesTask extends AsyncTask<Void, Void, String> {
        // 애뮬레이터의 로컬 호스트 IP는 127.0.0.1, 때문에 서버 URL을 따로 표기해 주어야 함.
        private static final String SERVER_URL = "http://121.133.180.56:4000/files";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return requestFileInfo();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                fileFetchListener.onFilesFetched(parseResponse(response));
            } else {
                handleErrorResponse("Failed to fetch files");
            }
        }

        private String requestFileInfo()  throws IOException {
            HttpURLConnection connection = createConnection(SERVER_URL);
            connection.setRequestMethod("GET");
            Log.i("requestFileInfo", "connection success");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                String response = readResponse(inputStream);
                inputStream.close();
                connection.disconnect();
                return response;
            } else {
                String errorMessage = handleErrorResponseCode(responseCode);
                handleErrorResponse(errorMessage);
                return null;
            }
        }

        private HttpURLConnection createConnection(String url) throws IOException {
            URL serverUrl = new URL(url);
            return (HttpURLConnection) serverUrl.openConnection();
        }

        private String readResponse(InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);

            reader.close();
            return response.toString();
        }

        private List<FileInfo> parseResponse(String response) {
            List<FileInfo> files = new ArrayList<>();

            try {
                JSONObject responseObject = new JSONObject(response);
                JSONArray filesArray = responseObject.getJSONArray("data");

                for (int i = 0; i < filesArray.length(); i++) {
                    JSONObject fileObject = filesArray.getJSONObject(i);
                    String fileName = fileObject.getString("name");
                    long fileSize = fileObject.getLong("size");
                    String fileType = fileObject.getString("type");

                    FileInfo fileInfo = new FileInfo(fileName, fileSize, fileType);
                    files.add(fileInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return files;
        }


        private void handleErrorResponse(String errorMessage) {
            Log.e("FileFetcher", "HTTP Error: " + errorMessage);
            showAlertDialog(errorMessage, "파일을 불러오지 못했습니다.\n앱을 다시 실행해 주세요.");
        }

        private void showAlertDialog(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("확인", null)
                    .show();
        }

        private String handleErrorResponseCode(int responseCode) {
            String errorMessage;
            switch (responseCode) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    errorMessage = "Bad Request";
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    errorMessage = "Unauthorized";
                    break;
                case HttpURLConnection.HTTP_FORBIDDEN:
                    errorMessage = "Forbidden";
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    errorMessage = "Not Found";
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    errorMessage = "Internal Server Error";
                    break;
                default:
                    errorMessage = "Unknown Error";
                    break;
            }
            return errorMessage;
        }
    }
}

