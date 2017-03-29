package br.com.mob.notificationc4;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

public class CadastroActivity extends AppCompatActivity {

    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Button btn_salvar = (Button) findViewById(R.id.btn_salvar);

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationLoad(v);
                notificationSave(v);
                finish();
            }
        });
    }

    public void notificationSave(View view) {
        EditText txt_nome = (EditText) findViewById(R.id.txt_nome);
        EditText txt_telefone = (EditText) findViewById(R.id.txt_telefone);
        EditText txt_email = (EditText) findViewById(R.id.txt_email);

        Intent viewIntent = new Intent(this, ViewActivity.class);
        viewIntent.putExtra("nome", txt_nome.getText().toString());
        viewIntent.putExtra("telefone", txt_telefone.getText().toString());
        viewIntent.putExtra("email", txt_email.getText().toString());
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ViewActivity.class);
        stackBuilder.addNextIntent(viewIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_people_white_48dp)
                .setContentTitle("Informações salvas!")
                .setContentText("Clique aqui para exibir os detalhes")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker("Hello");

        Notification notification = builder.build();

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_custom);

        final String text = getResources().getString(R.string.sucesso);
        contentView.setTextViewText(R.id.textView, text);
        notification.contentView = contentView;

        if (Build.VERSION.SDK_INT >= 16) {
            RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_extended);

            final String textExtended = getResources().getString(R.string.notification_ex);
            expandedView.setTextViewText(R.id.textExtended, textExtended);
            notification.bigContentView = expandedView;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void notificationLoad(View view) {
        final NotificationManager notifyManager;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];

        notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder buider = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_backup_white_18dp)
                .setContentTitle("Enviando informações")
                .setContentText("Enviando as informações para o servidor remoto, você pode continuar utilizando o aplicativo!");

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int increase;

                        for (increase = 0; increase <= 100; increase += 5) {
                            buider.setProgress(100, increase, false);
                            notifyManager.notify(2, buider.build());

                            try {
                                Thread.sleep(5 * 200);
                            } catch (InterruptedException e) {
                                Log.d("", "Deu ruim");
                            }
                        }

                        buider.setContentText("Envio concluído!").setProgress(0, 0, false);
                        notifyManager.notify(2, buider.build());
                    }
                }
        ).start();
    }
}
