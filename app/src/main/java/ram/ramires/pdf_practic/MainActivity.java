package ram.ramires.pdf_practic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ScrollView content_create;
    private LinearLayout pdf_layout;
    private int pdf_width;
    private int pdf_height;
    private Context context;
    private Activity activity;
    private int REQUEST_CODE_PERMISSION=1;
    private File dir;
    private  File filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        activity=this;

        Button btn_create=findViewById(R.id.create);
        Button btn_read=findViewById(R.id.read);
        listView=findViewById(R.id.listView);
        content_create=findViewById(R.id.content_create);
        pdf_layout=findViewById(R.id.ll_pdflayout);


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_create.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                content_create.setVisibility(View.VISIBLE);

                generatePdf();

               /* int permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    generatePdf();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSION);
                }*/
            }
        });
        btn_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* btn_create.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                content_create.setVisibility(View.GONE);*/
                openPdf();

            }
        });
    }


    private void generatePdf() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHeight = (int) height, convertWidth = (int) width;

        // создаем документ
        PdfDocument document = new PdfDocument();
        // определяем размер страницы
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();
        // получаем страницу, на котором будем генерировать контент
        PdfDocument.Page page = document.startPage(pageInfo);

        // получаем холст (Canvas) страницы
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        // получаем контент, который нужно добавить в PDF, и загружаем его в Bitmap
        Bitmap bitmap = loadBitmapFromView(pdf_layout, pdf_layout.getWidth(), pdf_layout.getHeight());
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        // рисуем содержимое и закрываем страницу
        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        //String destPath = this.getExternalFilesDir(null).getAbsolutePath();
        //File dir = new File(destPath + "/PDF");
        dir=this.getExternalFilesDir("pdf");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // сохраняем записанный контент
        String targetPdf = dir.getAbsolutePath() + "/test1.pdf";
        filePath = new File(targetPdf);
        try {
            FileOutputStream fos=new FileOutputStream(filePath,true);
            document.writeTo(fos);
            Toast.makeText(getApplicationContext(), "PDf сохранён в " + filePath.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            // обновляем список
            //initViews();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Что-то пошло не так: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // закрываем документ
        document.close();
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }
    public void openFolder()
    {
        String targetPdf = dir.getAbsolutePath() + "/test1.pdf";
        Uri uri =
                FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider", new File(targetPdf));


        Log.d("path", uri.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        startActivity(intent);
    }

    public void openPdf(){
        String targetPdf = dir.getAbsolutePath() + "/test1.pdf";
        Uri uri =
                FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider", new File(targetPdf));


        Log.d("path", uri.toString());


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent1 = Intent.createChooser(intent, "Open With");

        startActivity(intent1);

        /*
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setDataAndType(uri, "application/pdf");
        Intent chooser = Intent.createChooser(browserIntent, "Выберите приложение");
        Toast.makeText(this, uri.toString() ,Toast.LENGTH_SHORT).show();
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional

        startActivity(chooser);*/
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                generatePdf();
            } else {
                // permission denied
                Toast.makeText(context,"No no no", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }*/
}