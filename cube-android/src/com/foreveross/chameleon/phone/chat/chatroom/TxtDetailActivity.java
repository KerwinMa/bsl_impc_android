package com.foreveross.chameleon.phone.chat.chatroom;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.phone.chat.image.CropImage;

public class TxtDetailActivity extends Activity implements OnClickListener {

	private TextView txt_detail;
	String txtPath;
	private Button back_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_txt_detail);
		txt_detail = (TextView) findViewById(R.id.txt_detail);
		txtPath = getIntent().getStringExtra("txtPath");
		if (txtPath != null){
			String txt = convertCodeAndGetText(txtPath);
			txt_detail.setText(txt);
		}
		back_btn = (Button) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(this);
	}

	public String convertCodeAndGetText(String str_filepath) {
		File file = new File(str_filepath);
		BufferedReader reader;
		String text = "";
		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fis);
			in.mark(4);
			byte[] first3bytes = new byte[3];
			in.read(first3bytes);
			in.reset();
			if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
					&& first3bytes[2] == (byte) 0xBF) {
				reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFE) {
				reader = new BufferedReader(
						new InputStreamReader(in, "unicode"));
			} else if (first3bytes[0] == (byte) 0xFE
					&& first3bytes[1] == (byte) 0xFF) {
				reader = new BufferedReader(new InputStreamReader(in,
						"utf-16be"));
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFF) {
				reader = new BufferedReader(new InputStreamReader(in,
						"utf-16le"));
			} else {
				reader = new BufferedReader(new InputStreamReader(in, "GBK"));
			}
			String str = reader.readLine();
			while (str != null) {
				text = text + str + "\n";
				str = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;

		default:
			break;
		}
	}

}
