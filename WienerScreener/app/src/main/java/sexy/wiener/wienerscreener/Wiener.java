/**
 * Wiener.java
 * @author Richard Red
 *
 * The front-end Android application that communicates with the cloud-based server
 * to determine whether or not a picture that is taken in the app is a hot dog.
 *
 * This code is awful.
 */
package sexy.wiener.wienerscreener;

 import android.app.Activity;
 import android.content.Intent;
 import android.graphics.Bitmap;
 import android.icu.util.Output;
 import android.net.Uri;
 import android.os.AsyncTask;
 import android.os.Bundle;
 import android.provider.MediaStore;
 import android.support.v4.content.FileProvider;
 import android.support.v7.app.AppCompatActivity;
 import android.util.Log;
 import android.view.View;
 import android.widget.Button;
 import android.widget.ImageView;

 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;

public class Wiener extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

     private ImageView wienerView;
     private int imageRequestId = 0;

     private Uri[] imageUris = new Uri[256];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiener);
        this.wienerView = (ImageView)this.findViewById(this.getResources().getIdentifier("wienerView", "id", this.getApplicationContext().getPackageName()));
    }

    public void onWienerButtonClick(View v) {
        Log.i("Wiener", "Scanning for wieners");
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        try {
            File tmp = makeTempImageFile();
            takePicture(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] fullyReadInputStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];

        int n;
        while ((n = in.read(buf)) > 0) {
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }

    private void detectWiener(Uri wienerUri) throws IOException {
        InputStream in = new BufferedInputStream(getContentResolver().openInputStream(wienerUri));

        byte[] inBytes = fullyReadInputStream(in);

        in.close();

        URL serverUrl = new URL("http://35.231.68.240/testwiener");
        HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("content-type", "application/octet-stream");
        conn.setRequestProperty("Content-Length", inBytes.length + "");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();


        OutputStream servOut = new BufferedOutputStream(conn.getOutputStream());
        servOut.write(inBytes);
        servOut.flush();
        servOut.close();

        InputStream servIn = new BufferedInputStream(conn.getInputStream());
        final File responseImageFile = makeTempImageFile();
        OutputStream wienerOut = new BufferedOutputStream(new FileOutputStream(responseImageFile));

        byte[] buf = new byte[4096];
        int n;
        try {
            while ((n = servIn.read(buf)) > 0) {
                wienerOut.write(buf, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        wienerOut.flush();
        wienerOut.close();
        conn.disconnect();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wienerView.setImageURI(Uri.fromFile(responseImageFile));
            }
        });
    }

     private File makeTempImageFile() throws IOException {
         File out = this.getApplicationContext().getCacheDir();
         File wienerFolder = new File(out, "wieners");
         if (!wienerFolder.exists()) {
             wienerFolder.mkdir();
         }
         return File.createTempFile("wiener", ".jpg", wienerFolder);
     }

     private void takePicture(File pic) {
         Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
             int req = imageRequestId;
             imageRequestId = (imageRequestId + 1) % 256;
             imageUris[req] = FileProvider.getUriForFile(this, "sexy.wiener.fileprovider", pic);
             takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUris[req]);
             startActivityForResult(takePictureIntent, req | 0x100);
         }
     }

     private void setViewPicture(Uri uri) {
         wienerView.setImageURI(uri);
     }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (((requestCode & 0x100) == 0x100) && resultCode == RESULT_OK) {
            final Uri pic = imageUris[requestCode & 0xFF];
            setViewPicture(pic);
            (new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        detectWiener(pic);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }).execute();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE){
            Log.e("Wiener", "ERROR: " + resultCode);
        }
    }
}
