package mingler.andrewlaurien.com.mingler;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mingler.andrewlaurien.com.mingler.org.nikki.omegle.Omegle;
import mingler.andrewlaurien.com.mingler.org.nikki.omegle.core.OmegleException;
import mingler.andrewlaurien.com.mingler.org.nikki.omegle.core.OmegleMode;
import mingler.andrewlaurien.com.mingler.org.nikki.omegle.core.OmegleSession;
import mingler.andrewlaurien.com.mingler.org.nikki.omegle.event.OmegleEventAdaptor;


public class MainActivity extends AppCompatActivity {

    TextView txt;

    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;

    Context mcontext;
    OmegleSession session;
    OmegleChat om;

    private ChatMessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mcontext = this;

        new OmegleChat().execute();

        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (Button) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);


        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }

                try {
                    if (session != null) {
                        session.send(message.trim());
                        //sendMessage(message.trim());
                    }
                } catch (OmegleException e) {
                    e.printStackTrace();
                }


                mEditTextMessage.setText("");
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                new OmegleChat().execute();
                //sendMessage();
            }
        });

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }


    }

    //region Omegle

    private class OmegleChat extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {

            // txt=(TextView)findViewById(R.id.txt);
            // txt.append("Omegele Connection Started... \n");


        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override

        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            OmgeleChatOn();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(mcontext, "You are now connected", Toast.LENGTH_SHORT).show();

        }
    }

    public void OmgeleChatOn() {

        Omegle omegle = new Omegle();
        try {
            System.out.println("Opening session...");

            session = omegle.openSession(OmegleMode.NORMAL, new OmegleEventAdaptor() {

                @Override
                public void chatWaiting(OmegleSession session) {
                    System.out.println("Waiting for chat...");
                }

                @Override
                public void chatConnected(OmegleSession session) {
                    System.out
                            .println("You are now talking to a random stranger!");
                    try {
                        session.send("hi", true);
                        MainActivity.this.session = session;
                    } catch (OmegleException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void chatMessage(OmegleSession session, final String message) {
                    System.out.println("Stranger: " + message);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mimicOtherMessage(message);
                        }

                    });


                }

                @Override
                public void messageSent(OmegleSession session, final String string) {
                    System.out.println("You: " + string);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            sendMessage(string);
                        }

                    });


                }

                @Override
                public void strangerDisconnected(OmegleSession session) {
                    System.out.println("Stranger disconnected, goodbye!");
                    //System.exit(0);
                }

                @Override
                public void omegleError(OmegleSession session, String string) {
                    System.out.println("ERROR! " + string);
                    System.exit(1);
                }


            });


        } catch (OmegleException e) {
            e.printStackTrace();
        }


    }

    //endregion


    //region Message

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

}
