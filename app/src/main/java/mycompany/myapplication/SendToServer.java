package mycompany.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by tnash219 on 10/18/2014.
 */
public class SendToServer extends DialogFragment {

    Activity contentActivity = new Activity();

    public static SendToServer newInstance(String host, String port, String message) {
        SendToServer sendToServer = new SendToServer();
        Bundle args = new Bundle();
        args.putString("HOST",host);
        args.putString("PORT",port);
        args.putString("MESSAGE",message);
        sendToServer.setArguments(args);
        return sendToServer;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        contentActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String host = getArguments().getString("HOST");
        final String port = getArguments().getString("PORT");
        final String message = getArguments().getString("MESSAGE");

        if (host.length() > 0 && port.length() > 0 && message.length() > 0) {
            builder.setMessage(R.string.send_to_server_dialog)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new SendMessageTask().execute(host, port, message);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        } else {

            StringBuffer isMissing = new StringBuffer();
            if (host.length() == 0) isMissing.append("host/");
            if (port.length() == 0) isMissing.append("port/");
            if (message.length() == 0) isMissing.append("message/");
            isMissing.deleteCharAt(isMissing.length() - 1);
            isMissing.append(" missing");


            builder.setMessage("Cannot send to server:\n" + isMissing)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        }


        // Create the AlertDialog object and return it
        return builder.create();
    }

    public class SendMessageTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... hostPortMessage) {

            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(hostPortMessage[0], Integer.parseInt(hostPortMessage[1])), 5000);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(hostPortMessage[2]);
                in.readLine();
                out.flush();
                out.close();
                socket.close();
            } catch (UnknownHostException e) {
                return "Host \"" + hostPortMessage[0] + ":" + hostPortMessage[1] + "\" not reachable";
                //System.exit(1);
            } catch (IOException e) {
                return "I/O operation failed: " + e;
                //System.exit(1);
            } catch(IllegalArgumentException e) {
                return "Illegal argument: " + e;
            }
                return "Message sent to host \"" + hostPortMessage[0] + ":" + hostPortMessage[1] +  "\"\n";
        }

        @Override
        protected void onPostExecute(String response) {
            if(contentActivity != null) {
                Toast.makeText(contentActivity, response, Toast.LENGTH_LONG).show();
            }
            else System.out.println(response);
        }


    }
}