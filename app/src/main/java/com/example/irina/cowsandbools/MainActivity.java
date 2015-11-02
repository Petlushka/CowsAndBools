package com.example.irina.cowsandbools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener{

    Button btnOk;
    EditText etNumb;
    SharedPreferences shPref;
    String generate_number;
    final String SAVE_NUMBER = "Save number";
    final String TAG = "my_number";
    public int cows = 0;
    public int bulls = 0;
    ListView list_item;
    int step = 0;
    List<String> listNumbers = new ArrayList<>();
    ArrayAdapter<String> adapter;
    final int DIALOG_WIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generate_number = generate_number();
        Log.i(TAG, generate_number);
        btnOk = (Button)findViewById(R.id.btnOk);
        etNumb = (EditText)findViewById(R.id.etNumb);
        list_item = (ListView)findViewById(R.id.list_item);

        btnOk.setOnClickListener(this);



    }

    public String generate_number(){
        String new_numb = "";
        Random random = new Random();
        for(int i=0; i < 4; i++) {
            int numb;
            if (i == 0) {
                do {
                    numb = random.nextInt(10);
                }
                while (numb == 0);
                new_numb += numb;
                continue;
            } else {
                String temp;
                do {
                    numb = random.nextInt(10);
                    temp = "" + numb;

                } while (new_numb.contains(temp));
                new_numb += numb;
            }
        }
        return new_numb;
    }

    public boolean checkNumber(String num) {
        boolean result = true;

        if(num == "")
            result = false;

        if(num.length() < 4)
            result = false;

        if(num.charAt(0) == 48)
            result = false;

        for(int i=0; i < 4; i++){
            for(int j=0; j < 4; j++){
                if(i == j)
                    continue;

                if(num.charAt(i) == num.charAt(j))
                    result = false;
            }
            if(generate_number.contains(num.charAt(i)+"")){

                if(generate_number.charAt(i)==num.charAt(i))
                    bulls++;
                 else
                    cows++;
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        adapter = new ArrayAdapter<String>(this, R.layout.my_item_list, listNumbers);
        list_item.setAdapter(adapter);

        shPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();
        editor.putString(SAVE_NUMBER, etNumb.getText().toString());
        editor.commit();
        String playerNumber = shPref.getString(SAVE_NUMBER,"");
        if(checkNumber(playerNumber)){
            if(bulls == 4){
                onCreateDialog(DIALOG_WIN);
            } else {
                listNumbers.add(0, ++step + ". " + playerNumber + " " + cows + " коровы, " + bulls + " быка.");
                adapter.notifyDataSetChanged();
                etNumb.setText("");
                cows =0;
                bulls = 0;
            }
        } else {

            etNumb.setText("");
            Toast.makeText(this, "Введите корректное число", Toast.LENGTH_LONG).show();
            cows =0;
            bulls = 0;
        }
    }

        protected Dialog onCreateDialog(int id){
        if(id == DIALOG_WIN){
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            step++;
            adb.setTitle(R.string.game_over);
            adb.setMessage("Вы угадали загаданное число за " + step + " попыток!");
            adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case Dialog.BUTTON_POSITIVE:
                            resetGame();
                            break;
                    }

                }
            });
            return adb.show();
        }
        return super.onCreateDialog(id);
    }

    public void resetGame(){
        etNumb.setText("");
        generate_number = generate_number();
        cows = 0;
        bulls = 0;
        step = 0;
        listNumbers.clear();
        list_item.setAdapter(null);
    }
}
